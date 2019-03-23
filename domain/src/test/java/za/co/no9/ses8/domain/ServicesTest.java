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
        TestRepositoryImpl repository =
                new TestRepositoryImpl();

        services =
                new Services(repository);

        unitOfWork =
                repository.newUnitOfWork();
    }


    @Test
    void publishAnEvent() {
        Event event1 =
                services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));

        Event event2 =
                services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));

        Assertions.assertEquals(0, event1.id);
        Assertions.assertEquals("CustomerAdded{name='Luke Skywalker'}", event1.content);

        Assertions.assertEquals(1, event2.id);
        Assertions.assertEquals("CustomerAdded{name='Ben Kenobi'}", event2.content);
    }


    @Test
    void allEventsOverEmptyStream() {
        Stream<Event> events =
                services.events(unitOfWork, Optional.empty(), 100);

        Assertions.assertEquals(0, events.count());
    }


    @Test
    void eventsOverNoneEmptyStreamWithPageSize() {
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Leia Organa"));

        Stream<Event> events =
                services.events(unitOfWork, Optional.empty(), 2);

        Event[] eventsArray =
                events.toArray(Event[]::new);

        Assertions.assertEquals(2, eventsArray.length);

        assertEventEquals(eventsArray[0], 0, "CustomerAdded{name='Luke Skywalker'}");
        assertEventEquals(eventsArray[1], 1, "CustomerAdded{name='Ben Kenobi'}");

    }


    @Test
    void eventsFromOverEmptyStream() {
        Stream<Event> events =
                services.events(unitOfWork, Optional.of(1), 100);

        Assertions.assertEquals(0, events.count());
    }


    @Test
    void eventsFromOverNonEmptyStreamWithPageSize() {
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Han Solo"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Solo"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Leia Organa"));

        Stream<Event> events =
                services.events(unitOfWork, Optional.of(1), 2);

        Event[] eventsArray =
                events.toArray(Event[]::new);


        Assertions.assertEquals(2, eventsArray.length);

        assertEventEquals(eventsArray[0], 2, "CustomerAdded{name='Han Solo'}");
        assertEventEquals(eventsArray[1], 3, "CustomerAdded{name='Ben Solo'}");
    }


    @Test
    void knownEventDetail() {
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Leia Organa"));

        Optional<Event> event =
                services.event(unitOfWork, 1);

        Assertions.assertTrue(event.isPresent());

        assertEventEquals(event.get(), 1, "CustomerAdded{name='Ben Kenobi'}");
    }


    @Test
    void unknownEventDetail() {
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Luke Skywalker"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Ben Kenobi"));
        services.saveEvent(unitOfWork, "CustomerAdded", customerAddedEvent("Leia Organa"));

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
