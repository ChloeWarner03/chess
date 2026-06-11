package client;
import static ui.EscapeSequences.*;
import java.io.PrintStream;
import chess.ChessGame;
import client.websocket.WebSocketFacade;
import model.GameData;
import websocket.messages.ServerMessage;

public class PostLoginChess {

    private final SharedChess shared;
    private final ServerFacade server;
    private final PrintStream out;
    private final ChessHelpers helpers;
    private final ChessClient notificationHandler;

    public PostLoginChess(SharedChess shared, ServerFacade server, PrintStream out,
                          ChessHelpers helpers, ChessClient notificationHandler) {
        this.shared = shared;
        this.server = server;
        this.out = out;
        this.helpers = helpers;
        this.notificationHandler = notificationHandler;
    }

    //PostLogin UI
    //Help, Logout, CreateGame, ListGames, PlayGame, ObserveGame
    private void userSignedIn() throws Exception {
        if (shared.state == State.LOGGED_OUT) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "You must be logged in!");
        }
    }

    public String logout() throws Exception {
        userSignedIn();
        server.logout(shared.authToken);
        out.printf(RESET_TEXT_COLOR + SET_TEXT_COLOR_GREEN + "%s logged out.\n", shared.username);
        var myName = shared.username;
        shared.authToken = null;
        shared.username = null;
        shared.state = State.LOGGED_OUT;
        return "logout";
    }

    public String createGame(String... params) throws Exception {
        userSignedIn();
        if (params.length >= 1) {
            var myGameName = String.join(" ", params);
            server.createGame(myGameName, shared.authToken);
            return String.format(RESET_TEXT_COLOR + SET_TEXT_COLOR_GREEN + myGameName + " has been created! Have fun playing!" + "\n");
        }
        throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "Error, you are expected to type: create <game name>" + "\n");
    }

    //PRelogin UI
    // Help, Quit, Login, Register

    public String listGames() throws Exception {
        userSignedIn();
        shared.savedGames = server.listGames(shared.authToken);
        // no games found
        if (shared.savedGames.length == 0) {
            return "No games yet.";
        }
        var result = new StringBuilder();
        for (int i = 0; i < shared.savedGames.length; i++) {
            GameData saves = shared.savedGames[i];
            // show open if no one has taken the spot
            String white;
            if (saves.whiteUsername() != null) {
                white = saves.whiteUsername();
            } else {
                white = "(open spot)";
            }
            String black;
            if (saves.blackUsername() != null) {
                black = saves.blackUsername();
            } else {
                black = "(open spot)";
            }
            result.append(String.format("%n%d. %s%n", i + 1, saves.gameName()));
            result.append(String.format("   White: %s%n", white));
            result.append(String.format("   Black: %s%n", black));
        }
        return result.toString().stripTrailing() + "\n";
    }

    public String playGame(String... params) throws Exception {
        userSignedIn();
        if (params.length != 2) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "Error, you are expected to type:  <number> <WHITE|BLACK>" + "\n");
        }
        shared.savedGames = server.listGames(shared.authToken);
        int myGameNumber = helpers.chessValidGameNumber(params[0]);
        GameData myGame = shared.savedGames[myGameNumber - 1];
        String myColor = params[1].toUpperCase();
        server.joinGame(myGame.gameID(), myColor, shared.authToken);

        //Adding in the websocket stuff here
        shared.openGameNumber = myGame.gameID();
        shared.yourColor = ChessGame.TeamColor.valueOf(myColor);
        shared.state = State.IN_GAME;
        shared.gameWebSocket = new WebSocketFacade(server.getTheServerURL(), notificationHandler);

        shared.gameWebSocket.connect(shared.authToken, shared.openGameNumber);
        return "joined " + myGame.gameName() + " as " + myColor + "\n";
    }

    public String observeGame(String... params) throws Exception {
        userSignedIn();
        if (params.length != 1) {
            throw new Exception("Use: observe <NUMBER>");
        }
        int gameNumber = helpers.chessValidGameNumber(params[0]);
        GameData game = shared.savedGames[gameNumber - 1];

        shared.yourColor = null;
        shared.openGameNumber = game.gameID();
        shared.state = State.OBSERVE;
        shared.gameWebSocket = new WebSocketFacade(server.getTheServerURL(), notificationHandler);
        shared.gameWebSocket.connect(shared.authToken, shared.openGameNumber);

        return "You are now an observer";
    }
}
