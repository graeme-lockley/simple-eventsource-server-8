package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.provider.EventDetail;
import za.co.no9.ses8.domain.provider.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TestRepositoryImpl implements Repository {
    private List<EventDetail> savedEvents =
            new ArrayList<>();

    private int idCounter =
            0;


    @Override
    public EventDetail saveEvent(Object event) {
        EventDetail detail =
                new EventDetail(idCounter, Date.from(Instant.now()), event);

        savedEvents.add(detail);
        idCounter += 1;

        return detail;
    }
}
