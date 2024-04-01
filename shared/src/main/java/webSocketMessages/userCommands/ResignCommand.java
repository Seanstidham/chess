package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{

//    Command
//    RESIGN
//    Required Fields
//    Integer gameID
//    Description
//    Forfeits the match and ends the game (no more moves can be made).

//Each user game command class must include the authToken field and the commandType field and should inherit from the UserGameCommand class.
// The commandType field must be set to the corresponding CommandType.

    int gameID;

    public ResignCommand(int gameID, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameID() {
        return gameID;
    }
}
