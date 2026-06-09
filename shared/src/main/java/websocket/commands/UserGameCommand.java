package websocket.commands;

import chess.ChessMove;
import websocket.messages.ServerMessage;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {
    //This is the stuff from the starter code
    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public enum CommandType {
        CONNECT,
        /** Used for a user to make a WebSocket
         * connection as a player or observer.
         */
        MAKE_MOVE,
        //Used to request to make a move in a game.
        LEAVE,
        //Tells the server you are leaving the game
        // //so it will stop sending you notifications.
        RESIGN
        //	Forfeits the match and ends the game
        //	(no more moves can be made).
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }
    //this was needed to be static
    public static class Make_Move extends UserGameCommand {
        public ChessMove move;

        public Make_Move(String authToken, Integer gameID, ChessMove move) {
            super(CommandType.MAKE_MOVE, authToken, gameID);
            this.move = move;
        }
    }

    //Override stuff from the starter code

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
