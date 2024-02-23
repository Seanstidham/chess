package dataAccess;

import model.AuthData;

public interface AuthDAO {
    //So looking at the github specs i need to throw exceptions and build a crud framework

    //create
    AuthData createAuth(AuthData auth) throws DataAccessException;
    //read
    AuthData getAuth(String authToken) throws DataAccessException;
    //no update with this one so on to delete
    void deleteAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;

}
