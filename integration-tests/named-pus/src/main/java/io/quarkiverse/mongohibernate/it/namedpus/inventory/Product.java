package io.quarkiverse.mongohibernate.it.namedpus.inventory;

import org.bson.types.ObjectId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    public ObjectId id;

    public String name;

    public int quantity;
}
