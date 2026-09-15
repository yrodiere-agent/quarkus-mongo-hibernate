package io.quarkiverse.mongohibernate.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface MongoDbHibernatePersistenceUnitConfig {

    /**
     * Query-related configuration.
     */
    QueryConfig query();

    @ConfigGroup
    interface QueryConfig {

        /**
         * How null values are handled in queries.
         *
         * Set to {@code MQL} for MongoDB Query Language null semantics,
         * which differ from SQL null semantics.
         */
        @WithDefault("MQL")
        String nullSemantics();
    }
}
