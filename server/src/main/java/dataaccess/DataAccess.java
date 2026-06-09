package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess {


    // User methods
    //THis one will need the username, the apssowrd and the email

    void  makeChessUser(UserData user) throws DataException;
    UserData  getUser(String username)  throws DataException ;

    // Game methods
    //this will need the authtoken adn the username


    int createGame(GameData game)  throws DataException;
    GameData getGame(int gameID)  throws DataException ;
    List<GameData> listGames()  throws DataException ;
    void updateGame(GameData game)  throws DataException ;

    // Auth methods
    //this will need the gameID, white username, black username and the gamename and the game json


    void makeAuthorization(AuthData auth) throws  DataException;
    AuthData  getAuthorization(String authToken) throws  DataException;
    void  deleteAuthorization(String authToken)  throws DataException;

    // Clear



    void clear()  throws DataException ;
}
