package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.provider.EventDetail;
import za.co.no9.ses8.domain.provider.Repository;

public abstract class Commands {
    protected abstract Repository repository();


    public EventDetail publish(Object event) {
        return repository().saveEvent(event);
    }
}
