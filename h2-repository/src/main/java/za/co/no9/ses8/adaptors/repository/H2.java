package za.co.no9.ses8.adaptors.repository;

import org.jdbi.v3.core.Jdbi;
import za.co.no9.ses8.domain.Event;
import za.co.no9.ses8.domain.ports.Repository;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;

public class H2 implements Repository {
    private Jdbi jdbi;


    public H2(Connection connection) {
        jdbi = Jdbi.create(connection);
    }


    @Override
    public Event saveEvent(Object event) {
        return jdbi.withHandle(handle -> {
            handle.execute("insert into event (when, content) values (?, ?)", new Timestamp(Date.from(Instant.now()).getTime()), event.toString());

            return handle
                    .createQuery("select id, when, content from event where id = SCOPE_IDENTITY()")
                    .map((rs, xtx) -> new Event(rs.getInt("id"), rs.getTimestamp("when"), rs.getString("content")))
                    .findOnly();
        });
    }


    @Override
    public Iterator<Event> events() {
        return null;
    }


    @Override
    public Iterator<Event> eventsFrom(int id) {
        return null;
    }
}
