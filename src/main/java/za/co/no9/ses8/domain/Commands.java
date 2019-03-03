package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.provider.Repository;

import java.util.Iterator;

public abstract class Commands {
    protected abstract Repository repository();


    public Event publish(Object event) {
        return repository().saveEvent(event);
    }


    public Iterator<Event> events() {
        return repository().events();
    }

    public Iterator<Event> eventsFrom(int id) {
        return repository().eventsFrom(id);
    }
}
