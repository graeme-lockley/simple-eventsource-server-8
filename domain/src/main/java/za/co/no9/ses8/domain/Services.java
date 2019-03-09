package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.util.stream.Stream;

public class Services {
    public Event publish(UnitOfWork unitOfWork, String eventName, String content) {
        return unitOfWork.saveEvent(eventName, content);
    }


    public Stream<Event> events(UnitOfWork unitOfWork) {
        return unitOfWork.events();
    }


    public Stream<Event> eventsFrom(UnitOfWork unitOfWork, int id) {
        return unitOfWork.eventsFrom(id);
    }
}
