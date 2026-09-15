package io.quarkiverse.mongohibernate.it.data;

import java.util.List;

import org.bson.types.ObjectId;
import org.hibernate.annotations.processing.Find;
import org.hibernate.annotations.processing.SQL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.data.hibernate.ManagedRepository;
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

    public interface Repo extends ManagedRepository.CustomId<Book, ObjectId> {

        @Find
        List<Book> findByAuthor(String author);

        @SQL("""
                {
                    aggregate: "books",
                    pipeline: [
                        { $match: { year: { $gte: 2000 } } },
                        { $project: { _id: 1, title: 1, author: 1, year: 1 } }
                    ]
                }""")
        List<Book> findRecentByNativeQuery();
    }

    @JsonProperty("id")
    @Transient
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }
}
