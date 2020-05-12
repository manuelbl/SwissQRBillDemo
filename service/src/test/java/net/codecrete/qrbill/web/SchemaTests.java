package net.codecrete.qrbill.web;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
@DisplayName("Schema")
class SchemaTests {

    @Test
    void testInvalidEnum() {
        given()
            .when()
                .get("/qrbill.yaml")
            .then()
                .statusCode(200)
                .contentType("application/x-yaml")
                .body(startsWith("openapi: "))
                .body(containsString("/bill/validated"));
    }
}
