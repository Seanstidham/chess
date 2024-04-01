package webSocketMessages.serverMessages;

public class LoadGameMessage extends ServerMessage {

//    Command
//    LOAD_GAME
//    Required Fields
//    game (can be any type, just needs to be called game)
//    Description
//    Used by the server to send the current game state to a client. When a client receives this message, it will redraw the chess board.

    Object game;

    public LoadGameMessage(Object game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Object getGame() {
        return game;
    }
}
