package za.co.no9.ses8.adaptors.api.javalin;

import io.javalin.websocket.WsHandler;
import io.javalin.websocket.WsSession;
import za.co.no9.ses8.domain.Observer;
import za.co.no9.ses8.domain.ports.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WebsocketAPI implements Observer {
    private final Map<String, Session> sessions =
            new HashMap<>();

    private final Repository repository;


    public WebsocketAPI(Repository repository) {
        this.repository = repository;
    }


    public Consumer<WsHandler> invoke() {
        return ws -> {
            ws.onConnect(this::connect);
            ws.onMessage(this::message);
            ws.onClose(this::close);
            ws.onError(this::error);
        };
    }


    private synchronized void connect(WsSession wsSession) {
        System.out.println("WsConnect: " + wsSession.getId());

        sessions.put(wsSession.getId(), new Session(wsSession, repository));
    }


    private void message(WsSession wsSession, String message) {
        System.out.println("WsMessage: " + wsSession.getId() + ": " + message);

        Session session =
                sessions.get(wsSession.getId());

        if (session == null) {
            System.err.println("WsMessage: "+ wsSession.getId() + ": " + message + ": Unknown session ID");
        } else {
            session.reset(message);
        }
    }


    private synchronized void close(WsSession wsSession, int statusCode, String reason) {
        System.out.println("WeClose: " + wsSession.getId() + ": " + statusCode + ": " + reason);

        Session session =
                sessions.get(wsSession.getId());

        if (session != null) {
            session.close();
            sessions.remove(wsSession.getId());
        }
    }


    private synchronized void error(WsSession wsSession, Throwable throwable) {
        System.out.println("WsError: " + wsSession.getId() + ": " + throwable.getMessage());
    }


    @Override
    public synchronized void ping() {
        sessions.forEach((key, session) -> session.ping());
    }
}
