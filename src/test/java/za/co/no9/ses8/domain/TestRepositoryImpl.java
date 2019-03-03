package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.provider.EventDetail;
import za.co.no9.ses8.domain.provider.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TestRepositoryImpl implements Repository {
    private List<String> savedEvents =
            new ArrayList<>();

    private int idCounter =
            0;


    @Override
    public EventDetail saveEvent(Object event) {
        String content =
                event.toString();

        savedEvents.add(content);

        EventDetail result = new EventDetail(idCounter, Date.from(Instant.now()), content);
        idCounter += 1;

        return result;
    }
}
