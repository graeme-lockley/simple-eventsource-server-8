package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.util.Optional;
import java.util.stream.Stream;

public class Services {
    private Repository repository;


    public Services(Repository repository) {
        this.repository = repository;
    }

    public Event saveEvent(UnitOfWork unitOfWork, String eventName, String content) {
        return unitOfWork.saveEvent(eventName, content);
    }


    public Stream<Event> events(UnitOfWork unitOfWork, Optional<Integer> from, int pageSize) {
        return unitOfWork.events(from, pageSize);
    }


    public Optional<Event> event(UnitOfWork unitOfWork, int id) {
        return unitOfWork.event(id);
    }


    public void registerObserver(Observer observer) {
        repository.register(observer);
    }
}
