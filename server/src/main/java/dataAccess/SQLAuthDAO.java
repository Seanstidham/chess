package dataAccess;
import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    //So I think that its similar to the MemoryAuthDAO classes, its an implement class thats throws CRUD
    //now the tricky part is creating the table, gonna learn a bit more then see what I can do
    /*
    okay so to do it I need these things
    1 gotta configure the database
    2 create the auth data and put it into the table
    3 get the way to retrieve it from the table
    4 delete the data from the table
     */
    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public AuthData createAuth(AuthData auth) throws DataAccessException {
//        Make sure that you wrap your calls to get a connection with a try-with-resources block so that the connection gets cleaned up.
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement magicConch = conn.prepareStatement("INSERT INTO auth (auth_token, username) VALUES (?, ?)")) {
            magicConch.setString(1, auth.authToken());
            magicConch.setString(2, auth.authToken());
            magicConch.executeUpdate();
            return auth;
        } catch (SQLException e) {
            throw new DataAccessException("Error with auth data: " + e.getMessage());
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement magicConch = conn.prepareStatement("SELECT * FROM auth WHERE auth_token = ?")) {
            magicConch.setString(1,authToken);
            try (ResultSet resultSet = magicConch.executeQuery()) {
                if (resultSet.next()) {
                    String mainCharacter = resultSet.getString("username");
                    return new AuthData(authToken, mainCharacter);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error nabbin auth data: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement magicConch = conn.prepareStatement("DELETE FROM auth WHERE auth_token = ?")) {
            magicConch.setString(1, authToken);
            magicConch.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error nuking auth data: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement magicConch = conn.prepareStatement("TRUNCATE TABLE auth")) {
            magicConch.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error wiping auth data: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String query : CREATE_AUTH_TABLE_QUERY) {
                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error configuring database: " + e.getMessage());
        }
    }
    private static final String[] CREATE_AUTH_TABLE_QUERY = {
            "CREATE TABLE IF NOT EXISTS auth (" +
                    "auth_token VARCHAR(50) NOT NULL, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "PRIMARY KEY (auth_token)" +
                    ")"
    };

}
