package dataaccess;

//These are my imports
import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

//stores in memory
public class MemoryDataAccess implements DataAccess {
    //data structures
    private  final HashMap<String,  UserData> users =  new HashMap<>() ;
    private  final HashMap<Integer,  GameData> games =  new HashMap<>() ;
    private final HashMap<String, AuthData>  auths =  new HashMap<>();
    private int nextGameID = 1;

    //User stuff
    public void  makeChessUser(UserData user) throws DataException {
        // lock down the password before saving, just like the database version
        String safePassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        users.put( user.username() , new UserData(user.username(), safePassword, user.email()));
    }

    public UserData  getUser(String username) throws DataException {
        return users.get(username);
    }

    //Game stuff
    public int  createGame (GameData game) throws  DataException {
        int id = nextGameID++;
        GameData newGame = new  GameData(id,  null, null, game.gameName(), game.game());
        games.put(id,  newGame) ;
        return id ;
    }


    public GameData  getGame (int gameID) throws   DataException {
        return games.get(gameID);
    }



    public List<GameData> listGames() throws  DataException {
        return new ArrayList<>(games.values());
    }


    public void updateGame(GameData game)  throws  DataException {
        games.put(game.gameID(), game);
    }

    //Auth stuff
    public void  makeAuthorization(AuthData auth) throws DataException {
        auths.put(auth.authToken(), auth);
    }

    public AuthData  getAuthorization(String authToken) throws DataException {
        return auths.get(authToken) ;
    }

    public void deleteAuthorization (String authToken) throws DataException {
        auths.remove(authToken) ;


    }

    //wipes everything for testing
    public void clear()  throws  DataException {
        users.clear();
        games.clear() ;
        auths.clear();
    }
}