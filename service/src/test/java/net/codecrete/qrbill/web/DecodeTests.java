//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.codecrete.qrbill.web.model.QrCodeInformation;
import net.codecrete.qrbill.web.model.ValidationMessage;
import net.codecrete.qrbill.web.model.ValidationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for QR code decoding API
 */
@QuarkusTest
@DisplayName("Decode QR code text")
class DecodeTests {

    //@formatter:off
    private static final String INVALID_QR_CODE_TEXT =
            "SPC\r\n" +
            "0100\r\n" +
            "1\r\n" +
            "CH2109000000450980316\r\n" +
            "Druckerei Stefan Meierhans\r\n" +
            "Trittligasse\r\n" +
            "12\r\n" +
            "3001\r\n" +
            "Bern\r\n" +
            "CH\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "45.60\r\n" +
            "CHF\r\n" +
            "2018-04-26\r\n" +
            "Anneliese Schmid\r\n" +
            "Segelhofstrasse\r\n" +
            "13\r\n" +
            "5057\r\n" +
            "Reitnau\r\n" +
            "CH\r\n" +
            "QRR\r\n" +
            "829300097829382938291172974\r\n";
    //@formatter:on

    //@formatter:off
    private static final String VALID_QR_CODE_TEXT = "SPC\n" +
            "0200\n" +
            "1\n" +
            "CH4431999123000889012\n" +
            "S\n" +
            "Robert Schneider AG\n" +
            "Rue du Lac\n" +
            "1268\n" +
            "2501\n" +
            "Biel\n" +
            "CH\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "1949.75\n" +
            "CHF\n" +
            "S\n" +
            "Pia-Maria Rutschmann-Schnyder\n" +
            "Grosse Marktgasse\n" +
            "28\n" +
            "9400\n" +
            "Rorschach\n" +
            "CH\n" +
            "QRR\n" +
            "210000000003139471430009017\n" +
            "Order dated 18.06.2020\n" +
            "EPD\n" +
            "//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010\n" +
            "UV;UltraPay005;12345\n" +
            "XY;XYService;54321";
    //@formatter:on

    @Test
    void decodeText() {

        QrCodeInformation info = new QrCodeInformation();
        info.setText(VALID_QR_CODE_TEXT);

        var response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(info)
                .post("/bill/qrdata")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ValidationResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getValid()).isTrue();
        assertThat(response.getValidationMessages()).isNull();
        assertThat(response.getValidatedBill()).isNotNull();
        assertThat(response.getBillID()).isNotNull();
        assertThat(response.getBillID().length()).isGreaterThan(100);
        assertThat(response.getQrCodeText()).isNotNull();
        assertThat(response.getQrCodeText()).isEqualTo(VALID_QR_CODE_TEXT);
    }

    @Test
    void decodeInvalidText() {

        QrCodeInformation info = new QrCodeInformation();
        info.setText(INVALID_QR_CODE_TEXT);

        var response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(info)
                .post("/bill/qrdata")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ValidationResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getValid()).isFalse();
        assertThat(response.getValidationMessages())
                .isNotNull()
                .hasSize(1);
        assertThat(response.getValidationMessages().get(0)).satisfies(m -> {
            assertThat(m.getType()).isEqualTo(ValidationMessage.TypeEnum.ERROR);
            assertThat(m.getField()).isEqualTo("qrText");
        });
    }
}
