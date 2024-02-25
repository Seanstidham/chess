package serviceTests;
import dataAccess.*;
import service.ListGamesService;
import model.AuthData;
import model.GameData;
import result.ListGamesResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ListGamesService listGamesService;

    @BeforeEach
    public void setmeupMordecai() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        listGamesService = new ListGamesService(gameDAO, authDAO);
    }
    @Test
    public void negativeListGamesServiceTest() {
        String authToken = "fakeAuthToken";

        ListGamesResult badEndingresult = null;
        try {
            badEndingresult = listGamesService.listGames(authToken);
        } catch (DataAccessException e) {
            fail("List it Exception: " + e.getMessage());
        }
        assertNull(badEndingresult.games());
        assertNotNull(badEndingresult);
        assertNotNull(badEndingresult.message());
        assertEquals("Error: unauthorized", badEndingresult.message());
    }
    @Test
    public void positiveListGamesServiceTest() {
        AuthData authToken = new AuthData("captainToken", "Usopp");
        try{
            authDAO.createAuth(authToken);
        } catch (DataAccessException e) {
            fail("Set up Exception: " + e.getMessage());
        }

        GameData game1 = new GameData(1, "Usopp", null, "Game 1", null);
        GameData game2 = new GameData(2, "Franky", null, "Game 2", null);
        try {
            gameDAO.createGame(game1);
            gameDAO.createGame(game2);
        } catch (DataAccessException e) {
            fail("Set up Exception: " + e.getMessage());
        }
        ListGamesResult goodEndingresult = null;
        try {
            goodEndingresult = listGamesService.listGames(authToken.authToken());
        } catch (DataAccessException e) {
            fail("Exception...but a good one: " + e.getMessage());
        }
        assertNull(goodEndingresult.message());
        assertNotNull(goodEndingresult);
        assertNotNull(goodEndingresult.games());
        assertEquals(2, goodEndingresult.games().length);
    }
}
