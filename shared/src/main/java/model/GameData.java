package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    //Verifies that the specified game exists, and, if a color is specified, adds the caller as the requested color to the game.
    // If no color is specified the user is joined as an observer.
    public GameData actualOpponent(String playerColor, String username) {
        if ("WHITE".equalsIgnoreCase(playerColor)) {
            return new GameData(gameID, username, blackUsername, gameName, game);
        } else if ("BLACK".equalsIgnoreCase(playerColor)) {
            return new GameData(gameID, whiteUsername, username, gameName, game);
        } else {
            return this;
        }
    }
    public GameData spectator() {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
