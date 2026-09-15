package io.quarkiverse.mongohibernate.it.defaultpu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.bson.types.ObjectId;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class TransactionTest {

    @Inject
    Session session;

    @Test
    void writeAndReadInSameTransaction() {
        ObjectId id = new ObjectId();
        QuarkusTransaction.requiringNew().run(() -> {
            Book book = new Book();
            book.id = id;
            book.title = "Same Transaction";
            book.author = "Test Author";
            book.year = 2024;
            session.persist(book);

            Book found = session.find(Book.class, id);
            assertNotNull(found);
            assertEquals("Same Transaction", found.title);
        });
    }

    @Test
    void writeAndReadInSeparateTransactions() {
        ObjectId id = new ObjectId();

        QuarkusTransaction.requiringNew().run(() -> {
            Book book = new Book();
            book.id = id;
            book.title = "Separate Transaction";
            book.author = "Test Author";
            book.year = 2025;
            session.persist(book);
        });

        Book found = QuarkusTransaction.requiringNew().call(() -> {
            return session.find(Book.class, id);
        });
        assertNotNull(found, "Book persisted in first transaction should be readable in second transaction");
        assertEquals("Separate Transaction", found.title);
    }

    @Test
    void rollbackPreventsRead() {
        ObjectId id = new ObjectId();

        try {
            QuarkusTransaction.requiringNew().run(() -> {
                Book book = new Book();
                book.id = id;
                book.title = "Should Be Rolled Back";
                book.author = "Test Author";
                book.year = 2026;
                session.persist(book);
                session.flush();
                throw new RuntimeException("Force rollback");
            });
        } catch (RuntimeException e) {
            // expected
        }

        Book found = QuarkusTransaction.requiringNew().call(() -> {
            return session.find(Book.class, id);
        });
        assertNull(found, "Flushed data from a rolled-back transaction must not be visible in MongoDB");
    }

}
