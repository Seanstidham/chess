package webSocketMessages.userCommands;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{

//    Command
//    MAKE_MOVE
//    Required Fields
//    Integer gameID, ChessMove move
//    Description
//    Used to request to make a move in a game.

//Each user game command class must include the authToken field and the commandType field and should inherit from the UserGameCommand class.
// The commandType field must be set to the corresponding CommandType.

    int gameID;
    ChessMove move;

    public MakeMoveCommand(int gameID, ChessMove move, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}
