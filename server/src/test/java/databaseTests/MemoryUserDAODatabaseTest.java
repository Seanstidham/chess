package databaseTests;
import model.UserData;
import dataAccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class MemoryUserDAODatabaseTest {
    MemoryUserDAO userDAO;
    @BeforeEach
    public void setmeupMordecai() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        userDAO.clear();
    }
    @AfterEach
    public void wreckIt() {
        try {
            userDAO.clear();
        } catch (DataAccessException e) {
            fail("Exception with nuking: " +e.getMessage());
        }
    }
    @Test
    public void negativeCreateUserTest() throws DataAccessException {
        UserData badendingUser = new UserData("testUser1", "password1", "email@example.com");
        userDAO.createUser(badendingUser);

        UserData badendingUser1 = new UserData("testUser1", "password2", "email2@example.com");
        assertThrows(DataAccessException.class, () -> userDAO.createUser(badendingUser1));
    }

    @Test
    public void positiveCreateUserTest() throws DataAccessException {
        UserData goodendingUser = new UserData("testUser1", "password1", "email@example.com");
        userDAO.createUser(goodendingUser);

        UserData yoinkedUser = userDAO.getUser("testUser1");

        assertEquals(goodendingUser.username(), yoinkedUser.username());
        assertEquals(goodendingUser.email(), yoinkedUser.email());
        assertNotNull(yoinkedUser.password());
        assertNotNull(yoinkedUser);
    }
    @Test
    public void negativeGetUserTest() throws DataAccessException {
        String schizoUser = "they_are_listening";
        assertNull(userDAO.getUser(schizoUser)); //ever just think youre developing schizophrenia?
    }
    @Test
    public void positiveGetUserTest() throws DataAccessException {
        UserData goodendingUser = new UserData("testUser1", "password1", "email@example.com");
        userDAO.createUser(goodendingUser);

        UserData yoinkedUser = userDAO.getUser("testUser1");

        assertEquals(goodendingUser.username(), yoinkedUser.username());
        assertEquals(goodendingUser.email(), yoinkedUser.email());
        assertNotNull(yoinkedUser.password());
        assertNotNull(yoinkedUser);
    }

}
