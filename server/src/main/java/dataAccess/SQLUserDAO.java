package dataAccess;
import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SQLUserDAO implements UserDAO {
    //Then when a user attempts to login, repeat the hashing process on the user
    // supplied login password and then compare the resulting hash to the previously stored hash of the original password.
    // If the two hashes match then you know the supplied password is correct.
    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String query : CREATE_USERS_TABLE_QUERY) {
                try (PreparedStatement magicConch = conn.prepareStatement(query)) {
                    magicConch.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error configuring the database: " + e.getMessage());
        }
    }
    private static final String[] CREATE_USERS_TABLE_QUERY = {
            "CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(50) NOT NULL, " +
                    "PRIMARY KEY (username)" +
                    ")"
    };
    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement magicConch = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
            BCryptPasswordEncoder secretPassword = new BCryptPasswordEncoder();
            String hashedPassword = secretPassword.encode(user.password());
            magicConch.setString(1, user.username());
            magicConch.setString(2, hashedPassword);
            magicConch.setString(3, user.email());
            magicConch.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Error spawning the user: " + e.getMessage());
        }
    }
    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement magicConch = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            magicConch.setString(1, username);
            try(ResultSet resultSet = magicConch.executeQuery()) {
                if (resultSet.next()) {
                    String yoinkedUsername = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    return new UserData(yoinkedUsername, password, email);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error nabbin the user: " + e.getMessage());
        }
    }
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement magicConch = conn.prepareStatement("TRUNCATE TABLE users")) {
            magicConch.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error nuking user data: " + e.getMessage());
        }
    }
}
