package service;
import result.ListGamesResult;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;
import model.GameData;

public class ListGamesService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        if (!isValidAuthToken(authToken)) {
            return new ListGamesResult(null, "Error: unauthorized");
        }

        GameData[] games = gameDAO.listGames();
        return new ListGamesResult(games, null);
    }


    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
