package webSocketMessages.userCommands;
import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand{

//    Command
//    JOIN_PLAYER
//    Required Fields
//    Integer gameID, ChessGame.TeamColor playerColor
//    Description
//    Used for a user to request to join a game.
    int gameID;
    ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(int gameID, ChessGame.TeamColor playerColor, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
