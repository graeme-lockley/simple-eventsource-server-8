package za.co.no9.ses8.adaptors;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


@XmlRootElement
public class EventBean {
    public int id;
    public Date when;
    public String name;
    public String content;

    public EventBean() {
    }


    public EventBean(int id, Date when, String name, String content) {
        this.id = id;
        this.when = when;
        this.name = name;
        this.content = content;
    }
}
