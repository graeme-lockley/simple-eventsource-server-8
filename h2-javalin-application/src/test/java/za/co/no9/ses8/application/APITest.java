package za.co.no9.ses8.application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.javalin.Javalin;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.no9.ses8.adaptors.EventBean;
import za.co.no9.ses8.adaptors.NewEventBean;
import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class APITest {
    private Gson gson =
            new Gson();

    private Repository repository;

    private Javalin server;


    @BeforeEach
    void before() {
        repository =
                new TestRepositoryImpl();

        server =
                Main.startServer(repository);
    }


    @AfterEach
    void after() {
        server.stop();
    }


    @Test
    void knownEvent() throws IOException {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        unitOfWork.saveEvent("CustomerAdded", "{name: \"Luke Skywalker\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Ben Solo\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Han Solo\"}");

        String response =
                Request.Get(Main.DEFAULT_BASE_URI + "events/1").execute().returnContent().asString();

        EventBean eventBean =
                gson.fromJson(response, EventBean.class);

        assertEventEquals("CustomerAdded", "Ben Solo", eventBean);
    }


    @Test
    void unknownEvent() throws IOException {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        unitOfWork.saveEvent("CustomerAdded", "{name: \"Luke Skywalker\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Ben Solo\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Han Solo\"}");

        Response response =
                Request.Get(Main.DEFAULT_BASE_URI + "events/10").execute();

        Assertions.assertEquals(412, response.returnResponse().getStatusLine().getStatusCode());
    }


    @Test
    void events() throws IOException {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        unitOfWork.saveEvent("CustomerAdded", "{name: \"Luke Skywalker\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Ben Solo\"}");

        String response =
                Request.Get(Main.DEFAULT_BASE_URI + "events").execute().returnContent().asString();

        List<EventBean> eventBeans =
                toEventBeanList(response);


        assertEquals(2, eventBeans.size());

        assertEventEquals("CustomerAdded", "Luke Skywalker", eventBeans.get(0));
        assertEventEquals("CustomerAdded", "Ben Solo", eventBeans.get(1));
    }


    @Test
    void eventsFrom() throws IOException {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        unitOfWork.saveEvent("CustomerAdded", "{name: \"Luke Skywalker\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Ben Solo\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Han Solo\"}");
        unitOfWork.saveEvent("CustomerAdded", "{name: \"Leia Organa\"}");

        String response =
                Request.Get(Main.DEFAULT_BASE_URI + "events?start=1").execute().returnContent().asString();

        List<EventBean> eventBeans =
                toEventBeanList(response);

        assertEquals(2, eventBeans.size());

        assertEventEquals("CustomerAdded", "Han Solo", eventBeans.get(0));
        assertEventEquals("CustomerAdded", "Leia Organa", eventBeans.get(1));
    }


    @Test
    void eventsWithDefaultPageSize() throws IOException {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        populateEvents(unitOfWork, "SomeEventHappened", 200);

        String response =
                Request.Get(Main.DEFAULT_BASE_URI + "events").execute().returnContent().asString();

        List<EventBean> eventBeans =
                toEventBeanList(response);

        assertEquals(100, eventBeans.size());
    }


    @Test
    void eventsFromWithPageSize() throws IOException {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        populateEvents(unitOfWork, "SomeEventHappened", 200);

        String response =
                Request.Get(Main.DEFAULT_BASE_URI + "events?start=50&pagesize=10").execute().returnContent().asString();

        List<EventBean> eventBeans =
                toEventBeanList(response);

        assertEquals(10, eventBeans.size());
        assertEquals(51, eventBeans.get(0).id);
    }


    @Test
    void saveEvents() throws IOException {
        NewEventBean input =
                new NewEventBean("CharacterAdded", "{name: \"Luke Skywalker\"}");

        String content =
                gson.toJson(input);

        // This is a piece of dummy Get code which causes the flow to wait until the server is ready.  For one or other
        // reason Post does not wait but then fails immediately.
        Request.Get(Main.DEFAULT_BASE_URI + "events/10").execute();

        String response =
                Request.Post(Main.DEFAULT_BASE_URI + "events").bodyString(content, ContentType.APPLICATION_JSON).execute().returnContent().asString();

        EventBean eventBean =
                gson.fromJson(response, EventBean.class);

        assertEventEquals("CharacterAdded", "Luke Skywalker", eventBean);
    }


    private void assertEventEquals(String eventName, String name, EventBean event) {
        assertEquals(eventName, event.name);
        assertEquals("{name: \"" + name + "\"}", event.content);
    }


    private void populateEvents(UnitOfWork unitOfWork, String name, int number) {
        for (int lp = 0; lp < number; lp += 1) {
            unitOfWork.saveEvent(name, "{count: " + lp + "}");
        }
    }


    private List<EventBean> toEventBeanList(String response) {
        Type listType = new TypeToken<ArrayList<EventBean>>() {
        }.getType();

        return gson.fromJson(response, listType);
    }
}
