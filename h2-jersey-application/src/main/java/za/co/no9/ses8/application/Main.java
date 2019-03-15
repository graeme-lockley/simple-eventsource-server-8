package za.co.no9.ses8.application;

import io.swagger.jaxrs.config.BeanConfig;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.jdbi.v3.core.Jdbi;
import za.co.no9.ses8.adaptors.repository.H2;
import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.io.IOException;
import java.net.URI;
import java.util.Random;


public class Main {
    public static final String BASE_URI =
            "http://localhost:8080/api/";


    public static HttpServer startServer(Repository repository) {
        String resources = "za.co.no9.ses8.adaptors";
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/api/");
        beanConfig.setResourcePackage(resources);
        beanConfig.setScan(true);

        final ResourceConfig rc =
                new ResourceConfig()
                        .register(new AbstractBinder() {
                            @Override
                            protected void configure() {
                                bind(repository).to(Repository.class);
                            }
                        })
                        .register(io.swagger.jaxrs.listing.ApiListingResource.class)
                        .register(io.swagger.jaxrs.listing.SwaggerSerializers.class)
                        .packages("za.co.no9.ses8.adaptors");

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final Jdbi jdbi =
                Jdbi.create("jdbc:h2:/Users/graemelockley/Projects/simple-eventsource-server-8/main/target/stream", "sa", "");

        final Repository repository =
                new H2(jdbi);

//        insertRows(repository);

        final HttpServer server =
                startServer(repository);

        ClassLoader loader = Main.class.getClassLoader();
        CLStaticHttpHandler docsHandler = new CLStaticHttpHandler(loader, "swagger-ui/");
        docsHandler.setFileCacheEnabled(false);

        ServerConfiguration cfg = server.getServerConfiguration();
        cfg.addHttpHandler(docsHandler, "/");

        System.out.println(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit enter to stop it...", BASE_URI));

        System.in.read();
        server.shutdownNow();
    }


    private static final void insertRows(Repository repository) {
        UnitOfWork unitOfWork =
                repository.newUnitOfWork();

        int lp = 0;
        while (lp < 1000000) {
            switch (lp % 3) {
                case 0:
                    unitOfWork.saveEvent("CustomerAdded",
                            "{name: \"" + randomString(1, 'A', 'Z') + randomString(8, 'a', 'z') + "\"," +
                                    "customerID: " + lp + "}");
                    break;

                case 1:
                    unitOfWork.saveEvent("AccountAdded",
                            "{productID: \"CUR023\"," +
                                    "number: \"" + randomString(10, '0', '9') + "\"," +
                                    "customerID: " + (lp - 1) + "}");
                    break;

                case 2:
                    unitOfWork.saveEvent("AccountAdded",
                            "{productID: \"CUR027\"," +
                                    "number: \"" + randomString(10, '0', '9') + "\"," +
                                    "customerID: " + (lp - 2) + "}");
                    break;
            }

            lp += 1;
        }
    }


    private static String randomString(int targetStringLength, char leftLimit, char rightLimit) {
        Random random =
                new Random();

        StringBuilder buffer =
                new StringBuilder(targetStringLength);

        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));

            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }
}