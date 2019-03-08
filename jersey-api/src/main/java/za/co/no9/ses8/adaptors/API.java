package za.co.no9.ses8.adaptors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("events")
public class API {
    @GET
    @Produces("application/json")
    public EventBean getEvents() {
        return new EventBean(100, new java.util.Date(), "Some or other content");
    }
}