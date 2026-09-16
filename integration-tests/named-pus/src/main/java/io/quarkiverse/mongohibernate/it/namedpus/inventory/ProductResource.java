package io.quarkiverse.mongohibernate.it.namedpus.inventory;

import java.util.List;

import org.bson.types.ObjectId;
import org.hibernate.Session;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    @PersistenceUnit("inventory")
    Session session;

    @GET
    public List<Product> list() {
        return session.createSelectionQuery("from Product", Product.class).getResultList();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        Product product = session.find(Product.class, new ObjectId(id));
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(product).build();
    }

    @POST
    @Transactional
    public Response create(Product product) {
        if (product.id == null) {
            product.id = new ObjectId();
        }
        session.persist(product);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }
}
