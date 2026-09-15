package io.quarkiverse.mongohibernate.it.namedpus;

import com.mongodb.client.MongoClient;

import io.quarkus.mongodb.MongoClientName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoClients {

    @Inject
    @MongoClientName("cluster1")
    MongoClient cluster1;

    @Inject
    @MongoClientName("cluster2")
    MongoClient cluster2;
}
