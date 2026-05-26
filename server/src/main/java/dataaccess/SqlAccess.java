package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

//imports that I added
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Statement;
import java.sql.Types;
import com.google.gson.Gson;
 //encrypt

import java.util.List;


public class SqlAccess implements DataAccess {

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS user (
        username VARCHAR(256) NOT NULL,
        password VARCHAR(256) NOT NULL,
        email VARCHAR(256) NOT NULL,
        PRIMARY KEY (username)
    )""",
            """
    CREATE TABLE IF NOT EXISTS auth (
        authToken VARCHAR(256) NOT NULL,
        username VARCHAR(256) NOT NULL,
        PRIMARY KEY (authToken)
    )""",
            """
    CREATE TABLE IF NOT EXISTS game (
        gameID INT NOT NULL AUTO_INCREMENT,
        blackUsername VARCHAR(256),
        whiteUsername VARCHAR(256),
        gameName VARCHAR(256) NOT NULL,
        game LONGTEXT NOT NULL,
        PRIMARY KEY (gameID)
    )"""
    };

    public SqlAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database: " + e.getMessage());
        }
    }

    //helper functions toS use for
    private UserData readUser(java.sql.ResultSet queryResults) throws SQLException {
        return new UserData(
                queryResults.getString("username"),
                queryResults.getString("password"),
                queryResults.getString("email")
        );
    }

    private int runUpdate(String sql, Object... params) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var query = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // fill in the placeholders we set for ???
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param == null) {
                        query.setNull(i + 1, Types.NULL);
                    } else if (param instanceof String) {
                        query.setString(i + 1, (String) param);
                    } else if (param instanceof Integer) {
                        query.setInt(i + 1, (Integer) param);
                    }
                }

                // run the sql
                query.executeUpdate();

                // return the generated key if there is one
                var queryResults= query.getGeneratedKeys();
                if (queryResults.next()) {
                    return queryResults.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to run update: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        //Need to hash the passwrod with BCrypt before storing
        var sql = "INSERT INTO user (username, password, email) values (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        runUpdate(sql, user.username(), hashedPassword, user.email());
    }


    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            var sql = "SELECT username, password, email FROM user WHERE username=?";
            try (var query = connection.prepareStatement(sql)) {
                query.setString(1, username);
                try (var queryResults = query.executeQuery()) {
                    if (queryResults.next()) {
                        return readUser(queryResults);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}