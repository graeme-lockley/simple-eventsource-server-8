package za.co.no9.ses8.adaptors.api.javalin;

import io.javalin.Javalin;
import io.javalin.websocket.WsHandler;
import za.co.no9.ses8.adaptors.repository.InMemory;
import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.io.IOException;
import java.util.Random;
import java.util.function.Consumer;


public class Main {
    static final String BASE_URI =
            "http://localhost:8080/api/";


    static Javalin startServer(Repository repository) {
        Consumer<WsHandler> wsHandlerConsumer = Bob.invoke();

        Javalin javalin = Javalin
                .create()
                .port(8080)
                .enableStaticFiles("/")
                .ws("/websocket/events", wsHandlerConsumer)
                .disableStartupBanner()
                .start();

        API.addEndpoints(javalin, repository);

        return javalin;
    }

    public static void main(String[] args) throws IOException {
        final Repository repository =
                new InMemory();

        insertRows(repository);

        final Javalin server =
                startServer(repository);

        System.out.println("Server running - hit enter to stop it...");

        System.in.read();
        server.stop();
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

    private static class Bob {
        private static Consumer<WsHandler> invoke() {
            return ws -> {
                ws.onConnect(session -> System.out.println("Connected: [" + session.getId() + "]"));
                ws.onMessage((session, message) -> {
                    System.out.println("Received: " + session.getId() + ": " + message);
                    session.getRemote().sendString("Echo: " + message + message);
                });
                ws.onClose((session, statusCode, reason) -> System.out.println("Closed: " + session.getId()));
                ws.onError((session, throwable) -> System.out.println("Errored: " + session.getId()));
            };
        }
    }
}
