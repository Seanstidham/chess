package service;

import dataAccess.DataAccessException;
import dataAccess.SQLUserDAO;
import request.LoginRequest;
import result.LoginResult;
import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import model.AuthData;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class LoginService {

    /*
    Okay so in order to implement login i need these things (Not in order, ill work that out later)
    1) gotta check the database to see if the username exists
    2) if its real i gotta assign it a way to be different from everyone elses
    3) then I gotta store that so that it'll always be in the update of the database
    4) then I gotta return everything
     */
    //so regarding imports imma need the exception, the request and the result of the login, the user and auth data objects, and the authdtat model
    //imma start by building the login request and result file, then i'll build this out more

    //okay plotting done, lets cook
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    //okay basic inits over, now to cook out my 4 steps
    //according to mr internet the UUID package is a good way to do my step 2
    public LoginResult login(LoginRequest request) throws DataAccessException {
        //init a user
        var user = userDAO.getUser(request.username());
        if(userDAO instanceof SQLUserDAO) {
            BCryptPasswordEncoder secretPassword = new BCryptPasswordEncoder();
            if(user == null || !secretPassword.matches(request.password(), user.password())) {
                return new LoginResult(null, null, "Error: Invalid username or password");
            }
        } else {

            if (user == null || !user.password().equals(request.password())) {
                return new LoginResult(null, null, "Error: Invalid username or password"); //found it
            }
        }
        String authToken = UUID.randomUUID().toString();
        AuthData newBoy = new AuthData(authToken, user.username());

        authDAO.createAuth(newBoy);

        return new LoginResult(user.username(), authToken, null);
    }

    //im getting a AssertionFailedError: Response missing error message ==>
    //going to see if a try catch block fixes it
    //oh nvm its looking for a specific message
}
