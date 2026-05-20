package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import java.util.List;

//Game related stuff
public class GameService {

    private final DataAccess dataAccess;

    //get my data access
    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //wipe everything for testing
    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    //create game and return game ID
    public int createGame(String authToken, String gameName)
            throws DataAccessException, UnauthorizedException, BadRequestException {
        validToken(authToken);
        gameName(gameName);
        GameData game = new GameData(0, null, null, gameName, new ChessGame());
        return dataAccess.createGame(game);
    }

    //list games
    public List<GameData> listGames(String authToken)
            throws DataAccessException, UnauthorizedException {
        validToken(authToken);
        return dataAccess.listGames();
    }

    //join game
    public void joinGame(String authToken, String playerColor, int gameID)
            throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        validToken(authToken);
        color(playerColor);
        GameData game = getGame(gameID);
        colorisntTaken(playerColor, game);
        AuthData auth = dataAccess.getAuth(authToken);
        newPlayer(auth.username(), playerColor, game);
    }

    //Helpers!
    //check is token valid
    private void validToken(String authToken) throws DataAccessException, UnauthorizedException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Not logged in");
        }
    }

    //need a game name
    private void gameName(String gameName) {
        if (gameName == null) {
            throw new BadRequestException("Game name is required");
        }
    }

    //black or white color
    private void color(String playerColor) {
        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new BadRequestException("Invalid player color");
        }
    }

    //game? or throw
    private GameData getGame(int gameID) throws DataAccessException, BadRequestException {
        GameData game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new BadRequestException("Game not found");
        }
        return game;
    }
    //color already taken
    private void colorisntTaken(String playerColor, GameData game) throws AlreadyTakenException {
        if (playerColor.equals("WHITE") && game.whiteUsername() != null) {
            throw new AlreadyTakenException("Color already taken");
        }
        if (playerColor.equals("BLACK") && game.blackUsername() != null) {
            throw new AlreadyTakenException("Color already taken");
        }
    }
    //game with new player
    private void newPlayer(String username, String playerColor, GameData game)
            throws DataAccessException {
        GameData updatedGame;
        if (playerColor.equals("WHITE")) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        dataAccess.updateGame(updatedGame);
    }

}