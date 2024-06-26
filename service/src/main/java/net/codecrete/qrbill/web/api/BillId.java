//
// Swiss QR Bill Generator
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.web.model.BillFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * A bill ID is a string encapsulating the entire QR code contents plus formatting information.
 * <p>
 *     It is used to create self-contained URIs for QR bill images.
 * </p>
 * <p>
 *     The ID is the Base 64 (URL safe version) of the compressed (deflate) JSON
 *     data consisting of version, language and the text string would be embedded in
 *     the QR code.
 * </p>
 * <p>
 *     The ID is made URL safe by using the URL-safe RFC4648 Base 64 encoding and
 *    replacing all equal signs (=) with tildes (~).
 * </p>
 */
public class BillId {
    private BillId() {}

    /**
     * Generates an ID that encodes the entire bill data.
     *
     * @param qrCodeText the QR code text
     * @param billFormat the billFormat
     * @return the generated ID
     */
    @SuppressWarnings("java:S112")
    static String generate(String qrCodeText, BillFormat billFormat) {

        BillPayload payload = new BillPayload();
        payload.setVersion(1);
        payload.setFormat(billFormat);
        payload.setQrText(qrCodeText);

        Base64.Encoder base64 = Base64.getUrlEncoder();
        byte[] encodedData;
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             OutputStream intermediate = base64.wrap(buffer);
             DeflaterOutputStream head = new DeflaterOutputStream(intermediate)) {

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(head, payload);
            head.flush();
            encodedData = buffer.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String id = new String(encodedData, StandardCharsets.US_ASCII);
        return id.replace('=', '~');
    }

    /**
     * Decodes a bill ID and returns the bill data
     * <p>
     * The bill ID is assumed to have been generated by
     * {@link #generate(String, BillFormat)}.
     * </p>
     *
     * @param id the ID
     * @return the bill data
     */
    static Bill decode(String id) {

        id = id.replace('~', '=');
        byte[] encodedData = id.getBytes(StandardCharsets.US_ASCII);

        Base64.Decoder base64 = Base64.getUrlDecoder();
        BillPayload payload;
        try (InputStream dataStream = new ByteArrayInputStream(encodedData);
             InputStream intermediate = base64.wrap(dataStream);
             InflaterInputStream head = new InflaterInputStream(intermediate)) {

            ObjectMapper mapper = new ObjectMapper();
            payload = mapper.readValue(head, BillPayload.class);

        } catch (Exception e) {
            return null; // invalid ID
        }

        Bill bill = QRBill.decodeQrCodeText(payload.getQrText());
        bill.setFormat(DtoConverter.fromDtoBillFormat(payload.getFormat()));
        return bill;
    }
}
