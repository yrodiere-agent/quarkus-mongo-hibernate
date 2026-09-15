package io.quarkiverse.mongohibernate.it.namedpus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.bson.types.ObjectId;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import io.quarkiverse.mongohibernate.it.namedpus.analytics.Event;
import io.quarkiverse.mongohibernate.it.namedpus.inventory.Product;
import io.quarkus.hibernate.orm.PersistenceUnit;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class TransactionTest {

    @Inject
    @PersistenceUnit("inventory")
    Session inventorySession;

    @Inject
    @PersistenceUnit("analytics")
    Session analyticsSession;

    @Test
    void inventoryWorksInOwnTransaction() {
        ObjectId id = new ObjectId();

        QuarkusTransaction.requiringNew().run(() -> {
            Product product = new Product();
            product.id = id;
            product.name = "Widget";
            product.quantity = 42;
            inventorySession.persist(product);
        });

        Product found = QuarkusTransaction.requiringNew().call(() -> {
            return inventorySession.find(Product.class, id);
        });
        assertNotNull(found);
        assertEquals("Widget", found.name);
        assertEquals(42, found.quantity);
    }

    @Test
    void analyticsWorksInOwnTransaction() {
        ObjectId id = new ObjectId();

        QuarkusTransaction.requiringNew().run(() -> {
            Event event = new Event();
            event.id = id;
            event.type = "page_view";
            event.description = "Home page";
            analyticsSession.persist(event);
        });

        Event found = QuarkusTransaction.requiringNew().call(() -> {
            return analyticsSession.find(Event.class, id);
        });
        assertNotNull(found);
        assertEquals("page_view", found.type);
        assertEquals("Home page", found.description);
    }

    @Test
    void twoPUsInSameTransactionFails() {
        assertThrows(Exception.class, () -> {
            QuarkusTransaction.requiringNew().run(() -> {
                Product product = new Product();
                product.id = new ObjectId();
                product.name = "Widget";
                product.quantity = 1;
                inventorySession.persist(product);
                inventorySession.flush();

                Event event = new Event();
                event.id = new ObjectId();
                event.type = "product_created";
                event.description = "Created Widget";
                analyticsSession.persist(event);
                analyticsSession.flush();
            });
        });
    }

    @Test
    void nativeQuery() {
        ObjectId id = new ObjectId();

        QuarkusTransaction.requiringNew().run(() -> {
            Product product = new Product();
            product.id = id;
            product.name = "NQ Widget";
            product.quantity = 7;
            inventorySession.persist(product);
        });

        List<Product> results = QuarkusTransaction.requiringNew().call(() -> {
            return inventorySession.createNativeQuery("""
                    {
                        aggregate: "products",
                        pipeline: [
                            { $match: { name: { $eq: "NQ Widget" } } },
                            { $project: { _id: 1, name: 1, quantity: 1 } }
                        ]
                    }""", Product.class).getResultList();
        });

        assertEquals(1, results.size());
        assertEquals("NQ Widget", results.get(0).name);
        assertEquals(7, results.get(0).quantity);
    }
}
