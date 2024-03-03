package databaseTests;
import model.GameData;
import dataAccess.*;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class MemoryGameDAODatabaseTest {
    GameDAO gameDAO;
    @BeforeEach
    public void setmeupMordecai() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        gameDAO.clear();
    }
    @AfterEach
    public void wreckIt() {
        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            fail("Exception with nuking: " + e.getMessage());
        }
    }
    @Test
    public void negativeCreateGameTest() throws DataAccessException {
        GameData badendinggameData1 = new GameData(1, "Player1", "Player2", "Game 1", null);

        gameDAO.createGame(badendinggameData1);

        GameData badendinggameData2 = new GameData(1, "Player3", "Player4", "Game 2", null);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(badendinggameData2));
    }
    @Test
    public void positiveCreateGameTest() throws DataAccessException {
        GameData goodendinggameData = new GameData(1, "Player1", "Player2", "Game 1", null);

        gameDAO.createGame(goodendinggameData);

        GameData yoinkedGameData = gameDAO.getGame(1);

        assertEquals(goodendinggameData.gameID(), yoinkedGameData.gameID());
        assertEquals(goodendinggameData.whiteUsername(), yoinkedGameData.whiteUsername());
        assertEquals(goodendinggameData.blackUsername(), yoinkedGameData.blackUsername());
        assertEquals(goodendinggameData.gameName(), yoinkedGameData.gameName());
    }
    @Test
    public void negativeGetGameTest() throws DataAccessException {
        int schizoGame = -1;
        assertNull(gameDAO.getGame(schizoGame));
    }
    @Test
    public void positiveGetGameTest() throws DataAccessException {
        ChessGame testGame = new ChessGame();
        GameData goodendingtestData = new GameData(1, "white", "black", "Test Game", testGame);
        gameDAO.createGame(goodendingtestData);

        GameData yoinkedData = gameDAO.getGame(1);

        assertNotNull(yoinkedData);
        assertEquals(1, yoinkedData.gameID());
        assertEquals("white", yoinkedData.whiteUsername());
        assertEquals("black", yoinkedData.blackUsername());
        assertEquals("Test Game", yoinkedData.gameName());
        assertNotNull(yoinkedData.game());
    }
    @Test
    public void negativeListGamesTest() throws DataAccessException {
        gameDAO.clear();

        GameData[] allthegames = gameDAO.listGames();

        assertNotNull(allthegames);
        assertEquals(0, allthegames.length);
    }
    @Test
    public void positiveListGamesTest() throws DataAccessException {
        ChessGame goodendingtestGame = new ChessGame();
        GameData goodendingtestData = new GameData(1, "white1", "black1", "Test Game 1", goodendingtestGame);
        gameDAO.createGame(goodendingtestData);

        ChessGame goodendingtestGame1 = new ChessGame();
        GameData goodendingtestData1 = new GameData(2, "white2", "black2", "Test Game 2", goodendingtestGame1);
        gameDAO.createGame(goodendingtestData1);

        GameData[] games = gameDAO.listGames();

        assertNotNull(games);
        assertEquals(2, games.length);
        assertEquals(games[0].gameID(),goodendingtestData.gameID());
        assertEquals(games[1].gameID(), goodendingtestData1.gameID());
    }
    @Test
    public void negativeUpdatedGameTest() throws DataAccessException {
        ChessGame badendingtestGame = new ChessGame();
        GameData badendingtestData = new GameData(999, "Vegapunk", "Saturn", "Test Game", badendingtestGame);

        gameDAO.createGame(badendingtestData);
        assertNotNull(gameDAO.getGame(999));
    }
    @Test
    public void positiveUpdatedGameTest() throws DataAccessException {
        ChessGame goodendingtestGame = new ChessGame();
        GameData goodendingtestData = new GameData(1, "Vegapunk", "Saturn", "Test Game", goodendingtestGame);
        gameDAO.createGame(goodendingtestData);

        GameData newgoodendingData = new GameData(1, "Vegapunk", "Saturn", "New", goodendingtestGame);
        gameDAO.updatedGame(1, newgoodendingData);

        GameData goodendingupdatedData = gameDAO.getGame(1);

        assertNotNull(goodendingupdatedData);
        assertEquals("New", goodendingupdatedData.gameName());
    }
    @Test
    public void positiveClearTest() throws DataAccessException {
        GameData testGame1 = new GameData(1, "P1", "P2", "G1", null);
        GameData testGame2 = new GameData(2, "P3", "P4", "G2", null);

        gameDAO.createGame(testGame1);
        gameDAO.createGame(testGame2);

        assertEquals(2, gameDAO.listGames().length);

        gameDAO.clear();

        assertEquals(0,gameDAO.listGames().length);
    }

}
