package serviceTests;
import dataAccess.*;
import service.LogoutService;
import model.AuthData;
import result.LogoutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class LogoutServiceTests {
    private AuthDAO authDAO;
    private LogoutService logoutService;

    @BeforeEach
    public void setmeupMordecai() {
        authDAO = new MemoryAuthDAO();
        logoutService = new LogoutService(authDAO);

        try {
            AuthData authToken = new AuthData("testPiece", "Sanji");
            authDAO.createAuth(authToken);
        } catch (DataAccessException e) {
            fail("Exception throwm " + e.getMessage());
        }
    }
    @Test
    public void testNegativeLogout() throws DataAccessException {
        String authToken = "invalidPiece";

        LogoutResult result = logoutService.logout(authToken);

        assertNotNull(result);
        assertNotNull(result.message());
        assertEquals("Error: Unauthorized", result.message());
    }
    @Test
    public void testPositiveLogout() {
        String authToken = "testPiece";

        try {
            LogoutResult result = logoutService.logout(authToken);

            assertNotNull(result);
            assertNull(result.message());
            assertNull(authDAO.getAuth("testPiece"));
        } catch (DataAccessException e) {
            fail("Logout Exception " + e.getMessage());
        }
    }

}
