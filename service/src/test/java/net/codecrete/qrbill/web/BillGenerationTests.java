//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.codecrete.qrbill.web.model.BillFormat;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static net.codecrete.qrbill.web.TestHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for QR bill generation API (PDF and SVG)
 */
@QuarkusTest
@DisplayName("QR bill generation")
class BillGenerationTests {

    @Test
    void svgQrBill() {
        QrBill bill = SampleData.createBill1();
        var xml = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .spec(SVG)
                .extract().asString();

        assertThat(xml)
                .is(svg())
                .contains("Meierhans AG");
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
                .spec(PDF)
                .extract().asByteArray();

        assertThat(result).is(pdf());
    }

    @Test
    void svgWithTruncatedTown() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown("city56789012345678901234567890123456");

        var xml = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .spec(SVG)
                .extract().asString();

        assertThat(xml)
                .is(svg())
                .contains("font-size=\"10\">2100 city5678901234567890123456789012345</text>");
    }

    @Test
    void oneValidationError() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown(null);

        var messages = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .statusCode(422)
                .contentType("application/json")
                .extract().as(ValidationMessage[].class);

        assertThat(messages)
                .isNotNull()
                .hasSize(1);
        assertThat(messages[0].getType()).isEqualTo(ValidationMessage.TypeEnum.ERROR);
        assertThat(messages[0].getField()).isEqualTo("creditor.town");
        assertThat(messages[0].getMessageKey()).isEqualTo("field_value_missing");
    }

    @Test
    void languageFromHeaderIT() {
        testLanguageFromHeader("it-CH", "Sezione pagamento");
    }

    @Test
    void languageFromHeaderDE() {
        testLanguageFromHeader("zh, de", "Zahlteil");
    }

    @Test
    void languageFromHeaderFR() {
        testLanguageFromHeader("fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5", "Section paiement");
    }

    @Test
    void languageFromHeaderRM() {
        testLanguageFromHeader("rm-CH, fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5", "Part da pajament");
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

        var xml = given()
            .when()
                .contentType(ContentType.JSON)
                .header("Accept-Language", language)
                .body(bill)
                .post("/bill/image")
            .then()
                .spec(SVG)
                .extract().asString();

        assertThat(xml)
                .is(svg())
                .contains(textFragment);
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
                .spec(PDF)
                .extract().asByteArray();

        assertThat(result).is(pdf());
    }

    @Test
    void testDefault() {
        QrBill bill = SampleData.createBill1();
        bill.setFormat(null);

        var xml = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/image")
            .then()
                .spec(SVG)
                .extract().asString();

        assertThat(xml)
                .is(svg())
                .contains("Payment part");
    }
}
