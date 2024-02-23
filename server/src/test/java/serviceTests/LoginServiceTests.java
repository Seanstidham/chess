package serviceTests;
import dataAccess.*;
import service.LoginService;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private LoginService loginService;

    @BeforeEach
    public void setmeupMordecai() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO);

        UserData Luffy = new UserData("kingofthepirates", "meat", "luffy@example.com");
        try {
            userDAO.createUser(Luffy);
        } catch (DataAccessException e) {
            fail("You broke it " + e.getMessage());
        }
    }
    @Test
    public void negativeLoginServiceTest() {
        LoginRequest badEndingrequest = new LoginRequest("helenkellarwasntreal", "secretword");

        LoginResult badEndingresult = null;

        try {
            badEndingresult = loginService.login(badEndingrequest);
        } catch (DataAccessException e) {
            fail("Login Exception " + e.getMessage());
        }

        assertNotNull(badEndingresult);
        assertNull(badEndingresult.authToken());
        assertNull(badEndingresult.username());
        assertEquals("Error: unauthorized ", badEndingresult.errorMessage());
    }
    @Test
    public void positiveLoginServiceTest ()  {
        LoginRequest goodEndingrequest = new LoginRequest("Nami", "catburglar");

        LoginResult goodEndingresult = null;
        try {
            goodEndingresult = loginService.login(goodEndingrequest);
        } catch (DataAccessException e) {
            fail("Exception thrown: " + e.getMessage());
        }

        assertNotNull(goodEndingresult);
        assertNull(goodEndingresult.errorMessage());
        assertNotNull(goodEndingresult.authToken());
        assertEquals("Nami", goodEndingresult.username());
    }


}
