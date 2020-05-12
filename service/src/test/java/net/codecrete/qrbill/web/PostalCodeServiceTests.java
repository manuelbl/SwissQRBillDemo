//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.codecrete.qrbill.web.model.PostalCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test for postal code lookup API
 */
@QuarkusTest
@DisplayName("Postal code lookup service")
class PostalCodeServiceTests {

    @Test
    void singleMatch() {
        Response res = given()
            .when()
                .queryParam("country", "CH")
                .queryParam("substring", "8302")
                .get("/postal-codes/suggest")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        PostalCode[] postalCodes = JsonHelper.extract(res, PostalCode[].class);

        assertThat(postalCodes, notNullValue());
        assertThat(postalCodes.length, equalTo(1));
        assertThat(postalCodes[0].getPostalCode(), equalTo("8302"));
        assertThat(postalCodes[0].getTown(), equalTo("Kloten"));
    }

    @Test
    void multipleNumericMatches() {
        Response res = given()
            .when()
                .queryParam("country", "CH")
                .queryParam("substring", "1475")
                .get("/postal-codes/suggest")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        PostalCode[] postalCodes = JsonHelper.extract(res, PostalCode[].class);

        assertThat(postalCodes, notNullValue());
        assertThat(postalCodes.length, equalTo(3));
        for (PostalCode pc : postalCodes) {
            assertThat(pc.getPostalCode(), equalTo("1475"));
            assertThat(pc.getTown(), notNullValue());
        }
    }

    @Test
    void noMatchOutsideSwitzerland() {
        Response res = given()
            .when()
                .queryParam("country", "FR")
                .queryParam("substring", "123")
                .get("/postal-codes/suggest")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        PostalCode[] postalCodes = JsonHelper.extract(res, PostalCode[].class);

        assertThat(postalCodes, notNullValue());
        assertThat(postalCodes.length, equalTo(0));
    }

    @Test
    void getZurichSubstring() {
        Response res = given()
            .when()
                .queryParam("country", "")
                .queryParam("substring", "Züri")
                .get("/postal-codes/suggest")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        PostalCode[] postalCodes = JsonHelper.extract(res, PostalCode[].class);

        assertThat(postalCodes, notNullValue());
        assertThat(postalCodes.length, equalTo(20));
        for (PostalCode pc : postalCodes) {
            assertThat(pc.getTown(), equalTo("Zürich"));
            assertThat(pc.getPostalCode(), notNullValue());
            int postalCode = Integer.parseInt(pc.getPostalCode());
            assertThat(postalCode, greaterThanOrEqualTo(8000));
            assertThat(postalCode, lessThanOrEqualTo(8099));
        }
    }
}
