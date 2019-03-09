package za.co.no9.ses8.domain.ports;


import za.co.no9.ses8.domain.Event;

import java.util.Iterator;

public interface Repository<T> {
    T newContext();

    Event saveEvent(T ctx, String eventName, String content);

    Iterator<Event> events(T ctx);

    Iterator<Event> eventsFrom(T ctx, int id);
}
