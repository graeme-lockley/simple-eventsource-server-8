package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


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
            public Optional<Event> event(int id) {
                return TestRepositoryImpl.this.event(id);
            }

            @Override
            public Stream<Event> events() {
                return TestRepositoryImpl.this.events();
            }

            @Override
            public Stream<Event> eventsFrom(int id) {
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


    private Optional<Event> event(int id) {
        return savedEvents.stream().filter(event -> event.id == id).findFirst();
    }


    private Stream<Event> events() {
        return savedEvents.stream();
    }


    private Stream<Event> eventsFrom(int id) {
        int index =
                0;

        int lengthOfSavedEvents =
                savedEvents.size();

        while (true) {
            if (index < lengthOfSavedEvents) {
                if (savedEvents.get(index).id <= id) {
                    index += 1;
                } else {
                    Iterator<Event> sourceIterator =
                            savedEvents.listIterator(index);

                    Iterable<Event> iterable =
                            () -> sourceIterator;

                    return StreamSupport.stream(iterable.spliterator(), false);
                }
            } else {
                return Stream.empty();
            }
        }
    }
}
