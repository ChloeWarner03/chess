package websocket.messages;

import java.util.Objects;
import chess.ChessGame;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public ServerMessage() {}

    //This is the override stuff from the inital code to use

    public enum ServerMessageType {
        LOAD_GAME,
        /** game (can be any type, just needs to be called game)
         * Used by the server to send the current game state to a client.
         * When a client receives this message,
         * it will redraw the chess board.
         */
        ERROR,
        /** String errorMessage
         * This message is sent to a client when it sends an invalid command.
         * The message must include the word Error.
         */
        NOTIFICATION
        /** String message
         * 	This is a message meant to inform a player
         * 	when another player made an action.
         */
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    //ADD MY STUFF UNDER HERE: (ABOVE IS THE STARTER CODE)
    //NEED a class for the Notification, loadGame and the error
    //holds a chess game and then will send it over
    public static class Load_Game extends ServerMessage {
        public ChessGame game;
        public Load_Game(ChessGame game) {
            this.serverMessageType = ServerMessageType.LOAD_GAME;
            this.game = game;
        }
    }

    public static class Error extends ServerMessage {
        public String errorMessage;
        public Error(String errorMessage) {
            this.serverMessageType = ServerMessageType.ERROR;
            this.errorMessage = errorMessage;
        }
    }

    public static class Notification extends ServerMessage {
        public String message;
        public Notification(String message) {
            this.serverMessageType = ServerMessageType.NOTIFICATION;
            this.message = message;
        }
    }

    //This is the override stuff from the inital code to use

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
