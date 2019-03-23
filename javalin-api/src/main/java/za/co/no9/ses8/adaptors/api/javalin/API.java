package za.co.no9.ses8.adaptors.api.javalin;

import com.google.gson.Gson;
import io.javalin.Javalin;
import za.co.no9.ses8.domain.Event;
import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.util.*;
import java.util.stream.Collectors;


public class API {
    private static final Gson gson =
            new Gson();

    private final Repository repository;

    public API(Repository repository) {
        this.repository = repository;
    }


    EventBean saveEvent(NewEventBean newEvent) {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        Event event =
                unitOfWork.saveEvent(newEvent.name, newEvent.content);

        return EventBean.from(event);
    }


    List<EventBean> getEvents(Optional<Integer> start, int pageSize) {
        return repository
                .newUnitOfWork()
                .events(start, pageSize)
                .map(EventBean::from)
                .collect(Collectors.toList());
    }


    Optional<EventBean> getEvent(int id) {
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
        final API api =
                new API(repository);

        server.get("/api/events/:id", ctx -> {
            Optional<EventBean> event = api.getEvent(Integer.parseInt(ctx.pathParam("id")));

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
                    api.getEvents(start, pageSize);

            ctx.header("Content-Type", "application/json");
            ctx.result(gson.toJson(events));
        });

        server.post("/api/events", ctx -> {
            NewEventBean newEventBean =
                    gson.fromJson(ctx.body(), NewEventBean.class);

            EventBean eventBean =
                    api.saveEvent(newEventBean);

            ctx.header("Content-Type", "application/json");
            ctx.result(gson.toJson(eventBean));
        });

        return server;
    }
}