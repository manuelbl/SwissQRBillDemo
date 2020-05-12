package net.codecrete.qrbill.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@DisplayName("Invalid requests")
class InvalidRequestTests {

    @Test
    void testInvalidEnum() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body("{ \"format\": { \"language\": \"pl\" }, " +
                        "\"amount\": 100.34, \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }")
                .post("/bill/validated")
            .then()
                .statusCode(400);
    }
    @Test
    void testInvalidNumber1() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body("{ \"format\": { \"language\": \"pl\" }, " +
                        "\"amount\": abc, \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }")
                .post("/bill/validated")
            .then()
                .statusCode(400);
    }

    @Test
    void testInvalidNumber2() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body("{ \"format\": { \"language\": \"pl\" }, " +
                        "\"amount\": \"abc\", \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }")
                .post("/bill/validated")
            .then()
                .statusCode(400);
    }

    @Test
    void testInvalidJson() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body("{ \"language\": \"de\", \"amount\": \"100.34\", \"currency\": \"CHF\", [" +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }")
                .post("/bill/validated")
            .then()
                .statusCode(400);
    }

    @Test
    void testInvalidUrl() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body("{ \"language\": \"de\", \"amount\": \"100.34\", \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }")
                .post("/bill/validated2")
            .then()
                .statusCode(404);
    }
}
