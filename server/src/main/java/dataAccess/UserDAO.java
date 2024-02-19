package dataAccess;

import model.UserData;

public interface UserDAO {
    //so the specs only say I need a create and get for this one, but pretty sure imma need the whole CRUD method so 2 people cant have the same name and I can delete it
    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void updateUser(UserData user) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;
}
