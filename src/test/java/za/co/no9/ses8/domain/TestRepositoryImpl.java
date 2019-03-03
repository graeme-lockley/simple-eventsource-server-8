package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.provider.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class TestRepositoryImpl implements Repository {
    private List<Event> savedEvents =
            new ArrayList<>();

    private int idCounter =
            0;


    @Override
    public Event saveEvent(Object event) {
        Event detail =
                new Event(idCounter, Date.from(Instant.now()), event);

        savedEvents.add(detail);
        idCounter += 1;

        return detail;
    }

    @Override
    public Iterator<Event> events() {
        return savedEvents.iterator();
    }
}
