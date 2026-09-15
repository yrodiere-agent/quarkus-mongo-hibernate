package io.quarkiverse.mongohibernate.it.namedpus.analytics;

import org.bson.types.ObjectId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class Event {

    @Id
    public ObjectId id;

    public String type;

    public String description;
}
