package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import java.util.List;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //This wipes everything out for testing
    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    //This creates a new game and returns the game ID
    public int createGame(String authToken, String gameName)
            throws DataAccessException, UnauthorizedException, BadRequestException {
        //Check if the auth token is valid
        if (dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Not logged in");
        }
        //Make sure game name is not null
        if (gameName == null) {
            throw new BadRequestException("Game name is required");
        }
        //Create the game and return its ID
        GameData game = new GameData(0, null, null, gameName, new ChessGame());
        return dataAccess.createGame(game);
    }

    //This lists all the games
    public List<GameData> listGames(String authToken)
            throws DataAccessException, UnauthorizedException {
        //Check if the auth token is valid
        if (dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Not logged in");
        }
        return dataAccess.listGames();
    }

    //This lets a player join a game
    public void joinGame(String authToken, String playerColor, int gameID)
            throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        //Check if the auth token is valid
        //Make sure playerColor and gameID are valid
        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new BadRequestException("Invalid player color");
        }
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Not logged in");
        }
        //Check if the game exists
        GameData game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new BadRequestException("Game not found");
        }
        //Check if the color is taken
        if (playerColor.equals("WHITE") && game.whiteUsername() != null) {
            throw new AlreadyTakenException("Color already taken");
        }
        if (playerColor.equals("BLACK") && game.blackUsername() != null) {
            throw new AlreadyTakenException("Color already taken");
        }
        //Update the game with the new player
        GameData updatedGame;
        if (playerColor.equals("WHITE")) {
            updatedGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
        }
        dataAccess.updateGame(updatedGame);
    }
}