package server.websocket;

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
import dataaccess.DataAccessException;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();


    private final DataAccess chessData;
    public WebSocketHandler() {
        try {
            chessData = new SqlAccess();
        } catch (DataAccessException e) {
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
            DataAccessException{
        AuthData authorized = chessData.getAuthorization(command.getAuthToken());
        String playerUser = authorized.username();
        var message = String.format("%s left the game", playerUser);
        var notification = new ServerMessage.Notification(message);
        connections.broadcast(command.getGameID(), notification, session);
        connections.remove(command.getGameID(), session);

    }


    //	Prompts the user to confirm they want to resign. If they do, the user forfeits the game and the game is over.
    //	Does not cause the user to leave the game.
    private void resign(Session session, UserGameCommand command) throws IOException,
            DataAccessException{
        AuthData  authorized = chessData.getAuthorization(command.getAuthToken());
        model.GameData chessGame = chessData.getGame(command.getGameID());
        String  playerUser  = authorized.username();

        chessGame.game().setTeamTurn(null);
        chessData.updateGame(chessGame);

        var message =  String.format("%s resigned the game", playerUser);
        var notification =  new ServerMessage.Notification(message);
        connections.broadcast (command.getGameID(),  notification, null);


    }

    //petshop thing?
    private void connect(Session session, UserGameCommand command) throws IOException, DataAccessException{
        AuthData  authorized = chessData.getAuthorization(command.getAuthToken());
        model.GameData chessGame = chessData.getGame(command.getGameID());
        String  playerUser  = authorized.username();



    }

    //Allow the user to input what move they want to make.
    // The board is updated to reflect the result of the move,
    // and the board automatically updates on all clients involved in the game.
    private void makeMove( Session session,  UserGameCommand command, String rawMessage)throws IOException,
            DataAccessException {
        AuthData  authorized = chessData.getAuthorization(command.getAuthToken());
        model.GameData chessGame = chessData.getGame(command.getGameID());
        String  playerUser  = authorized.username();

    }
}