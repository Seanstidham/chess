package service;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
import chess.ChessGame;
public class CreateGameService {
    //okay I want to get the create, list and join game completely done today
    // okay to implement imma need auth and game, then imma need a way to show how many games there are and store that
    //imma also have to make a request and result file, imma make all the files rn in fact
        private AuthDAO authDAO;
        private GameDAO gameDAO;
        private int gameCounter;

        public CreateGameService(GameDAO gameDAO, AuthDAO authDAO) {
            this.gameDAO = gameDAO;
            this.authDAO = authDAO;
            this.gameCounter = 0;
        }

        public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
            try {
                if(authToken == null) {
                    return new CreateGameResult(null, "Error: unauthorized");
                }

                if (!isValidAuthToken(authToken)) {
                    return new CreateGameResult(null, "Error: unauthorized");
                }

                if (request == null || request.gameName() == null) {
                    return new CreateGameResult(null, "Error: bad request");
                }

                ChessGame chessGame = new ChessGame();

                GameData newGame = new GameData(gameCounter, null,null, request.gameName(), chessGame);
                GameData createdGame = gameDAO.createGame(newGame);

                gameCounter++;

                return new CreateGameResult(createdGame.gameID(), null);
            } catch (DataAccessException e) {
                throw e;
            }
        }

        private boolean isValidAuthToken(String authToken) throws DataAccessException {
            return authDAO.getAuth(authToken) != null;
        }


}
