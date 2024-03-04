package dataAccess;
import model.AuthData;
import java.util.HashMap;


public class MemoryAuthDAO implements AuthDAO{
    //need a way to store everything
    //options are an Array, Hashmap, or a linked list I think
    //gonna go with my strongsuit of the Hashmap
    private HashMap<String, AuthData> identiMap = new HashMap<>();
    // now just back to the CRUD i think
    // same classes i did in the DAO classes
    @Override
    public AuthData createAuth(AuthData auth) throws DataAccessException {
        if (identiMap.containsKey(auth.authToken())) {
            throw new DataAccessException("Username already exists");
        } else {
            identiMap.put(auth.authToken(), auth);
            return auth;
        }
    }
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return identiMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        identiMap.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        identiMap.clear();
    }
}
