package dataAccessTests;
import model.AuthData;
import dataAccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class SQLAuthDAODatabaseTest {
    AuthDAO authDAO;
    @BeforeEach
    public void setmeupMordecai() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }
    @AfterEach
    public void wreckIt() {
        try {
            authDAO.clear();
        } catch (DataAccessException e) {
            fail("Exception with nuking: " + e.getMessage());
        }
    }
    //wait a second? Its all the same format and function, I might just be able to copy all the tests over
    @Test
    public void negativeCreateAuthTest() throws DataAccessException{
        AuthData badendingauthData = new AuthData("test_token", "BigMom");

        authDAO.createAuth(badendingauthData);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(badendingauthData);
        });
    }
    @Test
    public void positiveCreateAuthTest() throws DataAccessException {
        AuthData goodendingauthData = new AuthData("test_token", "test_user");

        authDAO.createAuth(goodendingauthData);

        AuthData yoinkedAuth = authDAO.getAuth("test_token");

        assertNotNull(yoinkedAuth);
        assertEquals(goodendingauthData.authToken(), yoinkedAuth.authToken());
        assertEquals(goodendingauthData.username(), yoinkedAuth.username());
    }
    @Test
    public void negativeGetAuthTest() throws DataAccessException {
        String schizoToken = "nonReal";

        assertNull(authDAO.getAuth(schizoToken));
    }
    @Test
    public void positiveGetAuthTest() throws DataAccessException {
        AuthData goodendingauthData = new AuthData("testToken", "testUser");
        authDAO.createAuth(goodendingauthData);

        AuthData yoinkedAuthData = authDAO.getAuth("testToken");

        assertEquals(goodendingauthData.authToken(), yoinkedAuthData.authToken());
        assertEquals(goodendingauthData.username(), yoinkedAuthData.username());
    }
    @Test
    public void negativeDeleteAuthTest() {
        String schizoToken = "please_take_your_medicine";

        assertDoesNotThrow(() -> authDAO.deleteAuth(schizoToken));
    }
    @Test
    public void positiveDeleteAuthTest() throws DataAccessException {
        AuthData goodendingauthData = new AuthData("test_token", "test_user");
        authDAO.createAuth(goodendingauthData);

        authDAO.deleteAuth("test_token");

        assertNull(authDAO.getAuth("test_token"));
    }
    @Test
    public void positiveClearTest() throws DataAccessException {
        AuthData goodendingData1 = new AuthData("token1", "Luffy");
        AuthData goodendingData2 = new AuthData("token2", "Shanks");

        authDAO.createAuth(goodendingData1);
        authDAO.createAuth(goodendingData2);

        assertNotNull(authDAO.getAuth("token1"));
        assertNotNull(authDAO.getAuth("token2"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }
    //well that made writing tests far easier
}
