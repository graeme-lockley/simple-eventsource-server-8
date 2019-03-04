package za.co.no9.ses8.adaptors.repository;

import za.co.no9.ses8.domain.Event;
import za.co.no9.ses8.domain.ports.Repository;

import java.util.Iterator;

public class H2 implements Repository {
    @Override
    public Event saveEvent(Object event) {
        return null;
    }


    @Override
    public Iterator<Event> events() {
        return null;
    }


    @Override
    public Iterator<Event> eventsFrom(int id) {
        return null;
    }
}
