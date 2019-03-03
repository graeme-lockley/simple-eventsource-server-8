package za.co.no9.ses8.domain.provider;


import za.co.no9.ses8.domain.Event;

public interface Repository {
    Event saveEvent(Object event);
}
