//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;
import net.codecrete.qrbill.web.model.ValidationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for bill data validation API
 */
@QuarkusTest
@DisplayName("Bill data validation")
class ValidationTests {

    @Test
    void validBill() {
        QrBill bill = SampleData.createBill1();

        var response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/validated")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ValidationResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getValid()).isTrue();
        assertThat(response.getValidationMessages()).isNull();
        assertThat(response.getValidatedBill()).isNotNull();
        assertThat(response.getValidatedBill()).isEqualTo(bill);
        assertThat(response.getBillID()).isNotNull();
        assertThat(response.getBillID().length()).isGreaterThan(100);
        assertThat(response.getQrCodeText()).isNotNull();
        assertThat(response.getQrCodeText().length()).isGreaterThan(100);
    }

    @Test
    void truncationWarning() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown("city56789012345678901234567890123456");

        var response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/validated")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ValidationResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getValid()).isTrue();
        assertThat(response.getValidationMessages())
                .isNotNull()
                .hasSize(1);
        assertThat(response.getValidationMessages().getFirst()).satisfies(m -> {
            assertThat(m.getType()).isEqualTo(ValidationMessage.TypeEnum.WARNING);
            assertThat(m.getField()).isEqualTo("creditor.town");
            assertThat(m.getMessageKey()).isEqualTo("field_value_clipped");
        });

        bill.getCreditor().setTown("city5678901234567890123456789012345");
        assertThat(response.getValidatedBill()).isEqualTo(bill);

        assertThat(response.getBillID()).isNotNull();
        assertThat(response.getBillID().length()).isGreaterThan(100);
        assertThat(response.getQrCodeText()).isNotNull();
        assertThat(response.getQrCodeText().length()).isGreaterThan(100);
    }

    @Test
    void missingCreditorError() {
        QrBill bill = SampleData.createBill1();
        bill.setCreditor(null);

        var response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/validated")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ValidationResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getValid()).isFalse();
        assertThat(response.getValidationMessages())
                .isNotNull()
                .hasSize(5);

        for (ValidationMessage message : response.getValidationMessages()) {
            assertThat(message.getType()).isEqualTo(ValidationMessage.TypeEnum.ERROR);
            assertThat(message.getField()).startsWith("creditor.");
            assertThat(message.getMessageKey()).isEqualTo("field_value_missing");
        }

        assertThat(response.getBillID()).isNull();
        assertThat(response.getQrCodeText()).isNull();
    }
}
