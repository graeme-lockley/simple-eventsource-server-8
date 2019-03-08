package za.co.no9.ses8.adaptors;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.jupiter.api.Assertions.assertTrue;


class APITest {
    private HttpServer server;
    private WebTarget target;


    @BeforeEach
    void before() {
        server =
                Main.startServer();

        target =
                ClientBuilder.newClient().target(Main.BASE_URI);
    }


    @AfterEach
    void after() {
        server.shutdownNow();
    }


    @Test
    void aTest() {
        String responseMsg =
                target.path("events").request().get(String.class);

        assertTrue(responseMsg.startsWith("{\"id\":100,\"when\":\""));
        assertTrue(responseMsg.endsWith("\",\"content\":\"Some or other content\"}"));
    }
}
