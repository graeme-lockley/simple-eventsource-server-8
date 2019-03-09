package za.co.no9.ses8.adaptors;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import za.co.no9.ses8.domain.ports.Repository;

import java.io.IOException;
import java.net.URI;


public class Main {
    public static final String BASE_URI =
            "http://localhost:8080/api/";


    public static HttpServer startServer(Repository repository) {
        final ResourceConfig rc =
                new ResourceConfig()
                        .register(new AbstractBinder() {
                            @Override
                            protected void configure() {
                                bind(repository).to(Repository.class);
                            }
                        }).packages("za.co.no9.ses8.adaptors");

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final Repository<TestContextImpl> repository =
                new TestRepositoryImpl();

        final HttpServer server =
                startServer(repository);

        System.out.println(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}
