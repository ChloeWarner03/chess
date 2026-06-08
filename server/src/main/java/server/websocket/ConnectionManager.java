package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

//this is working on broadcasting to the rooms
//petshop only had 1 session but for chess I need mutiple for each game
//this is in the 18 minute video
public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<Session, Session>> connections = new ConcurrentHashMap<>();

    public void add(int chessID,Session session) {
        if (connections.get(chessID) == null) {
            connections.put(chessID, new ConcurrentHashMap<>());
        }
        connections.get(chessID).put(session, session);
    }

    public void remove(int chessID,Session session) {
        if (connections.get(chessID) != null) {
            connections.get(chessID).remove(session);
        }
    }

    public void broadcast(int chessID, ServerMessage message, Session excludeSession) throws IOException {
        String msg = new Gson().toJson(message);
        for (Session c : connections.get(chessID).values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}