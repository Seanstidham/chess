package dataAccess;
import com.google.gson.GsonBuilder;
import model.GameData;
import chess.*;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import java.util.Objects;

public class SQLGameDAO implements GameDAO {
//    The easiest way to store the state of a ChessGame in MySQL is to serialize it to a JSON string, and then store the string in your database. Whenever your server needs to update the state of a game, it should:
//
//    Select the game’s state (JSON string) from the database
//    Deserialize the JSON string to a ChessGame Java object
//    Update the state of the ChessGame object
//    Re-serialize the Chess game to a JSON string
//    Update the game’s JSON string in the database

    //I want to get a lot done, so imma cook
    private final Gson gson;

    public SQLGameDAO() throws DataAccessException {
        gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        configureDatabase();
    }
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String query : CREATE_GAMES_TABLE_QUERY) {
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error configuring tables: " + ex.getMessage());
        }
    }
    private static final String[] CREATE_GAMES_TABLE_QUERY = {
            "CREATE TABLE IF NOT EXISTS game (" +
                    "gameID INT PRIMARY KEY, " +
                    "whiteUsername VARCHAR(50), " +
                    "blackUsername VARCHAR(50), " +
                    "gameName VARCHAR(50) NOT NULL, " +
                    "game TEXT NOT NULL" +
                    ")"
    };

    private String serializeGame(ChessGame game) {
        JsonObject gameJson = new JsonObject();

        gameJson.add("chessBoard", gson.toJsonTree(game.getBoard()));

        if(game.getTeamTurn() == null){
            gameJson.addProperty("turn", "null");
        } else {
            gameJson.addProperty("turn", game.getTeamTurn().toString());
        }

        if (game.getLastMove() != null) {
            gameJson.add("lastMove", gson.toJsonTree(game.getLastMove()));
        }
        return gameJson.toString();
    }
    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection();
        PreparedStatement magicConch = conn.prepareStatement("INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
            magicConch.setInt(1, gameData.gameID());
            magicConch.setString(2, gameData.whiteUsername());
            magicConch.setString(3, gameData.blackUsername());
            magicConch.setString(4, gameData.gameName());
            magicConch.setString(5, serializeGame(gameData.game()));
            magicConch.executeUpdate();
            return gameData;
        } catch (SQLException e) {
            throw new DataAccessException("Error poofing game into existence: " + e.getMessage());
        }
    }
    private ChessGame deserializeGame(String json) {
        JsonObject gameJson = gson.fromJson(json, JsonObject.class);

        ChessBoard chessBoard = gson.fromJson(gameJson.get("chessBoard"), ChessBoard.class);

        String color = gameJson.get("turn").getAsString();
        ChessGame.TeamColor turn = null;

        if(Objects.equals(color, "WHITE")) {
            turn = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(color, "BLACK")) {
            turn = ChessGame.TeamColor.BLACK;
        }

        ChessMove thelastMove = null;
        if (gameJson.has("lastMove")) {
            thelastMove = gson.fromJson(gameJson.get("lastMove"), ChessMove.class);
        }

        ChessGame game = new ChessGame();
        game.setBoard(chessBoard);
        game.setTeamTurn(turn);
        game.setLastMove(thelastMove);

        return game;
    }
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement magicConch = conn.prepareStatement("SELECT whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?")) {
            magicConch.setInt(1, gameID);
            try (ResultSet resultSet = magicConch.executeQuery()) {
                if (resultSet.next()) {
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String json = resultSet.getString("game");
                    ChessGame game = deserializeGame(json);
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error nabbin the game: " + e.getMessage());
        }
    }
    @Override
    public GameData[] listGames() throws DataAccessException {
        //need a list data structure to hold the data here
        List<GameData> alloftheGames = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement magicConch = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game")) {
            try (ResultSet resultSet = magicConch.executeQuery()) {
                for (; resultSet.next(); ) {
                    int gameID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String json = resultSet.getString("game");
                    ChessGame game = deserializeGame(json);
                    alloftheGames.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error trying to list em all: " + e.getMessage());
        }
        return alloftheGames.toArray(new GameData[0]);
    }
    @Override
    public void updatedGame(int gameID, GameData updatedGameData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement magicConch = conn.prepareStatement("UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?")) {
            magicConch.setString(1, updatedGameData.whiteUsername());
            magicConch.setString(2, updatedGameData.blackUsername());
            magicConch.setString(3, updatedGameData.gameName());
            magicConch.setString(4, serializeGame(updatedGameData.game()));
            magicConch.setInt(5, gameID);
            magicConch.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement magicConch = conn.prepareStatement("TRUNCATE TABLE game")) {
            magicConch.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error nuking game data: " + e.getMessage());
        }
    }


}
