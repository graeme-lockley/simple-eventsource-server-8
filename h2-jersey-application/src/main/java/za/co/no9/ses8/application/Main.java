package za.co.no9.ses8.application;

import io.swagger.jaxrs.config.BeanConfig;
import org.apache.commons.cli.*;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.jdbi.v3.core.Jdbi;
import za.co.no9.ses8.adaptors.repository.H2;
import za.co.no9.ses8.domain.ports.Repository;

import java.io.IOException;
import java.net.URI;


public class Main {
    private HttpServer server;
    private Repository repository;


    public Main(Repository repository) {
        this.repository = repository;

        startup();
    }


    public Main(String jdbcURL, String username, String password) {
        this(new H2(Jdbi.create(jdbcURL, username, password)));
    }


    static final String BASE_URI =
            "http://localhost:8080/api/";


    private void startServer() {
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

        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }


    private void startup() {
        startServer();

        ClassLoader loader = Main.class.getClassLoader();
        CLStaticHttpHandler docsHandler = new CLStaticHttpHandler(loader, "swagger-ui/");
        docsHandler.setFileCacheEnabled(false);

        ServerConfiguration cfg = server.getServerConfiguration();
        cfg.addHttpHandler(docsHandler, "/");
    }


    public void shutdown() {
        server.shutdownNow();
    }


    public static void main(String[] args) throws IOException, ParseException {
        Options options =
                new Options();

        options.addOption(new Option("dburl", true, "JDBC URL containing events table"));
        options.addOption(new Option("dbuser", true, "JDBC user"));
        options.addOption(new Option("dbpass", true, "JDBC user password"));
        options.addOption(new Option("help", "Print this message"));

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            (new HelpFormatter()).printHelp("Main", options);
        } else {
            Main main = new Main(
                    cmd.getOptionValue("db", "jdbc:h2:./h2-jersey-application/target/stream"),
                    cmd.getOptionValue("dbuser", "sa"),
                    cmd.getOptionValue("dbpassword", ""));

            System.out.println(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit enter to stop it...", BASE_URI));

            System.in.read();

            main.shutdown();
        }
    }
}
