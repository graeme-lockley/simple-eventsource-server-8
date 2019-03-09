package za.co.no9.ses8.domain.ports;

import za.co.no9.ses8.domain.Event;

import java.util.Iterator;

public interface UnitOfWork {
    Event saveEvent(String eventName, String content);

    Iterator<Event> events();

    Iterator<Event> eventsFrom(int id);
}
