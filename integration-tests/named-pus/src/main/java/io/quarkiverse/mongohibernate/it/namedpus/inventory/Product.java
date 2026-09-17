package io.quarkiverse.mongohibernate.it.namedpus.inventory;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @JsonIgnore
    public ObjectId id;

    public String name;

    public int quantity;

    @JsonProperty("id")
    @Transient
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }
}
