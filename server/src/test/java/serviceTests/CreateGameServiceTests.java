package serviceTests;
import dataAccess.*;
import service.CreateGameService;
import model.AuthData;
import request.CreateGameRequest;
import result.CreateGameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private CreateGameService createGameService;


    @BeforeEach
    public void setmeupMordecai() {
        //ik nobody reads any of my comments, but if you understand the "Set me up, Mordecai!" reference you've earned my respect
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
        this.createGameService = new CreateGameService(gameDAO, authDAO);

        //ik im going fast but i just figured out i needed to move the init setup try to the positive test because it has 2 parameters in CreateGame


    }
    @Test
    public void negativeCreateGameServiceTest() {
        String invalidAuthToken = "fakeToken";
        //hopefully my names dont mess with the naminng conventions thing
        CreateGameRequest badEndingrequest = new CreateGameRequest("schizoGame"); //get it?! cause it isnt real
        CreateGameResult badEndingresult = null;                                            //fun fact schizophrenia usually develops in your early 20s
        try {
            badEndingresult = createGameService.createGame(invalidAuthToken, badEndingrequest);
        } catch (DataAccessException e) {
            fail("God creation Exception: " + e.getMessage());
        }

        assertNull(badEndingresult.gameID());
        assertNotNull(badEndingresult);
        assertNotNull(badEndingresult.message());
        assertEquals("Error: unauthorized", badEndingresult.message());
    }
    @Test
    public void positiveCreateGameServiceTest() {

        AuthData authToken = new AuthData("KumaToken", "Kuma");
        try {
            authDAO.createAuth(authToken);
        } catch (DataAccessException e) {
            fail("Setup Exception: " + e.getMessage());
        }
        CreateGameRequest goodEndingrequest = new CreateGameRequest("schizoGame");

        CreateGameResult goodEndingresult = null;
        try {
            goodEndingresult = createGameService.createGame(authToken.authToken(), goodEndingrequest);
        } catch (DataAccessException e) {
            fail("God creation Exception: " + e.getMessage());
        }

        assertNull(goodEndingresult.message());
        assertNotNull(goodEndingresult);
        assertNotNull(goodEndingresult.gameID());
    }
}
