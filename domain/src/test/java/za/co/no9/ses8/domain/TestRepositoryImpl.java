package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

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
    public UnitOfWork newUnitOfWork() {
        return new UnitOfWork() {
            @Override
            public Event saveEvent(String eventName, String content) {
                return TestRepositoryImpl.this.saveEvent(eventName, content);
            }

            @Override
            public Iterator<Event> events() {
                return TestRepositoryImpl.this.events();
            }

            @Override
            public Iterator<Event> eventsFrom(int id) {
                return TestRepositoryImpl.this.eventsFrom(id);
            }
        };
    }


    private Event saveEvent(String eventName, String content) {
        Event detail =
                new Event(idCounter, Date.from(Instant.now()), eventName, content);

        savedEvents.add(detail);
        idCounter += 1;

        return detail;
    }

    private Iterator<Event> events() {
        return savedEvents.iterator();
    }


    private Iterator<Event> eventsFrom(int id) {
        int index =
                0;

        int lengthOfSavedEvents =
                savedEvents.size();

        while (true) {
            if (index < lengthOfSavedEvents) {
                if (savedEvents.get(index).id <= id) {
                    index += 1;
                } else {
                    return savedEvents.listIterator(index);
                }
            } else {
                return new ArrayList<Event>().iterator();
            }
        }
    }
}
