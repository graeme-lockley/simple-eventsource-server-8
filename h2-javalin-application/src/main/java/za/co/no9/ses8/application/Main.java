package za.co.no9.ses8.application;

import io.javalin.Javalin;
import org.apache.commons.cli.*;
import org.jdbi.v3.core.Jdbi;
import za.co.no9.ses8.adaptors.repository.H2;
import za.co.no9.ses8.adaptors.api.javalin.API;
import za.co.no9.ses8.domain.ports.Repository;

import java.io.IOException;


public class Main {
    public static final int DEFAULT_PORT =
            8080;

    private static final String DEFAULT_JDBC_URL =
            "jdbc:h2:./h2-jersey-application/target/stream";

    private static final String DEFAULT_JDBC_USER =
            "sa";

    private static final String DEFAULT_JDBC_PASS =
            "";

    private int port;
    private Javalin server;
    private Repository repository;


    public Main(int port, Repository repository) {
        this.port = port;
        this.repository = repository;

        startup();
    }


    public Main(int port, String jdbcURL, String username, String password) {
        this(port, new H2(Jdbi.create(jdbcURL, username, password)));
    }


    static Javalin startServer(Repository repository, int port) {
        Javalin javalin = Javalin
                .create()
                .port(port)
                .disableStartupBanner()
                .start();

        API.addEndpoints(javalin, repository);

        return javalin;
    }


    private void startup() {
        server = startServer(repository, port);
    }


    public void shutdown() {
        server.stop();
    }


    public static void main(String[] args) throws IOException, ParseException {
        Options options =
                new Options();

        options.addOption(new Option("port", true, "API port"));
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
                    Integer.parseInt(cmd.getOptionValue("port", Integer.toString(DEFAULT_PORT))),
                    cmd.getOptionValue("db", DEFAULT_JDBC_URL),
                    cmd.getOptionValue("dbuser", DEFAULT_JDBC_USER),
                    cmd.getOptionValue("dbpassword", DEFAULT_JDBC_PASS));

            System.out.println("Javalin app started - hit enter to stop it...");

            System.in.read();

            main.shutdown();
        }
    }
}
