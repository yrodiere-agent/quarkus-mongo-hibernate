package io.quarkiverse.mongohibernate.it.defaultpu;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @JsonIgnore
    public ObjectId id;

    public String title;

    public String author;

    public int year;

    @JsonProperty("id")
    @Transient
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }
}
