package za.co.no9.ses8.adaptors.repository;

import org.jdbi.v3.core.Jdbi;
import za.co.no9.ses8.domain.Event;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;

public class H2UnitOfWork implements za.co.no9.ses8.domain.ports.UnitOfWork {
    private final Jdbi jdbi;


    public H2UnitOfWork(Jdbi context) {
        this.jdbi = context;
    }

    @Override
    public Event saveEvent(String eventName, String content) {
        return jdbi.withHandle(handle -> {
            handle.execute("insert into event (when, name, content) values (?, ?, ?)", new Timestamp(Date.from(Instant.now()).getTime()), eventName, content);

            return handle
                    .createQuery("select id, when, name, content from event where id = SCOPE_IDENTITY()")
                    .map((rs, ctx) -> new Event(rs.getInt("id"), rs.getTimestamp("when"), rs.getString("name"), rs.getString("content")))
                    .findOnly();
        });
    }


    @Override
    public Stream<Event> events() {
        return jdbi.withHandle(handle -> handle
                        .createQuery("select id, when, name, content from event order by id")
                        .map((rs, ctx) -> new Event(rs.getInt("id"), rs.getTimestamp("when"), rs.getString("name"), rs.getString("content")))
                        .stream());
    }


    @Override
    public Stream<Event> eventsFrom(int id) {
        return jdbi.withHandle(handle -> handle
                .select("select id, when, name, content from event where id > ? order by id", id)
                .map((rs, ctx) -> new Event(rs.getInt("id"), rs.getTimestamp("when"), rs.getString("name"), rs.getString("content")))
                .stream());
    }
}