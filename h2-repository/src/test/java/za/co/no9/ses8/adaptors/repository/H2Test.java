package za.co.no9.ses8.adaptors.repository;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.no9.jfixture.FixtureException;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;
import za.co.no9.jfixture.JDBCHandler;
import za.co.no9.ses8.domain.Event;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;


class H2Test {
    private Jdbi jdbi;
    private H2 h2;

    @BeforeEach
    void before() throws IOException, FixtureException {
        Fixtures fixtures =
                Fixtures.process(FixturesInput.fromLocation("resource:initial.yaml"));

        jdbi = Jdbi.create(fixtures.findHandler(JDBCHandler.class).get().connection());
        h2 = new H2();
    }


    @Test
    void saveEvent() {
        Event event1 =
                saveEvent("Luke Skywalker");

        Event event2 =
                saveEvent("Han Solo");

        assertEquals(1, event1.id);
        assertEquals("CustomerAdded{name='Luke Skywalker'}", event1.content);

        assertEquals(2, event2.id);
        assertEquals("CustomerAdded{name='Han Solo'}", event2.content);
    }


    @Test
    void events() {
        saveEvent("Luke Skywalker");
        saveEvent("Han Solo");
        saveEvent("R2D2");

        Iterator<Event> events =
                h2.events(jdbi);

        assertNextName(events, 1, "CustomerAdded{name='Luke Skywalker'}");
        assertNextName(events, 2, "CustomerAdded{name='Han Solo'}");
        assertNextName(events, 3, "CustomerAdded{name='R2D2'}");

        Assertions.assertFalse(events.hasNext());
    }


    @Test
    void eventsFrom() {
        saveEvent("Luke Skywalker");
        saveEvent("Han Solo");
        saveEvent("R2D2");

        Iterator<Event> events =
                h2.eventsFrom(jdbi, 2);

        assertNextName(events, 3, "CustomerAdded{name='R2D2'}");

        Assertions.assertFalse(events.hasNext());
    }


    private void assertNextName(Iterator<Event> events, int id, String content) {
        Assertions.assertTrue(events.hasNext());

        Event event =
                events.next();

        Assertions.assertEquals(id, event.id);
        Assertions.assertEquals(content, event.content);
    }

    private Event saveEvent(String name) {
        return h2.saveEvent(jdbi, "CustomerAdded", new CustomerAdded(name).toString());
    }
}