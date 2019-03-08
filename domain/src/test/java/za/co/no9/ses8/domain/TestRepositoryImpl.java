package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.ports.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class TestRepositoryImpl implements Repository<TestContextImpl> {
    private List<Event> savedEvents =
            new ArrayList<>();

    private int idCounter =
            0;


    @Override
    public Event saveEvent(TestContextImpl ctx, String eventName, String content) {
        Event detail =
                new Event(idCounter, Date.from(Instant.now()), eventName, content);

        savedEvents.add(detail);
        idCounter += 1;

        return detail;
    }

    @Override
    public Iterator<Event> events(TestContextImpl ctx) {
        return savedEvents.iterator();
    }


    @Override
    public Iterator<Event> eventsFrom(TestContextImpl ctx, int id) {
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
