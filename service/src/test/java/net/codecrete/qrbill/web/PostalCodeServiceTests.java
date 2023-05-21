//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.codecrete.qrbill.web.api.PostalCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for postal code lookup API
 */
@QuarkusTest
@DisplayName("Postal code lookup service")
class PostalCodeServiceTests {

    @Test
    void singleMatch() {
        var postalCodes = given()
            .when()
                .queryParam("country", "CH")
                .queryParam("substring", "8302")
                .get("/postal-codes/suggest")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(PostalCode[].class);

        assertThat(postalCodes)
                .isNotNull()
                .hasSize(1);
        assertThat(postalCodes[0].getPostalCode()).isEqualTo("8302");
        assertThat(postalCodes[0].getTown()).isEqualTo("Kloten");
    }

    @Test
    void multipleNumericMatches() {
        var postalCodes = given()
            .when()
                .queryParam("country", "CH")
                .queryParam("substring", "1475")
                .get("/postal-codes/suggest")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(PostalCode[].class);

        assertThat(postalCodes)
                .isNotNull()
                .hasSize(3);

        for (var pc : postalCodes) {
            assertThat(pc.getPostalCode()).isEqualTo("1475");
            assertThat(pc.getTown()).isNotNull();
        }
    }

    @Test
    void noMatchOutsideSwitzerland() {
        var postalCodes = given()
            .when()
                .queryParam("country", "FR")
                .queryParam("substring", "123")
                .get("/postal-codes/suggest")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(PostalCode[].class);

        assertThat(postalCodes)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    void getZurichSubstring() {
        var postalCodes = given()
            .when()
                .queryParam("country", "")
                .queryParam("substring", "Züri")
                .get("/postal-codes/suggest")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(PostalCode[].class);

        assertThat(postalCodes)
                .isNotNull()
                .hasSize(20);

        for (var pc : postalCodes) {
            assertThat(pc.getTown()).isEqualTo("Zürich");
            assertThat(pc.getPostalCode()).isNotNull();
            int postalCode = Integer.parseInt(pc.getPostalCode());
            assertThat(postalCode).isBetween(8000, 8099);
        }
    }
}
