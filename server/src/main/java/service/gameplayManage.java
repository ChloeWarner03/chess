package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataException;
import model.AuthData;
import model.GameData;
import java.util.List;

//There is a recommendation for this one to have a constant instead of duplicating
//WHite but sine Chess Game has a color enum do I want to keep it constant?


//Game related stuff
public class gameplayManage {

    private static final String WHITE = "WHITE";
    private static final String BLACK = "BLACK";

    private final DataAccess dataAccess;

    //get my data access
    public gameplayManage(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //wipe everything for testing
    public void clear() throws DataException {
        dataAccess.clear();
    }

    //create game and return game ID
    public int createGame(String authToken, String gameName)
            throws DataException, UnauthorizedException, BadRequest {
        validToken(authToken);
        gameName(gameName);
        GameData game = new GameData(0, null, null, gameName, new ChessGame());
        return dataAccess.createGame(game);
    }

    //list games
    public List<GameData> listGames(String authToken)
            throws DataException, UnauthorizedException {
        validToken(authToken);
        return dataAccess.listGames();
    }

    //join game
    public void joinGame(String authToken, String playerColor, int gameID)
            throws DataException, UnauthorizedException,
            BadRequest, BeenTakenException {
        validToken(authToken);
        color(playerColor);
        GameData game = getGame(gameID);
        colorisntTaken(playerColor, game);
        AuthData auth = dataAccess.getAuthorization(authToken);
        newGamePlayer(auth.username(), playerColor, game);
    }

    //Helpers!
    //check is token valid
    private void validToken(String authToken) throws DataException,
            UnauthorizedException {
        if (dataAccess.getAuthorization(authToken) == null) {
            throw new UnauthorizedException("Not logged in");
        }
    }

    //need a game name
    private void gameName(String gameName) {
        if (gameName == null) {
            throw new BadRequest("You need a Game Name");
        }
    }

    //game? or throw
    private GameData getGame(int gameID) throws DataException,
            BadRequest {
        GameData game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new BadRequest("Couldn't Find Game");
        }
        return game;
    }

    //game with new player
    private void newGamePlayer(String username, String playerColor, GameData game)
            throws DataException {
        GameData updatedGame;
        if (playerColor.equals(WHITE)) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        dataAccess.updateGame(updatedGame);
    }
    //color already taken
    private void colorisntTaken(String playerColor, GameData game) throws BeenTakenException {
        if (playerColor.equals(WHITE) && game.whiteUsername() != null) {
            throw new BeenTakenException("Color is already taken");
        }
        if (playerColor.equals(BLACK) && game.blackUsername() != null) {
            throw new BeenTakenException("Color is already taken");
        }
    }

    //black or white color
    private void color(String playerColor) {
        if (playerColor == null || (!playerColor.equals(WHITE)
                && !playerColor.equals(BLACK))) {
            throw new BadRequest("Can't Use This Color");
        }
    }


}