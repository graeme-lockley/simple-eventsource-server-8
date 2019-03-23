package za.co.no9.ses8.adaptors.rest.javalin;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.swagger.annotations.Api;
import za.co.no9.ses8.domain.Event;
import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import javax.ws.rs.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Api(value = "/events", description = "Operations to access and append to the event stream.")
@Path("events")
public class API {
    private static Gson gson =
            new Gson();


    static EventBean saveEvent(Repository repository, NewEventBean newEvent) {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        Event event =
                unitOfWork.saveEvent(newEvent.name, newEvent.content);

        return EventBean.from(event);
    }


    static List<EventBean> getEvents(Repository repository, Optional<Integer> start, int pageSize) {
        return repository
                .newUnitOfWork()
                .events(start, pageSize)
                .map(EventBean::from)
                .collect(Collectors.toList());
    }


    static Optional<EventBean> getEvent(Repository repository, int id) {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        return unitOfWork
                .event(id)
                .map(EventBean::from);
    }


    private Date calculateExpires() {
        Calendar instance =
                Calendar.getInstance();

        instance.add(Calendar.YEAR, 1);

        return instance.getTime();
    }


    public static Javalin addEndpoints(final Javalin server, final Repository repository) {
        server.get("/api/events/:id", ctx -> {
            Optional<EventBean> event = getEvent(repository, Integer.parseInt(ctx.pathParam("id")));

            if (event.isPresent()) {
                ctx.header("Content-Type", "application/json");
                ctx.result(gson.toJson(event.get()));
            } else {
                ctx.status(412);
            }
        });

        server.get("/api/events", ctx -> {
            Optional<Integer> start =
                    Optional.ofNullable(ctx.queryParam("start")).map(Integer::parseInt);

            int pageSize =
                    Integer.parseInt(ctx.queryParam("pagesize", "100"));

            List<EventBean> events =
                    getEvents(repository, start, pageSize);

            ctx.header("Content-Type", "application/json");
            ctx.result(gson.toJson(events));
        });

        server.post("/api/events", ctx -> {
            NewEventBean newEventBean =
                    gson.fromJson(ctx.body(), NewEventBean.class);

            EventBean eventBean =
                    saveEvent(repository, newEventBean);

            ctx.header("Content-Type", "application/json");
            ctx.result(gson.toJson(eventBean));
        });

        return server;
    }
}