package io.quarkiverse.mongohibernate.it.namedpus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;

import io.quarkus.arc.Arc;
import io.quarkus.hibernate.orm.PersistenceUnit;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class NamedPUsTest {

    @Test
    void defaultSessionNotAvailable() {
        assertFalse(Arc.container().select(Session.class).isResolvable(),
                "No default Session should be available — only named PUs are configured");
    }

    @Test
    void defaultMongoClientNotAvailable() {
        assertFalse(Arc.container().select(MongoClient.class).isResolvable(),
                "No default MongoClient should be available — only named clients are configured");
    }

    @Test
    void namedSessionsAvailable() {
        assertTrue(Arc.container()
                .select(Session.class, new PersistenceUnit.PersistenceUnitLiteral("inventory"))
                .isResolvable());
        assertTrue(Arc.container()
                .select(Session.class, new PersistenceUnit.PersistenceUnitLiteral("analytics"))
                .isResolvable());
    }
}
