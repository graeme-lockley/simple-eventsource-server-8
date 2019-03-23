package za.co.no9.ses8.adaptors.api.javalin;

import com.google.gson.Gson;
import io.javalin.websocket.WsSession;
import za.co.no9.ses8.domain.Event;
import za.co.no9.ses8.domain.ports.Repository;
import za.co.no9.ses8.domain.ports.UnitOfWork;

import java.util.Optional;
import java.util.stream.Stream;

public class Session {
    private static final Gson gson =
            new Gson();

    private final WsSession wsSession;
    private final Repository repository;

    private Optional<Integer> fromID =
            Optional.empty();


    public Session(WsSession wsSession, Repository repository) {
        this.wsSession = wsSession;
        this.repository = repository;
    }

    public void close() {

    }


    public void ping() {
        if (fromID.isPresent()) {
            int pageSize =
                    1000;

            UnitOfWork unitOfWork =
                    repository.newUnitOfWork();

            while (true) {
                Stream<Event> events =
                        unitOfWork.events(fromID, pageSize);

                Optional<Integer> last =
                        fromID;

                events.forEach(event -> {
                    String message =
                            gson.toJson(event);

                    System.out.println(message);

                    wsSession.send(message);

                    fromID = Optional.of(event.id);
                });

                if (last.equals(fromID)) {
                    break;
                }
            }
        }
    }

    public void reset(String message) {
        fromID = Optional.of(Integer.parseInt(message));
        ping();
    }
}
