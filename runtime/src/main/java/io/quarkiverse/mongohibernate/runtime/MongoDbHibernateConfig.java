package io.quarkiverse.mongohibernate.runtime;

import java.util.Map;

import io.quarkus.hibernate.orm.runtime.PersistenceUnitUtil;
import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefaults;
import io.smallrye.config.WithParentName;
import io.smallrye.config.WithUnnamedKey;

@ConfigMapping(prefix = "quarkus.hibernate-orm")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface MongoDbHibernateConfig {

    /**
     * Configuration for persistence units.
     */
    @WithParentName
    @WithUnnamedKey(value = PersistenceUnitUtil.DEFAULT_PERSISTENCE_UNIT_NAME, eager = false)
    @WithDefaults
    @ConfigDocMapKey("persistence-unit-name")
    Map<String, MongoDbHibernatePersistenceUnitConfig> persistenceUnits();
}
