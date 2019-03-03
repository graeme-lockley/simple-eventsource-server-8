package za.co.no9.ses8.domain.provider;

import java.util.Date;

public class EventDetail {
    public final int id;
    public final Date when;
    public final Object content;


    public EventDetail(int id, Date when, Object content) {
        this.id = id;
        this.when = when;
        this.content = content;
    }
}
