package service;

//These are my imports
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

//User related stuff
public class UserService {
    private final DataAccess dataAccess;

    //Get my data access
    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //register a new user and give auth token
    public AuthData register(UserData newUser) throws DataAccessException {
        HaveAllInfo(newUser);
        checkUsername(newUser.username());
        dataAccess.createUser(newUser);
        return storeToken(newUser.username());
    }
    //log existing user, give new token
    public AuthData login(UserData user) throws DataAccessException {
        loginHaveInfo(user);
        checkPassword(user);
        return storeToken(user.username());
    }

    //logout, delete token
    public void logout(String authToken) throws DataAccessException {
        validToken(authToken);
        dataAccess.deleteAuth(authToken);
    }

    //helperS!!!
    //HAVE ALL INFO
    private void HaveAllInfo(UserData newUser) {
        if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
            throw new BadRequestException("missing required fields");
        }
    }
    //check user exists and password matches
    private void checkPassword(UserData user) throws DataAccessException {
        UserData existingUser = dataAccess.getUser(user.username());
        if (existingUser == null || !existingUser.password().equals(user.password())) {
            throw new UnauthorizedException("wrong password");
        }
    }
    //need username and password to login
    private void loginHaveInfo(UserData user) {
        if (user.username() == null || user.password() == null) {
            throw new BadRequestException("missing required fields");
        }
    }

    //username taken
    private void checkUsername(String username) throws DataAccessException {
        if (dataAccess.getUser(username) != null) {
            throw new AlreadyTakenException("username taken");
        }
    }


    //check is token valid
    private void validToken(String authToken) throws DataAccessException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        }
    }


    //store token
    private AuthData storeToken(String username) throws DataAccessException {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        dataAccess.createAuth(newAuth);
        return newAuth;
    }

}