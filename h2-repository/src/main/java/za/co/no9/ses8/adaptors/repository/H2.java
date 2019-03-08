package za.co.no9.ses8.adaptors.repository;

import org.jdbi.v3.core.Jdbi;
import za.co.no9.ses8.domain.Event;
import za.co.no9.ses8.domain.ports.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;

public class H2 implements Repository<Jdbi> {
    public H2() {
    }


    @Override
    public Event saveEvent(Jdbi jdbi, String eventName, String content) {
        return jdbi.withHandle(handle -> {
            handle.execute("insert into event (when, name, content) values (?, ?, ?)", new Timestamp(Date.from(Instant.now()).getTime()), eventName, content);

            return handle
                    .createQuery("select id, when, name, content from event where id = SCOPE_IDENTITY()")
                    .map((rs, ctx) -> new Event(rs.getInt("id"), rs.getTimestamp("when"), rs.getString("name"), rs.getString("content")))
                    .findOnly();
        });
    }


    @Override
    public Iterator<Event> events(Jdbi jdbi) {
        return jdbi.withHandle(handle -> handle
                        .createQuery("select id, when, name, content from event order by id")
                        .map((rs, ctx) -> new Event(rs.getInt("id"), rs.getTimestamp("when"), rs.getString("name"), rs.getString("content")))
                        .list()).iterator();
    }


    @Override
    public Iterator<Event> eventsFrom(Jdbi jdbi, int id) {
        return jdbi.withHandle(handle -> handle
                .select("select id, when, name, content from event where id > ? order by id", id)
                .map((rs, ctx) -> new Event(rs.getInt("id"), rs.getTimestamp("when"), rs.getString("name"), rs.getString("content")))
                .list()).iterator();
    }
}
