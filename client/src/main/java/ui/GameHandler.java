package ui;
import chess.ChessGame;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;

public interface GameHandler {
    void updateGame(LoadGameMessage game);
    void printMessage(NotificationMessage message);
}
