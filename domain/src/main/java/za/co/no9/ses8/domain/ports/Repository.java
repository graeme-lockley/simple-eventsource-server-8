package za.co.no9.ses8.domain.ports;


import za.co.no9.ses8.domain.Event;

import java.util.Iterator;

public interface Repository<T> {
    Event saveEvent(T ctx, Object event);

    Iterator<Event> events(T ctx);

    Iterator<Event> eventsFrom(T ctx, int id);
}
