package za.co.no9.ses8.adaptors.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.no9.jfixture.FixtureException;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;
import za.co.no9.jfixture.JDBCHandler;
import za.co.no9.ses8.domain.Event;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class H2Test {
    @BeforeEach
    void before() throws IOException, FixtureException {
    }


    @Test
    void aTest() throws IOException, FixtureException {
        Fixtures fixtures =
                Fixtures.process(FixturesInput.fromLocation("resource:initial.yaml"));

        H2 h2 = new H2(fixtures.findHandler(JDBCHandler.class).get().connection());

        Event event1 =
                h2.saveEvent(new CustomerAdded("Luke Skywalker"));

        Event event2 =
                h2.saveEvent(new CustomerAdded("Han Solo"));

        assertEquals(1, event1.id);
        assertEquals("CustomerAdded{name='Luke Skywalker'}", event1.content);

        assertEquals(2, event2.id);
        assertEquals("CustomerAdded{name='Han Solo'}", event2.content);
    }
}