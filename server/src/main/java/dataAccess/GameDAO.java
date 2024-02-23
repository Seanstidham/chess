package dataAccess;

import model.GameData;

public interface GameDAO {
    //okay now with this one we cant delete games but we do need to list them
    //so itll be more of a CRLU than CRUD i think on this one, at least going off the github specs
    GameData createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    void updatedGame(int gameID, GameData updatedGameData) throws DataAccessException;

    void clear() throws DataAccessException;

}
