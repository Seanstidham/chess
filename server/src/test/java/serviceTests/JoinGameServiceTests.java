package serviceTests;
import dataAccess.*;
import service.JoinGameService;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;
import result.JoinGameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class JoinGameServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private JoinGameService joinGameService;

    @BeforeEach
    public void setmeupMordecai() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        joinGameService = new JoinGameService(gameDAO, authDAO);

        GameData testGame = new GameData(1, "Robin", null, "Ohara", null);
        try {
            gameDAO.createGame(testGame);
        } catch (DataAccessException e) {
            fail("Setting up with an Exception: " + e.getMessage());
        }
    }
    @Test
    public void negativeJoinGameServiceTest() throws DataAccessException {
        AuthData authToken = new AuthData("fishman", "Jinbe");
        authDAO.createAuth(authToken);

        JoinGameRequest badEndingrequest = new JoinGameRequest("WHITE", 1);
        JoinGameResult badEndingresult = joinGameService.joinGame(authToken.authToken(), badEndingrequest);

        assertNotNull(badEndingresult);
        assertNotNull(badEndingresult.message());
        assertEquals("Error: already taken", badEndingresult.message());
    }
    @Test
    public void positiveJoinGameServiceTest() throws DataAccessException {
        AuthData authToken = new AuthData("demonchild", "Robin");
        authDAO.createAuth(authToken);

        JoinGameRequest goodEndingrequest = new JoinGameRequest("BLACK", 1);
        JoinGameResult goodEndingresult = joinGameService.joinGame(authToken.authToken(), goodEndingrequest);

        assertNotNull(goodEndingresult);
        assertNull(goodEndingresult.message());
    }
}
