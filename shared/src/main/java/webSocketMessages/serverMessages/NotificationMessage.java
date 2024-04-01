package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage  {

//    Command
//    NOTIFICATION
//    Required Fields
//    String message
//    Description
//    This is a message meant to inform a player when another player made an action.

    String notificationMessage;

    public NotificationMessage(String notificationMessage) {
        super(ServerMessageType.NOTIFICATION);
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }
}
