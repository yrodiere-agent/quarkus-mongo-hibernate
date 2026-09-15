package io.quarkiverse.mongohibernate.it.data;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class QuarkusDataTest {

    @Test
    void createAndRetrieveBook() {
        String id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Hibernate in Action", "author": "Gavin King", "year": 2004}
                        """)
                .when()
                .post("/books")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Hibernate in Action"))
                .extract().path("id");

        given()
                .when()
                .get("/books/{id}", id)
                .then()
                .statusCode(200)
                .body("title", equalTo("Hibernate in Action"))
                .body("author", equalTo("Gavin King"))
                .body("year", equalTo(2004));
    }

    @Test
    void findByAuthor() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "MongoDB: The Definitive Guide", "author": "Shannon Bradshaw", "year": 2019}
                        """)
                .when()
                .post("/books")
                .then()
                .statusCode(201);

        given()
                .when()
                .queryParam("author", "Shannon Bradshaw")
                .get("/books/by-author")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].title", equalTo("MongoDB: The Definitive Guide"));
    }

    @Test
    void listBooks() {
        given()
                .when()
                .get("/books")
                .then()
                .statusCode(200);
    }

    @Test
    void deleteBook() {
        String id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "To Delete", "author": "Test", "year": 2024}
                        """)
                .when()
                .post("/books")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .when()
                .delete("/books/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/books/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void nativeQuery() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Modern MongoDB", "author": "NQ Author", "year": 2023}
                        """)
                .when()
                .post("/books")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/books/recent-native")
                .then()
                .statusCode(200)
                .body("find { it.title == 'Modern MongoDB' }.author", equalTo("NQ Author"));
    }

    @Test
    void getNonExistentBookReturns404() {
        given()
                .when()
                .get("/books/{id}", "000000000000000000000000")
                .then()
                .statusCode(404);
    }
}
