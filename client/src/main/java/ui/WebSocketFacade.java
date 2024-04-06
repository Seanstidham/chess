package ui;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.MessageHandler;

public class WebSocketFacade extends Endpoint{
    private Session session;
    private GameHandler gameHandler;

    public WebSocketFacade(String url, GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        try {
            URI websocketURI = new URI(url.replace("http", "ws") + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, websocketURI);
            initMessageHandler();
        } catch (URISyntaxException | DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    private void initMessageHandler() {
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                handleMessage(message);
            }
        });
    }

    private void handleMessage(String message) {
        Gson gson = new Gson();
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
            gameHandler.printMessage(notificationMessage);
        } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
            gameHandler.updateGame(loadGameMessage);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig){
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        sendMessage(new JoinPlayerCommand(gameID, teamColor, authToken));
    }

    public void joinObserver(int gameID, String authToken) {
        sendMessage(new JoinObserverCommand(gameID, authToken));
    }

    public void makeMove(int gameID, ChessMove move, String authToken) {
        sendMessage(new MakeMoveCommand(gameID, move, authToken));
    }

    public void leave(int gameID, String authToken) throws IOException {
        sendMessage(new LeaveCommand(gameID, authToken));
        this.session.close();
    }

    public void resign(int gameID, String authToken) {
        sendMessage(new ResignCommand(gameID, authToken));
    }

    private void sendMessage(UserGameCommand command) {
        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
