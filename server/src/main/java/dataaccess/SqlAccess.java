package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

//imports that I added
import java.sql.SQLException;

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
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
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