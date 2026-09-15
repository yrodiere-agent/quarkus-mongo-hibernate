package io.quarkiverse.mongohibernate.it.defaultpu;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.hibernate.Session;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    Session session;

    @GET
    public List<Book> list() {
        return session.createSelectionQuery("from Book", Book.class).getResultList();
    }

    @GET
    @Path("/native-search")
    public List<Book> nativeSearch() {
        return session.createNativeQuery("""
                {
                    aggregate: "books",
                    pipeline: [
                        { $match: { year: { $gte: 2000 } } },
                        { $project: { _id: 1, title: 1, author: 1, year: 1 } }
                    ]
                }""", Book.class).getResultList();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        Book book = session.find(Book.class, new ObjectId(id));
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(book).build();
    }

    @POST
    @Transactional
    public Response create(Book book) {
        if (book.id == null) {
            book.id = new ObjectId();
        }
        session.persist(book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") String id) {
        Book book = session.find(Book.class, new ObjectId(id));
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        session.remove(book);
        return Response.noContent().build();
    }
}
