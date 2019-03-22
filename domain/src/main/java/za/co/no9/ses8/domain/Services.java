package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.util.Optional;
import java.util.stream.Stream;

public class Services {
    public Event publish(UnitOfWork unitOfWork, String eventName, String content) {
        return unitOfWork.saveEvent(eventName, content);
    }


    public Stream<Event> events(UnitOfWork unitOfWork, int pageSize) {
        return unitOfWork.events(pageSize);
    }


    public Stream<Event> eventsFrom(UnitOfWork unitOfWork, int id, int pageSize) {
        return unitOfWork.eventsFrom(id, pageSize);
    }


    public Optional<Event> event(UnitOfWork unitOfWork, int id) {
        return unitOfWork.event(id);
    }
}
