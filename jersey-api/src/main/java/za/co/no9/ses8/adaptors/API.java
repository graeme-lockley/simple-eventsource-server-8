package za.co.no9.ses8.adaptors;

import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}