package dataAccess;
import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    //you know the drill
    private HashMap<String, UserData> realboyMap = new HashMap<>();

    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        realboyMap.put(user.username(), user);
        return user;
    }
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return realboyMap.get(username);
    }
    @Override
    public void clear() throws DataAccessException {
        realboyMap.clear();
    }
}
