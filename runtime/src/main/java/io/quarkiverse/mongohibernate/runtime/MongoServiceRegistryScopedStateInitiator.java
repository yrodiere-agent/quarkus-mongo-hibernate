package io.quarkiverse.mongohibernate.runtime;

import java.util.Map;

import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

import com.mongodb.client.MongoClient;
import com.mongodb.hibernate.internal.cfg.MongoConfiguration;
import com.mongodb.hibernate.internal.service.StandardServiceRegistryScopedState;

final class MongoServiceRegistryScopedStateInitiator
        implements StandardServiceInitiator<StandardServiceRegistryScopedState> {

    static final String MONGO_CLIENT_PROPERTY = "io.quarkiverse.mongohibernate.runtime.mongoClient";
    static final String DATABASE_NAME_PROPERTY = "io.quarkiverse.mongohibernate.runtime.databaseName";

    static final MongoServiceRegistryScopedStateInitiator INSTANCE = new MongoServiceRegistryScopedStateInitiator();

    private MongoServiceRegistryScopedStateInitiator() {
    }

    @Override
    public Class<StandardServiceRegistryScopedState> getServiceInitiated() {
        return StandardServiceRegistryScopedState.class;
    }

    @Override
    public StandardServiceRegistryScopedState initiateService(Map<String, Object> configurationValues,
            ServiceRegistryImplementor registry) {
        Object clientValue = configurationValues.get(MONGO_CLIENT_PROPERTY);
        if (!(clientValue instanceof MongoClient mongoClient)) {
            throw new IllegalStateException(
                    "No Quarkus-managed MongoClient available. This is a bug in the quarkus-mongodb-hibernate extension.");
        }
        Object dbValue = configurationValues.get(DATABASE_NAME_PROPERTY);
        if (!(dbValue instanceof String databaseName) || databaseName.isEmpty()) {
            throw new IllegalStateException(
                    "No database name available. This is a bug in the quarkus-mongodb-hibernate extension.");
        }
        return new StandardServiceRegistryScopedState(new MongoConfiguration(mongoClient, databaseName));
    }
}
