//
// Swiss QR Bill Generator
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.MultilingualText;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.SeparatorType;
import net.codecrete.qrbill.generator.ValidationResult;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.QrCodeInformation;
import net.codecrete.qrbill.web.model.ValidationMessage;
import net.codecrete.qrbill.web.model.ValidationResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
@Path("/bill")
public class BillApi {

    @Inject
    MessageLocalizer messageLocalizer;

    @POST
    @Path("/qrdata")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response decodeQRCode(@NotNull @Valid QrCodeInformation qrCodeInformation, @Context HttpHeaders headers) {
        ValidationResult result;
        try {
            Bill bill = QRBill.decodeQrCodeText(qrCodeInformation.getText());
            result = QRBill.validate(bill);
        } catch (QRBillValidationError e) {
            result = e.getValidationResult();
        }
        return Response.ok(createValidationResponse(result, headers)).build();
    }

    @POST
    @Path("/image")
    @Consumes({"application/json"})
    @Produces({"image/svg+xml", "application/pdf", "application/json"})
    public Response generateBill(@NotNull @Valid QrBill qrBill, @Context HttpHeaders headers) {
        Bill bill = DtoConverter.fromDtoQrBill(qrBill);
        setFormatDefaults(bill, headers);
        return generateImage(headers, bill);
    }

    @GET
    @Path("/image/{billID}")
    @Produces({"image/svg+xml", "application/pdf", "application/json"})
    public Response getBillImage(@PathParam("billID") String billID, @QueryParam("outputSize") String outputSize,
                                 @QueryParam("graphicsFormat") String graphicsFormat, @Context HttpHeaders headers) {
        Bill bill;
        try {
            bill = BillId.decode(billID);
            if (bill == null)
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid bill ID. Validate bill data to get a valid ID").build();
            setFormatDefaults(bill, headers);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid bill ID. Validate bill data to get a valid ID").build();
        }

        if (outputSize != null)
            bill.getFormat().setOutputSize(getOutputSize(outputSize));
        if (graphicsFormat != null)
            bill.getFormat().setGraphicsFormat(getGraphicsFormat(graphicsFormat));
        return generateImage(headers, bill);
    }

    private Response generateImage(HttpHeaders headers, Bill bill) {
        updateForAdviceOnly(bill);

        try {
            byte[] result = QRBill.generate(bill);
            MediaType contentType = getContentType(bill.getFormat().getGraphicsFormat());
            return Response.ok(result, contentType).build();
        } catch (QRBillValidationError ex) {
            return validationErrorResponse(ex, headers);
        }
    }

    @POST
    @Path("/validated")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response validateBill(@NotNull @Valid QrBill qrBill, @Context HttpHeaders headers) {
        ValidationResult result = QRBill.validate(DtoConverter.fromDtoQrBill(qrBill));
        return Response.ok(createValidationResponse(result, headers)).build();
    }

    private void updateForAdviceOnly(Bill bill) {
        if (bill.getAmount() == null || BigDecimal.ZERO.compareTo(bill.getAmount()) != 0)
            return;

        bill.setUnstructuredMessage(
                MultilingualText.getText(MultilingualText.KEY_DO_NOT_USE_FOR_PAYMENT, bill.getFormat().getLanguage()));
    }

    private ValidationResponse createValidationResponse(ValidationResult result, HttpHeaders headers) {
        // Get validated data
        Bill validatedBill = result.getCleanedBill();

        ValidationResponse response = new ValidationResponse();
        response.setValid(result.isValid());

        // Generate localized messages
        if (result.hasMessages()) {
            List<ValidationMessage> messages
                    = DtoConverter.toDtoValidationMessageList(result.getValidationMessages());
            messageLocalizer.translateMessages(messages, headers);
            response.setValidationMessages(messages);
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

    private void setFormatDefaults(Bill bill, HttpHeaders headers) {
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
            language = languageFromRequestHeader(headers);
        if (language == null)
            language = Language.EN;
        if (separatorType == null)
            separatorType = SeparatorType.SOLID_LINE_WITH_SCISSORS;
        if (fontFamily == null)
            fontFamily = "Helvetica,Arial,\"Liberation Sans\"";

        if (graphicsFormat == null)
            graphicsFormat = graphicsFormatFromRequestHeader(headers);
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
        OutputSize outputSize;
        switch (value) {
            case "a4-portrait-sheet":
                outputSize = OutputSize.A4_PORTRAIT_SHEET;
                break;
            case "qr-bill-only":
                outputSize = OutputSize.QR_BILL_ONLY;
                break;
            case "qr-bill-with-horizontal-line":
                outputSize = OutputSize.QR_BILL_WITH_HORIZONTAL_LINE;
                break;
            case "qr-code-only":
                outputSize = OutputSize.QR_CODE_ONLY;
                break;
            default:
                outputSize = null;
        }
        return outputSize;
    }

    private static GraphicsFormat getGraphicsFormat(String value) {
        GraphicsFormat graphicsFormat;
        switch (value) {
            case "svg":
                graphicsFormat = GraphicsFormat.SVG;
                break;
            case "pdf":
                graphicsFormat = GraphicsFormat.PDF;
                break;
            case "png":
                graphicsFormat = GraphicsFormat.PNG;
                break;
            default:
                graphicsFormat = null;
        }
        return graphicsFormat;
    }

    private static final MediaType MEDIA_TYPE_APPLICATION_PDF = new MediaType("application", "pdf");
    private static final MediaType MEDIA_TYPE_IMAGE_SVG = new MediaType("image", "svg+xml");

    private GraphicsFormat graphicsFormatFromRequestHeader(HttpHeaders headers) {
        for (MediaType mediaType : headers.getAcceptableMediaTypes()) {
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

    private Language languageFromRequestHeader(HttpHeaders headers) {

        for (Locale locale : headers.getAcceptableLanguages()) {
            String language = locale.getLanguage();
            if ("en".equals(language))
                return Language.EN;
            if ("de".equals(language))
                return Language.DE;
            if ("fr".equals(language))
                return Language.FR;
            if ("it".equals(language))
                return Language.IT;
        }

        return null;
    }

    private Response validationErrorResponse(QRBillValidationError ex, HttpHeaders headers) {
        List<ValidationMessage> messages
                = DtoConverter.toDtoValidationMessageList(ex.getValidationResult().getValidationMessages());
        messageLocalizer.translateMessages(messages, headers);
        return Response
            .status(422, "Unprocessable Entity")
            .entity(messages)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

}
