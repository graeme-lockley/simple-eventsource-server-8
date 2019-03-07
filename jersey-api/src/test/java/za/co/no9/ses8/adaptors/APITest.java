package za.co.no9.ses8.adaptors;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;


class APITest {
    private HttpServer server;
    private WebTarget target;


    @BeforeEach
    void before() {
        server = Main.startServer();

        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }


    @AfterEach
    void after() {
        server.shutdownNow();
    }

    @Test
    void aTest() {

    }
}
