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
import net.codecrete.qrbill.web.model.ValidationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static net.codecrete.qrbill.web.TestHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;

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
        var response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(bill)
                .post("/bill/validated")
            .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().as(ValidationResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getBillID()).isNotNull();

        // Generate and validated image from bill ID
        var xml = given()
            .when()
                .get("/bill/image/{billId}", response.getBillID())
            .then()
                .spec(SVG)
                .extract().asString();

        assertThat(xml)
                .is(svg())
                .contains(textInSvg);
    }

    @Test
    void retrieveBillOverrideOutputSize() {
        var xml = given()
                .queryParam("outputSize", "qr-code-only")
            .when()
                .get("/bill/image/{billId}", VALID_BILL_ID)
            .then()
                .spec(SVG)
                .extract().asString();

        assertThat(xml)
                .is(svg())
                .doesNotContain("Croce");
    }

    @Test
    void retrieveBillOverrideGraphicsFormat() {
        byte[] result = given()
            .queryParam("graphicsFormat", "pdf")
            .when()
                .get("/bill/image/{billId}", VALID_BILL_ID)
            .then()
                .spec(PDF)
                .extract().asByteArray();

        assertThat(result).is(pdf());
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
