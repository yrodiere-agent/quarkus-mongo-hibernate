package io.quarkiverse.mongohibernate.deployment;

import java.util.HashSet;
import java.util.Set;

final class ClassNames {

    static final Set<String> CREATED_CONSTANTS = new HashSet<>();

    // mongo-hibernate internals
    static final String MONGO_DIALECT = createConstant("com.mongodb.hibernate.internal.dialect.MongoDialect");
    static final String MONGO_NAMED_STRATEGY_CONTRIBUTOR = createConstant(
            "com.mongodb.hibernate.internal.service.MongoNamedStrategyContributor");
    static final String MONGO_ADDITIONAL_MAPPING_CONTRIBUTOR = createConstant(
            "com.mongodb.hibernate.internal.boot.MongoAdditionalMappingContributor");
    static final String MONGO_SERVICE_REGISTRY_SCOPED_STATE = createConstant(
            "com.mongodb.hibernate.internal.service.StandardServiceRegistryScopedState");
    static final String MONGO_CONFIGURATION = createConstant(
            "com.mongodb.hibernate.internal.cfg.MongoConfiguration");

    // Extension runtime
    static final String JTA_AWARE_MONGO_CONNECTION_PROVIDER = createConstant(
            "io.quarkiverse.mongohibernate.runtime.JtaAwareMongoConnectionProvider");

    private ClassNames() {
    }

    private static String createConstant(String fqcn) {
        CREATED_CONSTANTS.add(fqcn);
        return fqcn;
    }
}
