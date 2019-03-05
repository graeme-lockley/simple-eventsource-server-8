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
    public Event saveEvent(Jdbi jdbi, Object event) {
        return jdbi.withHandle(handle -> {
            handle.execute("insert into event (when, content) values (?, ?)", new Timestamp(Date.from(Instant.now()).getTime()), event.toString());

            return handle
                    .createQuery("select id, when, content from event where id = SCOPE_IDENTITY()")
                    .map((rs, xtx) -> new Event(rs.getInt("id"), rs.getTimestamp("when"), rs.getString("content")))
                    .findOnly();
        });
    }


    @Override
    public Iterator<Event> events(Jdbi jdbi) {
        return null;
    }


    @Override
    public Iterator<Event> eventsFrom(Jdbi jdbi, int id) {
        return null;
    }
}
