package org.acme.todo;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TodoResourceTest {

    @Inject
    TestConnectionPool testConnectionPool;

    @AfterEach
    void tearDown() throws SQLException {
        this.testConnectionPool.getRealConnection().rollback();
    }

    @Test
    void testInsertAndGet() {
        final Todo newTodo = new Todo(null, "Buy groceries", "Milk, Eggs, Bread", false, null);

        final Todo created = given()
            .contentType(ContentType.JSON)
            .body(newTodo)
            .when()
            .post("/todos")
            .then()
            .statusCode(201)
            .extract()
            .as(Todo.class);

        assertThat(created.id()).isNotNull();
        assertThat(created.title()).isEqualTo("Buy groceries");
        assertThat(created.description()).isEqualTo("Milk, Eggs, Bread");
        assertThat(created.completed()).isFalse();

        final Todo fetched = given()
            .when()
            .get("/todos/" + created.id())
            .then()
            .statusCode(200)
            .extract()
            .as(Todo.class);

        assertThat(fetched).isEqualTo(created);
    }

    @Test
    void testInsertUpdateAndGet() {
        final Todo initial = new Todo(null, "Task to update", "Initial description", false, null);

        final Todo created = given()
            .contentType(ContentType.JSON)
            .body(initial)
            .when()
            .post("/todos")
            .then()
            .statusCode(201)
            .extract()
            .as(Todo.class);

        final Todo updatePayload = new Todo(created.id(), "Task updated", "Updated description", true, created.createdAt());

        final Todo updated = given()
            .contentType(ContentType.JSON)
            .body(updatePayload)
            .when()
            .put("/todos/" + created.id())
            .then()
            .statusCode(200)
            .extract()
            .as(Todo.class);

        assertThat(updated.title()).isEqualTo("Task updated");
        assertThat(updated.description()).isEqualTo("Updated description");
        assertThat(updated.completed()).isTrue();

        final Todo fetched = given()
            .when()
            .get("/todos/" + created.id())
            .then()
            .statusCode(200)
            .extract()
            .as(Todo.class);

        assertThat(fetched.title()).isEqualTo("Task updated");
        assertThat(fetched.completed()).isTrue();
    }

    @Test
    void testInsertGetDeleteAndGet() {
        final Todo newTodo = new Todo(null, "Task to delete", "Will be deleted", false, null);

        final Todo created = given()
            .contentType(ContentType.JSON)
            .body(newTodo)
            .when()
            .post("/todos")
            .then()
            .statusCode(201)
            .extract()
            .as(Todo.class);

        given()
            .when()
            .get("/todos/" + created.id())
            .then()
            .statusCode(200);

        given()
            .when()
            .delete("/todos/" + created.id())
            .then()
            .statusCode(204);

        given()
            .when()
            .get("/todos/" + created.id())
            .then()
            .statusCode(404);
    }
}
