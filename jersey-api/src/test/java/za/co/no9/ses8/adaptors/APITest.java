package za.co.no9.ses8.adaptors;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class APITest {
    private Repository repository;

    private HttpServer server;
    private WebTarget target;


    @BeforeEach
    void before() {
        repository =
                new TestRepositoryImpl();

        server =
                Main.startServer(repository);

        target =
                ClientBuilder.newClient().target(Main.BASE_URI);
    }


    @AfterEach
    void after() {
        server.shutdownNow();
    }


    @Test
    void knownEvent() {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        unitOfWork.saveEvent("CustomerAdded", "{name: \"Luke Skywalker\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Ben Solo\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Han Solo\"}");

        EventBean response =
                target.path("events/1").request().get(EventBean.class);

        assertEventEquals("CustomerAdded", "Ben Solo", response);
    }


    @Test
    void unknownEvent() {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        unitOfWork.saveEvent("CustomerAdded", "{name: \"Luke Skywalker\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Ben Solo\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Han Solo\"}");

        Assertions.assertThrows(NotFoundException.class, () -> target.path("events/10").request().get(EventBean.class));
    }


    @Test
    void events() {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        unitOfWork.saveEvent("CustomerAdded", "{name: \"Luke Skywalker\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Ben Solo\"}");

        List<EventBean> response =
                target.path("events").request().get(new GenericType<List<EventBean>>() {
                });

        assertEquals(2, response.size());

        assertEventEquals("CustomerAdded", "Luke Skywalker", response.get(0));
        assertEventEquals("CustomerAdded", "Ben Solo", response.get(1));
    }


    private void assertEventEquals(String eventName, String name, EventBean event) {
        assertEquals(eventName, event.name);
        assertEquals("{name: \"" + name + "\"}", event.content);
    }
}
