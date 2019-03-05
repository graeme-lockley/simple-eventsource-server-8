package za.co.no9.ses8.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;


class ServicesTest {
    private Services<TestContextImpl> services =
            new TestServicesImpl();


    @Test
    void publishAnEvent() {
        Event event1 =
                services.publish(TestContextImpl.INSTANCE, new CustomerAdded("Luke Skywalker"));

        Event event2 =
                services.publish(TestContextImpl.INSTANCE, new CustomerAdded("Ben Kenobi"));

        Assertions.assertEquals(0, event1.id);
        Assertions.assertEquals("CustomerAdded{name='Luke Skywalker'}", event1.content.toString());

        Assertions.assertEquals(1, event2.id);
        Assertions.assertEquals("CustomerAdded{name='Ben Kenobi'}", event2.content.toString());
    }


    @Test
    void allEventsOverEmptyStream() {
        Iterator<Event> events =
                services.events(TestContextImpl.INSTANCE);

        Assertions.assertFalse(events.hasNext());
    }


    @Test
    void eventsOverNoneEmptyStream() {
        services.publish(TestContextImpl.INSTANCE, new CustomerAdded("Luke Skywalker"));
        services.publish(TestContextImpl.INSTANCE, new CustomerAdded("Ben Kenobi"));
        services.publish(TestContextImpl.INSTANCE, new CustomerAdded("Leia Organa"));

        Iterator<Event> events =
                services.events(TestContextImpl.INSTANCE);

        assertNextName(events, 0, "CustomerAdded{name='Luke Skywalker'}");
        assertNextName(events, 1, "CustomerAdded{name='Ben Kenobi'}");
        assertNextName(events, 2, "CustomerAdded{name='Leia Organa'}");

        Assertions.assertFalse(events.hasNext());
    }


    @Test
    void eventsFromOverEmptyStream() {
        Iterator<Event> events =
                services.eventsFrom(TestContextImpl.INSTANCE, 1);

        Assertions.assertFalse(events.hasNext());
    }


    @Test
    void eventsFromOverNonEmptyStream() {
        services.publish(TestContextImpl.INSTANCE, new CustomerAdded("Luke Skywalker"));
        services.publish(TestContextImpl.INSTANCE, new CustomerAdded("Ben Kenobi"));
        services.publish(TestContextImpl.INSTANCE, new CustomerAdded("Leia Organa"));

        Iterator<Event> events =
                services.eventsFrom(TestContextImpl.INSTANCE, 1);

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
