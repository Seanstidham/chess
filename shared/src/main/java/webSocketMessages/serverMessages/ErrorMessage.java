package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage{


//    Command
//    ERROR
//    Required Fields
//    String errorMessage
//    Description
//    This message is sent to a client when it sends an invalid command. The message must include the word Error.

    String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
