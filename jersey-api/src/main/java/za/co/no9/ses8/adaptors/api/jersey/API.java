package za.co.no9.ses8.adaptors.api.jersey;

import io.swagger.annotations.Api;
import za.co.no9.ses8.domain.Event;
import za.co.no9.ses8.domain.Services;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(value = "/events", description = "Operations to access and append to the event stream.")
@Path("events")
public class API {
    @Inject
    public Services services;


    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public EventBean saveEvent(NewEventBean newEvent) {
        Event event =
                services.saveEvent(newEvent.name, newEvent.content);

        return EventBean.from(event);
    }


    @GET
    @Produces("application/json")
    public List<EventBean> getEvents(@QueryParam("start") Integer start, @QueryParam("pagesize") @DefaultValue("100") int pageSize) {
        return services
                .events(Optional.ofNullable(start), pageSize)
                .map(EventBean::from)
                .collect(Collectors.toList());
    }


    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getEvent(@PathParam("id") int id) {
        return services
                .event(id)
                .map(event -> Response.status(Response.Status.OK).expires(calculateExpires()).entity(EventBean.from(event)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }


    private Date calculateExpires() {
        Calendar instance =
                Calendar.getInstance();

        instance.add(Calendar.YEAR, 1);

        return instance.getTime();
    }
}