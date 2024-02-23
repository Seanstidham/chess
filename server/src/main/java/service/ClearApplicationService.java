package service;
import result.ClearApplicationResult;
import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;

public class ClearApplicationService {
    /* imports/files imma need
    1 imma need the 3 DAOs
    2 github specs look like it only needs a result, which makes sense
    3 and then imma need the data access exception
     */
    //imma make the result real quick then come back and import it all

    //okay so i can't set it up like I did in Register because Clear needs to catch the exception and do what its gotta do
    //I can set up the beginning like Register, but mr google is telling me I need a try catch block to handle what I need
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    public ClearApplicationService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    //okay think i have enough practice after the test file i wrote, let me cook
    public void clearApplication() throws DataAccessException {
        try {
            //Clears the database. Removes all users, games, and authTokens.
            authDAO.clear();
            gameDAO.clear();
            userDAO.clear();
            new ClearApplicationResult(null);
        } catch (DataAccessException e) {
            new ClearApplicationResult("Error: " + e.getMessage());
        }
    }
    /*
    OKay now I need to make/implement these
    1 needs a handler class
    2 needs to be implemented into the server page
    3 need a test page (icky)
    aight imma flesh it all out
     */

}
