package za.co.no9.ses8.application;

import io.javalin.Javalin;
import org.apache.commons.cli.*;
import org.jdbi.v3.core.Jdbi;
import za.co.no9.ses8.adaptors.API;
import za.co.no9.ses8.adaptors.repository.H2;
import za.co.no9.ses8.domain.ports.Repository;

import java.io.IOException;


public class Main {
    public static final String DEFAULT_BASE_URI =
            "http://localhost:8080/api/";

    private static final String DEFAULT_JDBC_URL =
            "jdbc:h2:./h2-jersey-application/target/stream";

    private static final String DEFAULT_JDBC_USER =
            "sa";

    private static final String DEFAULT_JDBC_PASS =
            "";

    private String baseURI;
    private Javalin server;
    private Repository repository;


    public Main(String baseURI, Repository repository) {
        this.baseURI = baseURI;
        this.repository = repository;

        startup();
    }


    public Main(Repository repository) {
        this(DEFAULT_BASE_URI, repository);
    }


    public Main(String baseURI, String jdbcURL, String username, String password) {
        this(baseURI, new H2(Jdbi.create(jdbcURL, username, password)));
    }


    public String getBaseURI() {
        return baseURI;
    }


    static Javalin startServer(Repository repository) {
        Javalin javalin = Javalin
                .create()
                .port(8080)
                .enableStaticFiles("swagger-ui/")
                .disableStartupBanner()
                .start();

        API.addEndpoints(javalin, repository);

        return javalin;
    }


    private void startup() {
        server = startServer(repository);
    }


    public void shutdown() {
        server.stop();
    }


    public static void main(String[] args) throws IOException, ParseException {
        Options options =
                new Options();

        options.addOption(new Option("uri", true, "The REST base URI"));
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
                    cmd.getOptionValue("uri", DEFAULT_BASE_URI),
                    cmd.getOptionValue("db", DEFAULT_JDBC_URL),
                    cmd.getOptionValue("dbuser", DEFAULT_JDBC_USER),
                    cmd.getOptionValue("dbpassword", DEFAULT_JDBC_PASS));

            System.out.println("Javalin app started - hit enter to stop it...");

            System.in.read();

            main.shutdown();
        }
    }
}
