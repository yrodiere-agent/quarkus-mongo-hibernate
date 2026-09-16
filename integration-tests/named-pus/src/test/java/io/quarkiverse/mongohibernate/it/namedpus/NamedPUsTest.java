package io.quarkiverse.mongohibernate.it.namedpus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class NamedPUsTest {

    @Test
    void inventoryPersistenceUnitWorks() {
        String id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "Widget", "quantity": 42}
                        """)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Widget"))
                .extract().path("id");

        given()
                .when()
                .get("/products/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Widget"))
                .body("quantity", equalTo(42));
    }

    @Test
    void analyticsPersistenceUnitWorks() {
        String id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"type": "page_view", "description": "Home page"}
                        """)
                .when()
                .post("/events")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("type", equalTo("page_view"))
                .extract().path("id");

        given()
                .when()
                .get("/events/{id}", id)
                .then()
                .statusCode(200)
                .body("type", equalTo("page_view"))
                .body("description", equalTo("Home page"));
    }
}
