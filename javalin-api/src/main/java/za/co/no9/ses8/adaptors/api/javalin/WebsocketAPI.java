package za.co.no9.ses8.adaptors.api.javalin;

import io.javalin.websocket.WsHandler;
import io.javalin.websocket.WsSession;
import za.co.no9.ses8.domain.Observer;
import za.co.no9.ses8.domain.Services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WebsocketAPI implements Observer {
    private final Map<String, Session> sessions =
            new HashMap<>();

    private final Services services;

    private final PostOffice postOffice =
            new PostOffice(10);


    public WebsocketAPI(Services services) {
        this.services = services;
        services.registerObserver(this);
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

        String id =
                wsSession.getId();

        sessions.put(id, new Session(postOffice.mailbox(id), wsSession, services));
    }


    private void message(WsSession wsSession, String message) {
        System.out.println("WsMessage: " + wsSession.getId() + ": " + message);

        Session session =
                sessions.get(wsSession.getId());

        if (session == null) {
            System.err.println("WsMessage: " + wsSession.getId() + ": " + message + ": Unknown session ID");
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