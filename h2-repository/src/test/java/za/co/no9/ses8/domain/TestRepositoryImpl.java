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


    @Override
    public Iterator<Event> eventsFrom(int id) {
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
