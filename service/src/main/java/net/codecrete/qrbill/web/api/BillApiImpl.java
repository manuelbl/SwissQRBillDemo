//
// Swiss QR Bill Generator
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.codecrete.qrbill.generator.*;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.QrCodeInformation;
import net.codecrete.qrbill.web.model.ValidationMessage;
import net.codecrete.qrbill.web.model.ValidationResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class BillApiImpl implements BillApi {

    private final MessageLocalizer messageLocalizer;

    private final HttpHeaders httpHeaders;

    public BillApiImpl(MessageLocalizer messageLocalizer, HttpHeaders httpHeaders) {
        this.messageLocalizer = messageLocalizer;
        this.httpHeaders = httpHeaders;
    }

    @Override
    public ValidationResponse decodeQRCode(QrCodeInformation qrCodeInformation) {
        ValidationResult result;
        try {
            Bill bill = QRBill.decodeQrCodeText(qrCodeInformation.getText());
            result = QRBill.validate(bill);
        } catch (QRBillValidationError e) {
            result = e.getValidationResult();
        }
        return createValidationResponse(result);
    }

    @Override
    public Response generateBill(QrBill qrBill) {
        Bill bill = DtoConverter.fromDtoQrBill(qrBill);
        setFormatDefaults(bill);
        return generateImage(bill);
    }

    @Override
    public Response getBillImage(String billID, String outputSize, String graphicsFormat) {
        Bill bill;
        try {
            bill = BillId.decode(billID);
            if (bill == null)
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid bill ID. Validate bill data to get a valid ID")
                        .build();
            setFormatDefaults(bill);

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid bill ID. Validate bill data to get a valid ID")
                    .build();
        }

        if (outputSize != null)
            bill.getFormat().setOutputSize(getOutputSize(outputSize));
        if (graphicsFormat != null)
            bill.getFormat().setGraphicsFormat(getGraphicsFormat(graphicsFormat));
        return generateImage(bill);
    }

    private Response generateImage(Bill bill) {
        updateForAdviceOnly(bill);

        byte[] result = QRBill.generate(bill);
        MediaType contentType = getContentType(bill.getFormat().getGraphicsFormat());
        return Response.ok(result, contentType).build();
    }

    @Override
    public ValidationResponse validateBill(QrBill qrBill) {
        ValidationResult result = QRBill.validate(DtoConverter.fromDtoQrBill(qrBill));
        return createValidationResponse(result);
    }

    private void updateForAdviceOnly(Bill bill) {
        if (bill.getAmount() == null || BigDecimal.ZERO.compareTo(bill.getAmount()) != 0)
            return;

        bill.setUnstructuredMessage(
                MultilingualText.getText(MultilingualText.KEY_DO_NOT_USE_FOR_PAYMENT, bill.getFormat().getLanguage()));
    }

    private ValidationResponse createValidationResponse(ValidationResult result) {
        // Get validated data
        Bill validatedBill = result.getCleanedBill();

        ValidationResponse response = new ValidationResponse();
        response.setValid(result.isValid());

        // Generate localized messages
        if (result.hasMessages()) {
            List<ValidationMessage> messages
                    = DtoConverter.toDtoValidationMessageList(result.getValidationMessages());
            messageLocalizer.translateMessages(messages, httpHeaders);
            response.setValidationMessages(messages);
        } else {
            // backward compatibility
            response.setValidationMessages(null);
        }
        response.setValidatedBill(DtoConverter.toDtoQrBill(validatedBill));

        // generate QR code text and bill ID
        if (!result.hasErrors()) {
            String qrCodeText = QRBill.encodeQrCodeText(validatedBill);
            response.setQrCodeText(qrCodeText);
            response.setBillID(BillId.generate(qrCodeText, DtoConverter.toDtoBillFormat(validatedBill.getFormat())));
        }

        return response;
    }

    private void setFormatDefaults(Bill bill) {
        net.codecrete.qrbill.generator.BillFormat format = bill.getFormat();
        OutputSize outputSize = null;
        Language language = null;
        SeparatorType separatorType = null;
        GraphicsFormat graphicsFormat = null;
        String fontFamily = null;

        if (format != null) {
            outputSize = format.getOutputSize();
            language = format.getLanguage();
            separatorType = format.getSeparatorType();
            graphicsFormat = format.getGraphicsFormat();
            fontFamily = format.getFontFamily();
        }

        if (outputSize == null)
            outputSize = OutputSize.A4_PORTRAIT_SHEET;
        if (language == null)
            language = languageFromRequestHeader();
        if (language == null)
            language = Language.EN;
        if (separatorType == null)
            separatorType = SeparatorType.SOLID_LINE_WITH_SCISSORS;
        if (fontFamily == null)
            fontFamily = "Helvetica,Arial,\"Liberation Sans\"";

        if (graphicsFormat == null)
            graphicsFormat = graphicsFormatFromRequestHeader();
        if (graphicsFormat == null)
            graphicsFormat = GraphicsFormat.SVG;

        if (format == null) {
            format = new net.codecrete.qrbill.generator.BillFormat();
            bill.setFormat(format);
        }
        format.setOutputSize(outputSize);
        format.setLanguage(language);
        format.setSeparatorType(separatorType);
        format.setFontFamily(fontFamily);
        format.setGraphicsFormat(graphicsFormat);
    }

    private static OutputSize getOutputSize(String value) {
        return switch (value) {
            case "a4-portrait-sheet" -> OutputSize.A4_PORTRAIT_SHEET;
            case "qr-bill-only" -> OutputSize.QR_BILL_ONLY;
            case "qr-bill-with-horizontal-line" -> OutputSize.QR_BILL_EXTRA_SPACE;
            case "qr-code-only" -> OutputSize.QR_CODE_ONLY;
            default -> null;
        };
    }

    private static GraphicsFormat getGraphicsFormat(String value) {
        return switch (value) {
            case "svg" -> GraphicsFormat.SVG;
            case "pdf" -> GraphicsFormat.PDF;
            case "png" -> GraphicsFormat.PNG;
            default -> null;
        };
    }

    private static final MediaType MEDIA_TYPE_APPLICATION_PDF = new MediaType("application", "pdf");
    private static final MediaType MEDIA_TYPE_IMAGE_SVG = new MediaType("image", "svg+xml");

    private GraphicsFormat graphicsFormatFromRequestHeader() {
        for (MediaType mediaType : httpHeaders.getAcceptableMediaTypes()) {
            if (mediaType.isCompatible(MEDIA_TYPE_IMAGE_SVG))
                return GraphicsFormat.SVG;
            if (mediaType.isCompatible(MEDIA_TYPE_APPLICATION_PDF))
                return GraphicsFormat.PDF;
        }

        return null;
    }

    private static MediaType getContentType(GraphicsFormat graphicsFormat) {
        return graphicsFormat == GraphicsFormat.SVG ? MEDIA_TYPE_IMAGE_SVG : MEDIA_TYPE_APPLICATION_PDF;
    }

    private Language languageFromRequestHeader() {

        for (Locale locale : httpHeaders.getAcceptableLanguages()) {
            String language = locale.getLanguage();
            if ("en".equals(language))
                return Language.EN;
            if ("de".equals(language))
                return Language.DE;
            if ("fr".equals(language))
                return Language.FR;
            if ("it".equals(language))
                return Language.IT;
            if ("rm".equals(language))
                return Language.RM;
        }

        return null;
    }

    @ServerExceptionMapper
    public Response mapException(QRBillValidationError ex) {
        List<ValidationMessage> messages
                = DtoConverter.toDtoValidationMessageList(ex.getValidationResult().getValidationMessages());
        messageLocalizer.translateMessages(messages, httpHeaders);
        return Response
            .status(422, "Unprocessable Entity")
            .entity(messages)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
