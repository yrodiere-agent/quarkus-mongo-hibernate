package io.quarkiverse.mongohibernate.it.data;

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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.bson.types.ObjectId;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    Book.Repo repository;

    @GET
    public List<Book> list() {
        return repository.listAll();
    }

    @GET
    @Path("/by-author")
    public List<Book> byAuthor(@QueryParam("author") String author) {
        return repository.findByAuthor(author);
    }

    @GET
    @Path("/recent-native")
    public List<Book> recentNative() {
        return repository.findRecentByNativeQuery();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        Book book = repository.findById(new ObjectId(id));
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
        repository.persist(book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") String id) {
        boolean deleted = repository.deleteById(new ObjectId(id));
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
