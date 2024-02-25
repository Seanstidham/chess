package service;
import request.JoinGameRequest;
import result.JoinGameResult;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;
import model.GameData;


public class JoinGameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        if (!isValidAuthToken(authToken)) {
            return new JoinGameResult("Error: unauthorized");
        }
        GameData game = gameDAO.getGame(request.gameID());
        String playerColor = request.playerColor();
        if (("WHITE".equalsIgnoreCase(playerColor) || "BLACK".equalsIgnoreCase(playerColor))) {
            if ((game.whiteUsername() != null && "WHITE".equalsIgnoreCase(playerColor)) || (game.blackUsername() != null && "BLACK".equalsIgnoreCase(playerColor))) {
                return new JoinGameResult("Error: already taken");
            }
            gameDAO.updatedGame(request.gameID(), game.actualOpponent(playerColor, authDAO.getAuth(authToken).username()));
        } else {
            gameDAO.updatedGame(request.gameID(), game.spectator());
        }
        return new JoinGameResult(null);
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
