package handler;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;
import java.io.IOException;
import java.util.HashMap;
public class WebSocketSessions {
    private static HashMap<Integer, HashMap<String, Session>> sessions = new HashMap<>();

    public void addsessiontoGame(int gameID, String authToken, Session session) {
        HashMap<String, Session> numgameSeshes = sessions.get(gameID);
        if (numgameSeshes == null) {
            numgameSeshes = new HashMap<>();
            sessions.put(gameID, numgameSeshes);
        }
        numgameSeshes.put(authToken, session);
    }

    public void removesessionfromGame(int gameID, String authToken) {
        HashMap<String, Session> numgameSeshes = sessions.get(gameID);
        if (numgameSeshes != null) {
            numgameSeshes.remove(authToken);
            if (numgameSeshes.isEmpty()) {
                sessions.remove(gameID);
            }
        }
    }

    public synchronized void removeSession(Session session) {
        for (HashMap<String, Session> gameSessions : sessions.values()) {
            gameSessions.values().removeIf(s -> s.equals(session));
        }
    }

    public void sendMessage(int gameID, ServerMessage message, String authToken) {
        HashMap<String, Session> numgameSeshes = sessions.get(gameID);
        if (numgameSeshes != null) {
            Session targetSession = numgameSeshes.get(authToken);
            if (targetSession != null && targetSession.isOpen()) {
                sendToSession(targetSession, message);
            }
        }
    }

    public void broadcastMessage(int gameID, ServerMessage message, String exceptThisAuthToken) {
        HashMap<String, Session> numgameSeshes = sessions.get(gameID);
        if (numgameSeshes != null) {
            for (HashMap.Entry<String, Session> entry : numgameSeshes.entrySet()) {
                String authToken = entry.getKey();
                if (!authToken.equals(exceptThisAuthToken)) {
                    Session targetSession = entry.getValue();
                    if (targetSession.isOpen()) {
                        sendToSession(targetSession, message);
                    }
                }
            }
        }
    }

    private void sendToSession(Session session, ServerMessage message) {
        RemoteEndpoint remote = session.getRemote();
        try {
            remote.sendString(new Gson().toJson(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
