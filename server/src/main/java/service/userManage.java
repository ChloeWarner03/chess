package service;

//These are my imports
import dataaccess.DataAccess;
import dataaccess.DataException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.util.UUID;

//User related stuff
public class userManage {
    private final DataAccess dataAccess;

    //Get my data access
    public userManage(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }


    //register a new user and give auth token
    public AuthData register(UserData newUser) throws DataException {
        haveAllInfo(newUser);
        checkUsername(newUser.username());
        dataAccess.makeChessUser(newUser);
        return storeToken(newUser.username());
    }



    //log existing user, give new token
    public AuthData login(UserData user) throws DataException {
        loginHaveInfo(user);
        checkPassword(user);
        return storeToken(user.username());
    }

    //logout, delete token
    public void logout(String authToken) throws DataException {
        validToken(authToken);
        dataAccess.deleteAuthorization(authToken);
    }


    //helperS!!!
    //HAVE ALL INFO
    private void haveAllInfo(UserData newUser) {
        if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
            throw new BadRequest("missing fields that are required");
        }
    }
    //check user exists and password matches
    private void checkPassword(UserData user) throws DataException {
        UserData existingUser = dataAccess.getUser(user.username());
        if (existingUser == null || !BCrypt.checkpw(user.password(), existingUser.password())) {
            throw new UnauthorizedException("wrong password");
        }
    }
    //check is token valid
    private void validToken(String authToken) throws DataException {
        if (dataAccess.getAuthorization(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        }
    }


    //username taken
    private void checkUsername(String username) throws DataException {
        if (dataAccess.getUser(username) != null) {
            throw new BeenTakenException("username is already taken");
        }
    }
    //need username and password to login
    private void loginHaveInfo(UserData user) {
        if (user.username() == null || user.password() == null) {
            throw new BadRequest("missing fields that are required");
        }
    }




    //store token
    private AuthData storeToken(String username) throws DataException {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        dataAccess.makeAuthorization(newAuth);
        return newAuth;
    }

}