package handler;
import chess.*;
import dataAccess.*;
import model.GameData;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.userCommands.*;
import java.io.IOException;
import java.util.Objects;
@WebSocket
public class WebSocketHandler {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private WebSocketSessions sessions = new WebSocketSessions();
    private Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.removeSession(session);
    }

    @OnWebSocketError
    public void onError(Throwable error) {

    }




    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        if (gameCommand.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER) {
            JoinPlayerCommand joinPlayerCommand = gson.fromJson(message, JoinPlayerCommand.class);
            joinPlayer(joinPlayerCommand.getAuthString(), joinPlayerCommand, session);
        } else if (gameCommand.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER) {
            JoinObserverCommand joinObserverCommand = gson.fromJson(message, JoinObserverCommand.class);
            joinObserver(joinObserverCommand.getAuthString(), joinObserverCommand, session);
        } else if (gameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
            makeMove(makeMoveCommand.getAuthString(), makeMoveCommand, session);
        } else if (gameCommand.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
            leaveGame(leaveCommand.getAuthString(), leaveCommand, session);
        } else if (gameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
            resignGame(resignCommand.getAuthString(), resignCommand, session);
        }
    }

    private void joinPlayer(String authToken, JoinPlayerCommand joinPlayerCommand, Session session) throws IOException {
        try {
            sessions.addSessionToGame(joinPlayerCommand.getGameID(), joinPlayerCommand.getAuthString(), session);
            if (!isValidAuthToken(authToken)) {
                sessions.sendMessage(joinPlayerCommand.getGameID(), new ErrorMessage("Error joining game: Unauthorized"), authToken);
                return;
            }
            int gameID = joinPlayerCommand.getGameID();
            if (gameDAO.getGame(gameID) == null) {
                sessions.sendMessage(joinPlayerCommand.getGameID(), new ErrorMessage("Error joining game: Unauthorized"), authToken);
                return;
            }
            ChessGame game = gameDAO.getGame(gameID).game();
            ChessGame.TeamColor playerColor = joinPlayerCommand.getPlayerColor();
            LoadGameMessage notifiedMessage = new LoadGameMessage(game);

            String userName;
            if (playerColor == ChessGame.TeamColor.WHITE) {
                userName = gameDAO.getGame(gameID).whiteUsername();
            } else {
                userName = gameDAO.getGame(gameID).blackUsername();
            }

            if (!Objects.equals(userName, authDAO.getAuth(authToken).username())) {
                sessions.sendMessage(joinPlayerCommand.getGameID(), new ErrorMessage("Error joining game: Color already in use "), authToken);
                return;
            }
            String color;
            if (playerColor == ChessGame.TeamColor.WHITE) {
                color = "White";
            } else {
                color = "Black";
            }

            NotificationMessage notification = new NotificationMessage(userName + " has challenged you as the color " + color);
            sessions.sendMessage(gameID, notifiedMessage, authToken);
            sessions.broadcastMessage(gameID, notification, authToken);
        } catch (DataAccessException e) {
            throw new IOException(e);
        }
    }

    private void joinObserver(String authToken, JoinObserverCommand joinObserverCommand, Session session) throws IOException {
        try {
            sessions.addSessionToGame(joinObserverCommand.getGameID(), joinObserverCommand.getAuthString(), session);

            if (!isValidAuthToken(authToken)) {
                sessions.sendMessage(joinObserverCommand.getGameID(), new ErrorMessage("Error joining: Unauthorized"), authToken);
                return;
            }

            int gameID = joinObserverCommand.getGameID();
            if (gameDAO.getGame(gameID) == null) {
                sessions.sendMessage(joinObserverCommand.getGameID(), new ErrorMessage("Error joining: Unauthorized"), authToken);
                return;
            }
            ChessGame game = gameDAO.getGame(gameID).game();
            LoadGameMessage notificationToRootClient = new LoadGameMessage(game);

            String userName = authDAO.getAuth(authToken).username();

            NotificationMessage notification = new NotificationMessage(userName + " joined as observer.");

            sessions.sendMessage(gameID, notificationToRootClient, authToken);
            sessions.broadcastMessage(gameID, notification, authToken);
        } catch (DataAccessException e) {
            throw new IOException(e);
        }
    }

    private void makeMove(String authToken, MakeMoveCommand makeMoveCommand, Session session) throws IOException {
        try {
            String userName = authDAO.getAuth(authToken).username();
            int gameID = makeMoveCommand.getGameID();
            ChessGame game = gameDAO.getGame(gameID).game();
            ChessMove move = makeMoveCommand.getActualMove();
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            ChessGame.TeamColor userColor = null;

            sessions.addSessionToGame(makeMoveCommand.getGameID(), makeMoveCommand.getAuthString(), session);

            if (!isValidAuthToken(authToken)) {
                sessions.sendMessage(makeMoveCommand.getGameID(), new ErrorMessage("Error: Unauthorized"), authToken);
                return;
            }
            if (Objects.equals(gameDAO.getGame(gameID).whiteUsername(), userName)) {
                userColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(gameDAO.getGame(gameID).blackUsername(), userName)) {
                userColor = ChessGame.TeamColor.BLACK;
            }

            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                sessions.sendMessage(gameID, new ErrorMessage("Invalid move"), authToken);
                return;
            }

            if (game.isInCheck(ChessGame.TeamColor.BLACK) || game.isInCheck(ChessGame.TeamColor.WHITE)) {
                sessions.sendMessage(gameID, new NotificationMessage("Check"), authToken);
                sessions.broadcastMessage(gameID, new NotificationMessage("Check"), authToken);
                gameDAO.updatedGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(), gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), game));
            }

            if (game.isInCheckmate(ChessGame.TeamColor.BLACK) || game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                handleGameEnd("Checkmate", game, gameID, sessions, authToken, gameDAO);
                return;
            }

            if (game.isInStalemate(ChessGame.TeamColor.BLACK) || game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                handleGameEnd("Stalemate", game, gameID, sessions, authToken, gameDAO);
                return;
            }

            if (piece.getTeamColor() != userColor) {
                sessions.sendMessage(gameID, new ErrorMessage("Piece is immovable"), authToken); //Just like your mother gottem
                return;
            }

            gameDAO.updatedGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(), gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), game));

            LoadGameMessage notificationToRootClient = new LoadGameMessage(game);

            NotificationMessage notification = new NotificationMessage(userName + " moved to " + positionToString(makeMoveCommand.getActualMove().getEndPosition()));

            sessions.sendMessage(gameID, notificationToRootClient, authToken);
            sessions.broadcastMessage(gameID, notificationToRootClient, authToken);
            sessions.broadcastMessage(gameID, notification, authToken);
        } catch (DataAccessException e) {
            throw new IOException(e);
        }
    }
    private void handleGameEnd(String message, ChessGame game, int gameID, WebSocketSessions sessions, String authToken, GameDAO gameDAO) {
        sessions.sendMessage(gameID, new NotificationMessage(message), authToken);
        sessions.broadcastMessage(gameID, new NotificationMessage(message), authToken);
        game.setTeamTurn(null);
        LoadGameMessage finalGame = new LoadGameMessage(game);
        sessions.sendMessage(gameID, finalGame, authToken);
        sessions.broadcastMessage(gameID, finalGame, authToken);
        try {
            gameDAO.updatedGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(), gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), game));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleGameAction(String authToken, int gameID, String userName, NotificationMessage notification, Session session) throws IOException {
        sessions.addSessionToGame(gameID, authToken, session);

        if (!isValidAuthToken(authToken)) {
            sessions.sendMessage(gameID, new ErrorMessage("Error: Unauthorized"), authToken);
            return;
        }

        sessions.broadcastMessage(gameID, notification, authToken);
        sessions.removeSessionFromGame(gameID, authToken);
        sessions.removeSession(session);
    }


    private void leaveGame(String authToken, LeaveCommand leaveCommand, Session session) throws IOException {
        String userName;
        try {
            userName = authDAO.getAuth(authToken).username();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        int gameID = leaveCommand.getGameID();
        String notificationMessage = userName + " has left the game.";
        NotificationMessage notification = new NotificationMessage(notificationMessage);

        try {
            if (Objects.equals(gameDAO.getGame(gameID).whiteUsername(), userName)) {
                gameDAO.updatedGame(gameID, new GameData(gameID, null, gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), gameDAO.getGame(gameID).game()));
            } else if (Objects.equals(gameDAO.getGame(gameID).blackUsername(), userName)) {
                gameDAO.updatedGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(), null, gameDAO.getGame(gameID).gameName(), gameDAO.getGame(gameID).game()));
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        handleGameAction(authToken, gameID, userName, notification, session);
    }


    private void resignGame(String authToken, ResignCommand resignCommand, Session session) throws IOException {
        String userName;
        try {
            userName = authDAO.getAuth(authToken).username();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        int gameID = resignCommand.getGameID();
        String notificationMessage = userName + " has given up.";
        NotificationMessage notification = new NotificationMessage(notificationMessage);

        // 🫸🔴🔵🫷🤌🫴☰🟣 Hollow Purple
        try {
            if (Objects.equals(gameDAO.getGame(gameID).whiteUsername(), userName) || Objects.equals(gameDAO.getGame(gameID).blackUsername(), userName)) {
                handleGameAction(authToken, gameID, userName, notification, session);
            } else {
                sessions.sendMessage(gameID, new ErrorMessage("Cannot resign as an Observer."), authToken);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidAuthToken(String authToken) throws IOException {
        try {
            return authDAO.getAuth(authToken) != null;
        } catch (DataAccessException e) {
            throw new IOException(e);
        }
    }

    private String positionToString(ChessPosition position) {
        char columnChar = (char) ('a' + position.getColumn() - 1);
        char rowChar = (char) ('1' + position.getRow() - 1);
        return Character.toString(columnChar) + rowChar;
    }
}
