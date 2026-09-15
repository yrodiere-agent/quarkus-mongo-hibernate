# quarkus-mongo-hibernate

EXPERIMENT to integrate Quarkus with mongo-hibernate

See integration tests for usage examples, but in the simplest case it boils down to:

1. Using Hibernate ORM as documented in https://quarkus.io/guides/hibernate-orm, ignoring the parts about datasources.
2. Making sure the default MongoDB client is defined, as documented in https://quarkus.io/guides/mongodb/.
3. Add a dependency to the quarkus-mongo-hibernate extension.

NOTE: Currently, a modified version of Quarkus is needed to build this project and run the tests. See https://github.com/quarkusio/quarkus/pull/56761
