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

    private static final String INVALID_QR_CODE_TEXT =
            """
SPC\r
0100\r
1\r
CH2109000000450980316\r
Druckerei Stefan Meierhans\r
Trittligasse\r
12\r
3001\r
Bern\r
CH\r
\r
\r
\r
\r
\r
\r
45.60\r
CHF\r
2018-04-26\r
Anneliese Schmid\r
Segelhofstrasse\r
13\r
5057\r
Reitnau\r
CH\r
QRR\r
829300097829382938291172974\r
""";

    private static final String VALID_QR_CODE_TEXT = """
SPC
0200
1
CH4431999123000889012
S
Robert Schneider AG
Rue du Lac
1268
2501
Biel
CH







1949.75
CHF
S
Pia-Maria Rutschmann-Schnyder
Grosse Marktgasse
28
9400
Rorschach
CH
QRR
210000000003139471430009017
Order dated 18.06.2020
EPD
//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010
UV;UltraPay005;12345
XY;XYService;54321""";

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
        assertThat(response.getValidationMessages().getFirst()).satisfies(m -> {
            assertThat(m.getType()).isEqualTo(ValidationMessage.TypeEnum.ERROR);
            assertThat(m.getField()).isEqualTo("qrText");
        });
    }
}
