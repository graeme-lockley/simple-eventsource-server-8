package za.co.no9.ses8.adaptors;

import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("events")
public class API {
    @Inject
    public Repository repository;


    @GET
    @Produces("application/json")
    public List<EventBean> getEvents() {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        return unitOfWork
                .events()
                .map(event -> new EventBean(event.id, event.when, event.eventName, event.content))
                .collect(Collectors.toList());
    }


    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getEvent(@PathParam("id") int id) {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        return unitOfWork
                .event(id)
                .map(event -> Response.status(Response.Status.OK).expires(calculateExpires()).entity(new EventBean(event.id, event.when, event.eventName, event.content)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }


    private Date calculateExpires() {
        Calendar instance =
                Calendar.getInstance();

        instance.add(Calendar.YEAR, 1);

        return instance.getTime();
    }
}