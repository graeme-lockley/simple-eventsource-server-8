package za.co.no9.ses8.domain.provider;


import za.co.no9.ses8.domain.Event;

import java.util.Iterator;

public interface Repository {
    Event saveEvent(Object event);

    Iterator<Event> events();

    Iterator<Event> eventsFrom(int id);
}
