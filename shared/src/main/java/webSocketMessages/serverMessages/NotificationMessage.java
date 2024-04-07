package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage  {

//    Command
//    NOTIFICATION
//    Required Fields
//    String message
//    Description
//    This is a message meant to inform a player when another player made an action.

    String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getNotificationMessage() {
        return message;
    }
}
