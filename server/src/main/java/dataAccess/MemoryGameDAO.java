package dataAccess;
import model.GameData;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    //same way as Auth Memory
    private HashMap<Integer, GameData> fortniteMap = new HashMap<>();

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        if (fortniteMap.containsKey(game.gameID())) {
            throw new DataAccessException("Username already exists");
        } else {
            fortniteMap.put(game.gameID(), game);
            return game;
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return fortniteMap.get(gameID);
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return fortniteMap.values().toArray(new GameData[0]);
    }

    @Override
    public void updatedGame(int gameID, GameData updatedGameData) throws DataAccessException {
        fortniteMap.put(gameID, updatedGameData);
    }

    @Override
    public void clear() throws DataAccessException {
        fortniteMap.clear();
    }

}
