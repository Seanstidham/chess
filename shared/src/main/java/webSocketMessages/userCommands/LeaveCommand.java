package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{

//    Command
//    LEAVE
//    Required Fields
//    Integer gameID
//    Description
//    Tells the server you are leaving the game so it will stop sending you notifications.

//Each user game command class must include the authToken field and the commandType field and should inherit from the UserGameCommand class.
// The commandType field must be set to the corresponding CommandType.
    int gameID;

    public LeaveCommand(int gameID, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
    }

    public int getGameID() {
        return gameID;
    }
}
