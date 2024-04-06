package handler;
import service.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.userCommands.*;
import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final WebsocketService websocketService;
    private final Gson gson;
    public WebSocketHandler(WebsocketService websocketService) {
        this.websocketService = websocketService;
        this.gson = new Gson();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        if (gameCommand.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER) {
            JoinPlayerCommand joinPlayerCommand = gson.fromJson(message, JoinPlayerCommand.class);
            websocketService.joinPlayer(joinPlayerCommand.getAuthString(), joinPlayerCommand, session);
        } else if (gameCommand.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER) {
            JoinObserverCommand joinObserverCommand = gson.fromJson(message, JoinObserverCommand.class);
            websocketService.joinObserver(joinObserverCommand.getAuthString(), joinObserverCommand, session);
        } else if (gameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
            websocketService.makeMove(makeMoveCommand.getAuthString(), makeMoveCommand, session);
        } else if (gameCommand.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
            websocketService.leaveGame(leaveCommand.getAuthString(), leaveCommand, session);
        } else if (gameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
            websocketService.resignGame(resignCommand.getAuthString(), resignCommand, session);
        }
    }

}

