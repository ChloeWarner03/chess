package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;
import io.javalin.websocket.*;
import java.io.IOException;
import dataaccess.SqlAccess;
import dataaccess.DataException;
import org.eclipse.jetty.websocket.api.Session;


import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();


    private final DataAccess chessData;
    public WebSocketHandler() {
        try {
            chessData = new SqlAccess();
        } catch (DataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }


    @Override
    public void handleMessage(WsMessageContext wsMessageContext) throws Exception {
        int chessID = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = new Gson().fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            chessID = command.getGameID();
            connections.add(chessID, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command);
                case MAKE_MOVE -> makeMove(session, command, wsMessageContext.message());
                case LEAVE -> leaveGame(session, command);
                case RESIGN -> resign(session, command);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
//THis one needs leave, resign, connect, makemove, redraw chess board, help,highlight legal moves

    //Removes the user from the game (whether they are playing or observing the game).
    // The client transitions back to the Post-Login UI.
    private void leaveGame(Session session, UserGameCommand command) throws IOException,
            DataException{
        AuthData authorized = chessData.getAuthorization(command.getAuthToken());
        model.GameData chessGame = chessData.getGame(command.getGameID());
        String playerUser = authorized.username();

        // clear their spot so someone else can join
        if (playerUser.equals(chessGame.whiteUsername())) {
            chessGame = new model.GameData(chessGame.gameID(), null,
                    chessGame.blackUsername(), chessGame.gameName(), chessGame.game());
            chessData.updateGame(chessGame);
        } else if (playerUser.equals(chessGame.blackUsername())) {
            chessGame = new model.GameData(chessGame.gameID(), chessGame.whiteUsername(),
                    null, chessGame.gameName(), chessGame.game());
            chessData.updateGame(chessGame);
        }


        var message = String.format("%s left the game", playerUser);
        var notification = new ServerMessage.Notification(message);
        connections.broadcast(command.getGameID(), notification, session);
        connections.remove(command.getGameID(), session);

    }


    //	Prompts the user to confirm they want to resign. If they do, the user forfeits the game and the game is over.
    //	Does not cause the user to leave the game.
    private void resign(Session session, UserGameCommand command) throws IOException,
            DataException{
        AuthData  authorized = chessData.getAuthorization(command.getAuthToken());
        model.GameData chessGame = chessData.getGame(command.getGameID());
        String  playerUser  = authorized.username();

        // observer check
        if (isObserver(playerUser, chessGame)) {
            session.getRemote().sendString(new Gson().toJson(
                    new ServerMessage.Error("Error: observers cannot resign")));
            return;
        }

        // game already over check
        if (chessGame.game().getTeamTurn() == null) {
            session.getRemote().sendString(new Gson().toJson(
                    new ServerMessage.Error("Error: game is already over")));
            return;
        }

        chessGame.game().setTeamTurn(null);
        chessData.updateGame(chessGame);

        var message =  String.format("%s resigned the game", playerUser);
        var notification =  new ServerMessage.Notification(message);
        connections.broadcast (command.getGameID(),  notification, null);

        chessGame.game().setTeamTurn(null);
        chessData.updateGame(chessGame);
        System.out.println("after resign teamTurn: " + chessGame.game().getTeamTurn());


    }


    private boolean isObserver(String username, model.GameData game) {
        return !username.equals(game.whiteUsername()) && !username.equals(game.blackUsername());
    }

    //petshop thing?
    private void connect(Session session, UserGameCommand command) throws IOException, DataException {
        AuthData authorized = chessData.getAuthorization(command.getAuthToken());
        if (authorized == null) {
            session.getRemote().sendString(new Gson().toJson(
                    new ServerMessage.Error("Error: invalid auth token")));
            return;
        }

        model.GameData chessGame = chessData.getGame(command.getGameID());
        if (chessGame == null) {
            session.getRemote().sendString(new Gson().toJson(
                    new ServerMessage.Error("Error: game not found")));
            return;
        }

        String playerUser = authorized.username();
        var loadGame = new ServerMessage.LoadGame(chessGame.game());
        session.getRemote().sendString(new Gson().toJson(loadGame));

        String myRole;
        if (playerUser.equals(chessGame.whiteUsername())) {
            myRole = "white";
        } else if (playerUser.equals(chessGame.blackUsername())) {
            myRole = "black";
        } else {
            myRole = "an observer";
        }
        var notification = new ServerMessage.Notification(playerUser + " joined as " + myRole);
        connections.broadcast(command.getGameID(), notification, session);
    }


    //Allow the user to input what move they want to make.
    // The board is updated to reflect the result of the move,
    // and the board automatically updates on all clients involved in the game.
    private void makeMove(Session session, UserGameCommand command, String rawMessage)
            throws IOException, DataException {

        AuthData auth = chessData.getAuthorization(command.getAuthToken());
        if (auth == null) {
            session.getRemote().sendString(new Gson().toJson(
                    new ServerMessage.Error("Error: invalid auth token")));
            return;
        }

        model.GameData game = chessData.getGame(command.getGameID());
        if (game == null) {
            session.getRemote().sendString(new Gson().toJson(
                    new ServerMessage.Error("Error: game not found")));
            return;
        }

        if (isObserver(auth.username(), game)) {
            session.getRemote().sendString(new Gson().toJson(
                    new ServerMessage.Error("Error: observers cannot make moves")));
            return;
        }

        ChessGame.TeamColor myColor = auth.username().equals(game.whiteUsername()) ?
                ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

        if (game.game().getTeamTurn() == null || game.game().getTeamTurn() != myColor) {
            String msg = game.game().getTeamTurn() == null ? "Error: game is already over" : "Error: not your turn";
            session.getRemote().sendString(new Gson().toJson(new ServerMessage.Error(msg)));
            return;
        }

        UserGameCommand.MakeMove move =
                new Gson().fromJson(rawMessage, UserGameCommand.MakeMove.class);

        try {
            makeTheMove(game, move);
        } catch (chess.InvalidMoveException e) {
            session.getRemote().sendString(new Gson().toJson(
                    new ServerMessage.Error("Error: " + e.getMessage())));
            return;
        }

        updateBoard(command.getGameID(), game, auth.username(), move, session);
        checkGame(command.getGameID(), game);


        System.out.println("makeMove teamTurn from db: " + game.game().getTeamTurn());
    }

        //Helpers

    private void makeTheMove(model.GameData game,
                             UserGameCommand.MakeMove move)
            throws chess.InvalidMoveException {

        game.game().makeMove(move.move);
    }

    private void updateBoard(int gameID, model.GameData game,
                             String username, UserGameCommand.MakeMove move, Session session)
            throws DataException {

        chessData.updateGame(game);

        try {
            connections.broadcast(gameID,
                    new ServerMessage.LoadGame(game.game()), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            connections.broadcast(gameID,
                    new ServerMessage.Notification(
                            username + " made a move " + move.move),
                    session);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkGame(int gameID, model.GameData game)
            throws IOException, DataException {

        ChessGame.TeamColor otherplayer = game.game().getTeamTurn();

        if (game.game().isInCheckmate(otherplayer)) {
            game.game().setTeamTurn(null);
            chessData.updateGame(game);

            connections.broadcast(gameID,
                    new ServerMessage.Notification( "Player" +
                            otherplayer + " is in checkmate! Good Game!"),
                    null);
        }
        else if (game.game().isInStalemate(otherplayer)) {
            game.game().setTeamTurn(null);
            chessData.updateGame(game);

            connections.broadcast(gameID,
                    new ServerMessage.Notification(
                            "It is a Stalemate! Good Game!"),
                    null);
        }
        else if (game.game().isInCheck(otherplayer)) {
            connections.broadcast(gameID,
                    new ServerMessage.Notification( "Player" +
                            otherplayer + " is in check"),
                    null);
        }
    }




}