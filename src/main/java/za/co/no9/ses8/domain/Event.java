package za.co.no9.ses8.domain;

import java.util.Date;

public class Event {
    public final int id;
    public final Date when;
    public final Object content;


    public Event(int id, Date when, Object content) {
        this.id = id;
        this.when = when;
        this.content = content;
    }
}
