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
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;
import net.codecrete.qrbill.web.model.ValidationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

/**
 * Unit tests for bill data validation API
 */
@QuarkusTest
@DisplayName("Bill data validation")
class ValidationTests {

    @Test
    void validBill() {
        QrBill bill = SampleData.createBill1();

        Response res = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/validated")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        ValidationResponse response = JsonHelper.extract(res, ValidationResponse.class);

        assertThat(response, notNullValue());
        assertThat(response.getValid(), equalTo(true));
        assertThat(response.getValidationMessages(), nullValue());
        assertThat(response.getValidatedBill(), notNullValue());
        assertThat(response.getValidatedBill(), equalTo(bill));
        assertThat(response.getBillID(), notNullValue());
        assertThat(response.getBillID().length(), greaterThan(100));
        assertThat(response.getQrCodeText(), notNullValue());
        assertThat(response.getQrCodeText().length(), greaterThan(100));
    }

    @Test
    void truncationWarning() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown("city56789012345678901234567890123456");

        Response res = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/validated")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        ValidationResponse response = JsonHelper.extract(res, ValidationResponse.class);

        assertThat(response, notNullValue());
        assertThat(response.getValid(), equalTo(true));
        assertThat(response.getValidationMessages(), notNullValue());
        assertThat(response.getValidationMessages().size(), equalTo(1));
        assertThat(response.getValidationMessages().get(0).getType(), equalTo(ValidationMessage.TypeEnum.WARNING));
        assertThat(response.getValidationMessages().get(0).getField(), equalTo("creditor.town"));
        assertThat(response.getValidationMessages().get(0).getMessageKey(), equalTo("field_clipped"));

        bill.getCreditor().setTown("city5678901234567890123456789012345");
        assertThat(response.getValidatedBill(), notNullValue());
        assertThat(response.getValidatedBill(), equalTo(bill));

        assertThat(response.getBillID(), notNullValue());
        assertThat(response.getBillID().length(), greaterThan(100));
        assertThat(response.getQrCodeText(), notNullValue());
        assertThat(response.getQrCodeText().length(), greaterThan(100));
    }

    @Test
    void missingCreditorError() {
        QrBill bill = SampleData.createBill1();
        bill.setCreditor(null);

        Response res = given()
                .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/validated")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        ValidationResponse response = JsonHelper.extract(res, ValidationResponse.class);

        assertThat(response, notNullValue());
        assertThat(response.getValid(), equalTo(false));
        assertThat(response.getValidationMessages(), notNullValue());
        assertThat(response.getValidationMessages().size(), equalTo(5));

        for (ValidationMessage message : response.getValidationMessages()) {
            assertThat(message.getType(), equalTo(ValidationMessage.TypeEnum.ERROR));
            assertThat(message.getField(), startsWith("creditor."));
            assertThat(message.getMessageKey(), equalTo("field_is_mandatory"));
        }

        assertThat(response.getBillID(), nullValue());
        assertThat(response.getQrCodeText(), nullValue());
    }
}
