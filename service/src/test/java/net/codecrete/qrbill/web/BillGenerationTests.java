//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.codecrete.qrbill.web.model.BillFormat;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

/**
 * Unit test for QR bill generation API (PDF and SVG)
 */
@QuarkusTest
@DisplayName("QR bill generation")
class BillGenerationTests {

    @Test
    void svgQrBill() {
        QrBill bill = SampleData.createBill1();
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .statusCode(200)
                .contentType("image/svg+xml")
                .body(startsWith("<?xml"))
                .body(containsString("<svg"))
                .body(containsString("Meierhans AG"));
    }

    @Test
    void pdfQrBill() {
        QrBill bill = SampleData.createBill1();
        bill.getFormat().setGraphicsFormat(BillFormat.GraphicsFormatEnum.PDF);

        byte[] result = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .statusCode(200)
                .contentType("application/pdf")
                .extract().asByteArray();

        assertThat(result.length, greaterThan(1000));
        String text = new String(result, 0, 8, StandardCharsets.UTF_8);
        assertThat(text, equalTo("%PDF-1.4"));
    }

    @Test
    void svgWithTruncatedTown() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown("city56789012345678901234567890123456");

        given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .statusCode(200)
                .contentType("image/svg+xml")
                .body(startsWith("<?xml"))
                .body(containsString("<svg"))
                .body(containsString("font-size=\"10\">2100 city5678901234567890123456789012345</text>"));
    }

    @Test
    void oneValidationError() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown(null);

        Response response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .statusCode(422)
                .contentType("application/json")
                .extract().response();

        ValidationMessage[] messages = JsonHelper.extract(response, ValidationMessage[].class);

        assertThat(messages, notNullValue());
        assertThat(messages.length, equalTo(1));
        assertThat(messages[0].getType(), equalTo(ValidationMessage.TypeEnum.ERROR));
        assertThat(messages[0].getField(), equalTo("creditor.town"));
        assertThat(messages[0].getMessageKey(), equalTo("field_is_mandatory"));
    }

    @Test
    void languageFromHeaderIT() {
        testLanguageFromHeader("it-CH", "Sezione di pagamento");
    }

    @Test
    void languageFromHeaderDE() {
        testLanguageFromHeader("zh, de", "Zahlteil");
    }

    @Test
    void languageFromHeaderFR() {
        testLanguageFromHeader("fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5", "Section de paiement");
    }

    @Test
    void languageFromHeaderEN() {
        testLanguageFromHeader("en-US, en", "Payment part");
    }

    @Test
    void languageFromHeaderDefault() {
        testLanguageFromHeader("", "Payment part");
    }

    private void testLanguageFromHeader(String language, String textFragment) {
        QrBill bill = SampleData.createBill1();
        bill.setFormat(null);

        given()
            .when()
                .contentType(ContentType.JSON)
                .header("Accept-Language", language)
                .body(bill)
                .post("/bill/image")
            .then()
                .statusCode(200)
                .contentType("image/svg+xml")
                .body(startsWith("<?xml"))
                .body(containsString("<svg"))
                .body(containsString(textFragment));
    }

    @Test
    void graphicsFormatFromHeader() {
        QrBill bill = SampleData.createBill1();
        bill.setFormat(null);

        byte[] result = given()
            .when()
                .contentType(ContentType.JSON)
                .header("Accept", "text/plain, application/pdf")
                .body(bill)
                .post("/bill/image")
            .then()
                .statusCode(200)
                .contentType("application/pdf")
                .extract().asByteArray();

        assertThat(result.length, greaterThan(1000));
        String text = new String(result, 0, 8, StandardCharsets.UTF_8);
        assertThat(text, equalTo("%PDF-1.4"));
    }

    @Test
    void testDefault() {
        QrBill bill = SampleData.createBill1();
        bill.setFormat(null);

        given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .statusCode(200)
                .contentType("image/svg+xml")
                .body(startsWith("<?xml"))
                .body(containsString("<svg"))
                .body(containsString("Payment part"));
    }
}
