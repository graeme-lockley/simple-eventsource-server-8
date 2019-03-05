package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.ports.Repository;

import java.util.Iterator;

public abstract class Services<T> {
    protected abstract Repository<T> repository();


    public Event publish(T ctx, Object event) {
        return repository().saveEvent(ctx, event);
    }


    public Iterator<Event> events(T ctx) {
        return repository().events(ctx);
    }

    public Iterator<Event> eventsFrom(T ctx, int id) {
        return repository().eventsFrom(ctx, id);
    }
}
