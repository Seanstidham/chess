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
            handleWebSocketException(e);
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

        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION:
                NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                gameHandler.printMessage(notificationMessage);
                break;
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                gameHandler.updateGame(loadGameMessage);
                break;
            default:

                break;
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        sendMessage(new JoinPlayerCommand(gameID, teamColor, authToken));
    }

    public void joinObserver(int gameID, String authToken) {
        sendMessage(new JoinObserverCommand(gameID, authToken));
    }

    public void makeMove(int gameID, ChessMove move, String authToken) {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(gameID, move, authToken);
        sendMessage(makeMoveCommand);
    }

    public void leave(int gameID, String authToken) {
        sendMessage(new LeaveCommand(gameID, authToken));
        closeSession();
    }

    public void resign(int gameID, String authToken) {
        sendMessage(new ResignCommand(gameID, authToken));
    }

    private void sendMessage(UserGameCommand command) {
        try {
        session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            handleWebSocketException(e);
        }
    }

    private void closeSession() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                handleWebSocketException(e);
            }
        }
    }

    private void handleWebSocketException(Exception e) {

        e.printStackTrace();
    }
}
