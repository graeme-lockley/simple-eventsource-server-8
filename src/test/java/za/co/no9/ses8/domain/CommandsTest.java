package za.co.no9.ses8.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;


class CommandsTest {
    private Commands commands =
            new TestCommandsImpl();


    @Test
    void publishAnEvent() {
        Event event1 =
                commands.publish(new CustomerAdded("Luke Skywalker"));

        Event event2 =
                commands.publish(new CustomerAdded("Ben Kenobi"));

        Assertions.assertEquals(0, event1.id);
        Assertions.assertEquals("CustomerAdded{name='Luke Skywalker'}", event1.content.toString());

        Assertions.assertEquals(1, event2.id);
        Assertions.assertEquals("CustomerAdded{name='Ben Kenobi'}", event2.content.toString());
    }


    @Test
    void allEventsOverEmptyStream() {
        Iterator<Event> events =
                commands.events();

        Assertions.assertFalse(events.hasNext());
    }


    @Test
    void eventsOverNoneEmptyStream() {
        commands.publish(new CustomerAdded("Luke Skywalker"));
        commands.publish(new CustomerAdded("Ben Kenobi"));
        commands.publish(new CustomerAdded("Leia Organa"));

        Iterator<Event> events =
                commands.events();

        assertNextName(events, 0, "CustomerAdded{name='Luke Skywalker'}");
        assertNextName(events, 1, "CustomerAdded{name='Ben Kenobi'}");
        assertNextName(events, 2, "CustomerAdded{name='Leia Organa'}");

        Assertions.assertFalse(events.hasNext());
    }


    @Test
    void eventsFromOverEmptyStream() {
        Iterator<Event> events =
                commands.eventsFrom(1);

        Assertions.assertFalse(events.hasNext());
    }


    @Test
    void eventsFromOverNonEmptyStream() {
        commands.publish(new CustomerAdded("Luke Skywalker"));
        commands.publish(new CustomerAdded("Ben Kenobi"));
        commands.publish(new CustomerAdded("Leia Organa"));

        Iterator<Event> events =
                commands.eventsFrom(1);

        assertNextName(events, 2, "CustomerAdded{name='Leia Organa'}");

        Assertions.assertFalse(events.hasNext());
    }


    private void assertNextName(Iterator<Event> events, int id, String content) {
        Assertions.assertTrue(events.hasNext());

        Event event =
                events.next();

        Assertions.assertEquals(id, event.id);
        Assertions.assertEquals(content, event.content.toString());
    }
}
