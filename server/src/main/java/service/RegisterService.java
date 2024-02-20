package service;

import dataAccess.DataAccessException;
import request.RegisterRequest;
import result.RegisterResult;
import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;


import java.util.UUID;

public class RegisterService {
    //build it similar to login and all the imports and stuff
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            return new RegisterResult(null, null, "Error: bad request");
        }
        UserData alreadyUser = userDAO.getUser(request.username());
        if (alreadyUser != null) {
            return new RegisterResult(null, null, "Error: already taken");
        }
        UserData newBoy = new UserData(request.username(), request.password(), request.email());
        String authToken = UUID.randomUUID().toString();
        AuthData newGuy = new AuthData(authToken, newBoy.username());

        userDAO.createUser(newBoy);
        authDAO.createAuth(newGuy);

        return new RegisterResult(newBoy.username(), authToken, null);
    }

}
