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
import net.codecrete.qrbill.web.model.ValidationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

/**
 * Unit test for retrieving a bill by ID (API test)
 */
@QuarkusTest
@DisplayName("SVG bill from ID")
class BillWithIdTests {

    private static final String VALID_BILL_ID =
            "eJxdT81OwzAMfpXI50Zqe0C010K1AxoTmTjlYrqQWUqdkqSDddq7kw5Osw-2_P3JFziZEMkztFUBnz6MmKC9gEO2M1oDLVCCAmzA6UhD7P8ZPDtXgJ_TNCdFy8r7CvKDnJOe3TkropkwYPJhf55WOHpHB-mIjfymdJRxoBh9iLDGcupxpKy7GV-LbLY3PzkH1K7TXNZlqbnS3G2qpmzKWz3kWT_WVaNZZST4wYg3HyMKdaJlMQGFFMos-TkjDsYJ5VPKJDaBNL8TCnQORYfjhJZRczZaPTW_zBbZr2ma77vb9Pen7ev2b3nePWmG6y8FaXYA";

    @Test
    void validateAndRetrieveBill() {
        QrBill bill = SampleData.createBill1();
        testImage(bill, "Meierhans AG");
    }

    @Test
    void validateAndRetrieveBillWithDefaultFormat() {
        QrBill bill = SampleData.createBill2();
        testImage(bill, "Kramer");
    }

    @Test
    void validateAndRetrieveBillWithNullFormatValues() {
        QrBill bill = SampleData.createBill2();
        bill.getFormat().setGraphicsFormat(null);
        bill.getFormat().setOutputSize(null);
        bill.getFormat().setLanguage(null);
        bill.getFormat().setFontFamily(null);
        bill.getFormat().setSeparatorType(null);

        testImage(bill, "Kramer");
    }

    void testImage(QrBill bill, String textInSvg) {
        // Validate bill data to retrieve bill ID
        Response res = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/validated")
            .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        ValidationResponse response = JsonHelper.extract(res, ValidationResponse.class);
        assertThat(response, notNullValue());
        assertThat(response.getBillID(), notNullValue());

        // Generate and validated image from bill ID
        given()
            .when()
                .get("/bill/image/{billId}", response.getBillID())
            .then()
                .statusCode(200)
                .contentType("image/svg+xml")
                .body(startsWith("<?xml"))
                .body(containsString("<svg"))
                .body(containsString(textInSvg));
    }

    @Test
    void retrieveBillOverrideOutputSize() {
        given()
                .queryParam("outputSize", "qr-code-only")
                .when()
                .get("/bill/image/{billId}", VALID_BILL_ID)
                .then()
                .statusCode(200)
                .contentType("image/svg+xml")
                .body(startsWith("<?xml"))
                .body(containsString("<svg"))
                .body(not(containsString("Croce")));
    }

    @Test
    void retrieveBillOverrideGraphicsFormat() {
        byte[] result = given()
            .queryParam("graphicsFormat", "pdf")
            .when()
                .get("/bill/image/{billId}", VALID_BILL_ID)
            .then()
                .statusCode(200)
                .contentType("application/pdf")
                .extract().asByteArray();

        assertThat(result.length, greaterThan(1000));
        String text = new String(result, 0, 8, StandardCharsets.UTF_8);
        assertThat(text, equalTo("%PDF-1.6"));
    }

    @Test
    void retrieveWithInvalidBillID() {
        given()
            .when()
                .get("/bill/image/{billId}", "eJxdT81OwzAMfpXI50Zqe0C010K1AxoTmTjlYrqQWUqdkqSDddq7kw5Osw")
            .then()
                .statusCode(400);
    }
}
