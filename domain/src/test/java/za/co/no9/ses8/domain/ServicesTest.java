package za.co.no9.ses8.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.util.Optional;
import java.util.stream.Stream;


class ServicesTest {
    private Services services;

    private UnitOfWork unitOfWork;


    @BeforeEach
    void beforeEach() {
        services =
                new Services();

        TestRepositoryImpl repository =
                new TestRepositoryImpl();

        unitOfWork =
                repository.newUnitOfWork();
    }


    @Test
    void publishAnEvent() {
        Event event1 =
                services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));

        Event event2 =
                services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));

        Assertions.assertEquals(0, event1.id);
        Assertions.assertEquals("CustomerAdded{name='Luke Skywalker'}", event1.content);

        Assertions.assertEquals(1, event2.id);
        Assertions.assertEquals("CustomerAdded{name='Ben Kenobi'}", event2.content);
    }


    @Test
    void allEventsOverEmptyStream() {
        Stream<Event> events =
                services.events(unitOfWork, 100);

        Assertions.assertEquals(0, events.count());
    }


    @Test
    void eventsOverNoneEmptyStream() {
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Leia Organa"));

        Stream<Event> events =
                services.events(unitOfWork, 100);

        Event[] eventsArray =
                events.toArray(Event[]::new);

        Assertions.assertEquals(3, eventsArray.length);

        assertEventEquals(eventsArray[0], 0, "CustomerAdded{name='Luke Skywalker'}");
        assertEventEquals(eventsArray[1], 1, "CustomerAdded{name='Ben Kenobi'}");
        assertEventEquals(eventsArray[2], 2, "CustomerAdded{name='Leia Organa'}");

    }


    @Test
    void eventsFromOverEmptyStream() {
        Stream<Event> events =
                services.eventsFrom(unitOfWork, 1);

        Assertions.assertEquals(0, events.count());
    }


    @Test
    void eventsFromOverNonEmptyStream() {
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Leia Organa"));

        Stream<Event> events =
                services.eventsFrom(unitOfWork, 1);

        Event[] eventsArray =
                events.toArray(Event[]::new);


        Assertions.assertEquals(1, eventsArray.length);

        assertEventEquals(eventsArray[0], 2, "CustomerAdded{name='Leia Organa'}");
    }


    @Test
    void knownEventDetail() {
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Leia Organa"));

        Optional<Event> event =
                services.event(unitOfWork, 1);

        Assertions.assertTrue(event.isPresent());

        assertEventEquals(event.get(), 1, "CustomerAdded{name='Ben Kenobi'}");
    }


    @Test
    void unknownEventDetail() {
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));
        services.publish(unitOfWork, "CustomerAdded", customerAddedEvent("Leia Organa"));

        Optional<Event> event =
                services.event(unitOfWork, 10);

        Assertions.assertFalse(event.isPresent());
    }


    private void assertEventEquals(Event event, int id, String content) {
        Assertions.assertEquals(id, event.id);
        Assertions.assertEquals("CustomerAdded", event.name);
        Assertions.assertEquals(content, event.content);
    }

    private String customerAddedEvent(String name) {
        return new CustomerAdded(name).toString();
    }
}
