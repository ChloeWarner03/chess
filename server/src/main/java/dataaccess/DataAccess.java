package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess {


    // User methods
    //THis one will need the username, the apssowrd and the email

    void  makeChessUser(UserData user) throws DataAccessException;
    UserData  getUser(String username)  throws DataAccessException ;

    // Game methods
    //this will need the authtoken adn the username


    int createGame(GameData game)  throws DataAccessException;
    GameData getGame(int gameID)  throws DataAccessException ;
    List<GameData> listGames()  throws DataAccessException ;
    void updateGame(GameData game)  throws DataAccessException ;

    // Auth methods
    //this will need the gameID, white username, black username and the gamename and the game json


    void makeAuthorization(AuthData auth) throws  DataAccessException;
    AuthData  getAuthorization(String authToken) throws  DataAccessException;
    void  deleteAuthorization(String authToken)  throws DataAccessException;

    // Clear



    void clear()  throws DataAccessException ;
}
