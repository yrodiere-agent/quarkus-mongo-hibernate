package io.quarkiverse.mongohibernate.it.namedpus.analytics;

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

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    @PersistenceUnit("analytics")
    Session session;

    @GET
    public List<Event> list() {
        return session.createSelectionQuery("from Event", Event.class).getResultList();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        Event event = session.find(Event.class, new ObjectId(id));
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(event).build();
    }

    @POST
    @Transactional
    public Response create(Event event) {
        if (event.id == null) {
            event.id = new ObjectId();
        }
        session.persist(event);
        return Response.status(Response.Status.CREATED).entity(event).build();
    }
}
