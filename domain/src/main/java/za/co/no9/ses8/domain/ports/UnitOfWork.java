package za.co.no9.ses8.domain.ports;

import za.co.no9.ses8.domain.Event;

import java.util.stream.Stream;

public interface UnitOfWork {
    Event saveEvent(String eventName, String content);

    Stream<Event> events();

    Stream<Event> eventsFrom(int id);
}