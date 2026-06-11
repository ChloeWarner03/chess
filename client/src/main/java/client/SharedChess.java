package client;

import chess.ChessGame;
import client.websocket.WebSocketFacade;
import model.GameData;

public class SharedChess {

    public String username;
    public String authToken;
    public State state = State.LOGGED_OUT;

    public GameData[] savedGames;
    public WebSocketFacade gameWebSocket;
    public ChessGame currentGame;
    public int openGameNumber;
    public ChessGame.TeamColor yourColor;
}
