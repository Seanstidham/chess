package serviceTests;
import dataAccess.*;
import service.ClearApplicationService;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearApplicationServiceTests {
    //pretty similar to the register tests
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ClearApplicationService clearApplicationService;

    @BeforeEach
    public void setmeupMordecai() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        clearApplicationService = new ClearApplicationService(authDAO, gameDAO, userDAO);
    }
    //clear is the only test we just need a positive
    @Test
    public void positiveClearApplicationServiceTest() {
        try {
            userDAO.createUser(new UserData("Luffy", "GomuGomuno", "Luffy@example.com"));
            userDAO.createUser(new UserData("Zoro", "3swordstyle", "Zoro@example.com"));
            gameDAO.createGame(new GameData(1, "Luffy", null, "1st Game", null));
            authDAO.createAuth(new AuthData("authToken", "Luffy"));
        } catch (DataAccessException e) {
            fail("Clear Exception: " + e.getMessage());
        }

        try {
            clearApplicationService.clearApplication();
        } catch (DataAccessException e) {
            fail("Clear Exception: " + e.getMessage());
        }

        assertAll(
                () -> assertNull(userDAO.getUser("Luffy")),
                () -> assertNull(userDAO.getUser("Zoro")),
                () -> assertEquals(0, gameDAO.listGames().length),
                () -> assertNull(authDAO.getAuth("authToken"))
        );
    }
}
