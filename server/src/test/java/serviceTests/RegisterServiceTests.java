package serviceTests;
import dataAccess.*;
import service.RegisterService;
import request.RegisterRequest;
import result.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private RegisterService registerService;

    @BeforeEach
    public void setmeupMordecai() {
        // Set it up so it can store the memory
        //wait im gonna have to set those calsses up first arent I?
        //aight give me a bit to go do that, then ill be back

//        Good tests extensively show that we get the expected behavior.
//        This could be asserting that data put into the database is really there, or that a function throws an error when it should.
//        Write a positive and a negative JUNIT test case for each public method on your Service classes, except for Clear which only needs a positive test case.
//        A positive test case is one for which the action happens successfully (e.g., successfully claiming a spot in a game).
//        A negative test case is one for which the operation fails (e.g., trying to claim an already claimed spot).
//        The service unit tests must directly call the methods on your service classes.
//        They should not use the HTTP server pass off test code that is provided with the starter code.
//

        this.authDAO = new MemoryAuthDAO();
        this.userDAO = new MemoryUserDAO();
        this.registerService = new RegisterService(userDAO, authDAO);

        // so i need an init for the inital test
        RegisterRequest alreadyaliveRequest = new RegisterRequest("alreadyUser", "passwordalreadyathing", "alreadyused@example.com");
        try {
            registerService.register(alreadyaliveRequest);
        } catch (DataAccessException e) {
            fail("Houston, we got a registration issue: " + e.getMessage());
        }
    }
    //okay so lkets set up the negative test first
    //first time ever writing my own tests let me see if I can cook
    @Test
    public void negativeRegisterServiceTest() {
        RegisterRequest badEndingrequest = new RegisterRequest("alreadyUser", "randomPassword", "alreadyused@example.com");
        RegisterResult badEndingresult = null;
        try {
            badEndingresult = registerService.register(badEndingrequest);
        } catch (DataAccessException e) {
            fail("Houston, we got a registration issue: " + e.getMessage());
        }

        //looking at the other test files I need like assert functions to test, let me see if I can get everything github says
        //so eveything i need to assert is the authToken, username, and message and see if everything calls properly
        //let me cook
        assertNull(badEndingresult.authToken());
        assertNull(badEndingresult.username());
        assertNotNull(badEndingresult.message());
        assertNotNull(badEndingresult);
        assertEquals("Error: already taken", badEndingresult.message());
    }
    //okay nowthe positive one, think its pretty similar to the bad ending

    @Test
    public void positiveRegisterServiceTest() {
        RegisterRequest goodEndingrequest = new RegisterRequest("randomUser", "randomPassword", "theonepieceisreal@example.com");
        RegisterResult goodEndingresult = null;

        try {
            goodEndingresult = registerService.register(goodEndingrequest);
        } catch (DataAccessException e) {
            fail("Houston, we got a registration issue: " + e.getMessage());
        }


        assertNull(goodEndingresult.message());
        assertNotNull(goodEndingresult.authToken());
        assertNotNull(goodEndingresult);
        assertEquals("randomUser", goodEndingresult.username());
    }
    //okay think thats good

}
