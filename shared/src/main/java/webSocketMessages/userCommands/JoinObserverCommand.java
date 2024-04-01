package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand{

//    Command
//    JOIN_OBSERVER
//    Required Fields
//    Integer gameID
//    Description
//    Used to request to start observing a game.

//Each user game command class must include the authToken field and the commandType field and should inherit from the UserGameCommand class.
// The commandType field must be set to the corresponding CommandType.

    int gameID;

    public JoinObserverCommand(int gameID, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public int getGameID() {
        return gameID;
    }
}
