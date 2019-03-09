package za.co.no9.ses8.adaptors;

import za.co.no9.ses8.domain.Event;
import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Path("events")
public class API {
    @Inject
    public Repository repository;


    @GET
    @Produces("application/json")
    public List<EventBean> getEvents() {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        Iterator<Event> events =
                unitOfWork.events();

        return map(events);
    }


    private List<EventBean> map(Iterator<Event> events) {
        List<EventBean> result =
                new LinkedList<>();

        while (events.hasNext()) {
            result.add(map(events.next()));
        }

        return result;
    }


    private EventBean map(Event event) {
        return new EventBean(event.id, event.when, event.eventName, event.content);
    }
}