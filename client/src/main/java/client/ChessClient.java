package client;

//Working on this one as well  need to fix a lot of things nad fisnh

//imports

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.AuthData;
import model.GameData;
import websocket.messages.ServerMessage;

import chess.*;

import static ui.EscapeSequences.*;


public class ChessClient implements NotificationHandler {

    private final ServerFacade server;
    private PrintStream out;

    private String username;
    private String authToken;
    private State state = State.LOGGED_OUT;

    private GameData[] savedGames;
    private WebSocketFacade gameWebSocket;
    private ChessGame currentGame;
    private int openGameNumber;
    private ChessGame.TeamColor yourColor;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        out = System.out;
    }

//Helpers

    //In the game I need to start the program, welcome them and then keep it going
    public void run() {
        startChessProgram();
        welcomeChessLoop();
    }

    //starts the program for the game of chess
    //Welcomes the user and then I need ot make it show what they can do
    private void startChessProgram() {
        System.out.println(RESET_TEXT_COLOR + "Welcome to 240 chess" + SET_TEXT_COLOR_BLUE);
        System.out.print(help());
    }

    //keeps looping
    // waiting for chess users to input something
    private void welcomeChessLoop() {
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            chessUserPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(RESET_TEXT_COLOR + SET_TEXT_COLOR_RED + result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    //Prints a prompt so then the user will know that input is needed
    private void chessUserPrompt() {
        if (state == State.LOGGED_OUT) {
            System.out.print(RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_BLUE);
        } else {
            System.out.print(
                    RESET_TEXT_COLOR +
                            "[" + username + "] (help for options) >>> " +
                            SET_TEXT_COLOR_BLUE);
        }
    }

    public String eval(String input) throws Exception {
        String[] tokens = input.toLowerCase().split(" ");
        String command = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (command.equals("quit")) {
            return "quit";
        }
        if (command.equals("help")) {
            return help();
        }


        if (state == State.LOGGED_OUT) {
            if (command.equals("register")) {
                return register(params);
            }
            if (command.equals("login")) {
                return login(params);
            }

        } else if (state == State.LOGGED_IN) {
            if (command.equals("logout")) {
                return logout();
            }

            if (command.equals("create")) {
                return createGame(params);
            }
            if (command.equals("list")) {
                return listGames();
            }

            if (command.equals("play")) {
                return playGame(params);
            }
            if (command.equals("observe")) {
                return observeGame(params);
            }

            } else if (state == State.IN_GAME) {
            if (command.equals("move")) return makeMove(params);
            if (command.equals("resign")) return resign();
            if (command.equals("leave")) return leave();
            if (command.equals("redraw")) return redraw();
            if (command.equals("highlight")) return highlight(params);
            } else if (state == State.OBSERVE) {
            if (command.equals("leave")) return leave();
            if (command.equals("redraw")) return redraw();
            if (command.equals("highlight")) return highlight(params);
        }

        return help();
    }

    // HElpers to get it from WEBSOCKET FACADE
    public String resign() throws Exception {
        requireInGame();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure? type: (yes/no) ");

        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            gameWebSocket.resign(authToken, openGameNumber);
            return "resigned";
        }

        return "cancelled";
    }
    public String leave() throws Exception {
        requireInGame();
        gameWebSocket.leave(authToken, openGameNumber);

        state = State.LOGGED_IN;

        return "You left the game";
    }

    public String redraw() throws Exception {
        requireInGame();

        MakeChessBoard.createChessBoard(
                currentGame,
                yourColor == ChessGame.TeamColor.WHITE || yourColor == null
        );

        return "";
    }
    public String highlight(String... params) throws Exception {
        requireInGame();

        if (params.length != 1) {
            throw new Exception("usage: highlight <position>");
        }

        MakeChessBoard.highlightMoves(
                currentGame,
                params[0],
                yourColor == ChessGame.TeamColor.WHITE || yourColor == null
        );

        return "";
    }

    private String makeMove(String... params) throws Exception {
        requireInGame();

        if (params.length < 2 || params.length > 3) {
            throw new Exception("Type: move <start> <end> [promotion] (example: move e4 e6)");
        }

        ChessPosition start;
        ChessPosition end;

        try {
            start = chessValidPosition(params[0]);
            end = chessValidPosition(params[1]);
        } catch (Exception e) {
            throw new Exception("Error: need to type: example: move e2 e4");
        }

        ChessPiece.PieceType promotion = null;

        if (params.length == 3) {
            switch (params[2].toLowerCase()) {
                case "queen" -> promotion = ChessPiece.PieceType.QUEEN;
                case "rook" -> promotion = ChessPiece.PieceType.ROOK;
                case "bishop" -> promotion = ChessPiece.PieceType.BISHOP;
                case "knight" -> promotion = ChessPiece.PieceType.KNIGHT;
                default -> throw new Exception("Error: not a piece you can promote.");
            }
        }

        ChessMove move = new ChessMove(start, end, promotion);

        gameWebSocket.make_move(authToken, openGameNumber, move);

        return "move has been sent!";
    }



    //shows different commands depending on whether the user is logged in or not
    private String help() {
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_BLUE + """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """ + RESET_TEXT_COLOR;
        }
        if (state == State.LOGGED_IN) {
            return SET_TEXT_COLOR_BLUE + """
                create <NAME> - a game
                list - games
                play <NUMBER> [WHITE|BLACK] - a game
                observe <NUMBER> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """ + RESET_TEXT_COLOR;
        }
        if (state == State.OBSERVE) {
            return SET_TEXT_COLOR_BLUE + """
                redraw - the chess board
                highlight <position> - legal moves (example input: highlight f3)
                leave - the game
                help - with possible commands
                """ + RESET_TEXT_COLOR;
        }
        return SET_TEXT_COLOR_BLUE + """
            move <start> <end> [promotion] - a piece (example input: move a3 a5)
            redraw - the chess board
            highlight <position> - legal moves (example input: highlight f3)
            leave - the game
            resign - forfeit the game
            help - with possible commands
            """ + RESET_TEXT_COLOR;
    }

    //make sure that the game number is valid before using it
    private int chessValidGameNumber(String s) throws Exception {
        int number;
        try {
            number = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE + "needs to be a valid number, please try again" + "\n");
        }
        if (savedGames.length == 0) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE + "run 'list' first to see the games" + "\n");
        }
        if (number < 1 || number > savedGames.length) {
            throw new Exception(RESET_TEXT_COLOR + "pick a number between 1 and " + savedGames.length + "\n");
        }
        return number;
    }

    //PRelogin UI
    // Help, Quit, Login, Register
    public String register(String... params) throws Exception {
        if (state != State.LOGGED_OUT) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE + "you're already logged in. You can type 'help' for options.");
        }
        if (params.length != 3) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "Error, you are expected to type: register <username> <password> <email>");
        }
        AuthData auth = server.register(params[0], params[1], params[2]);
        authToken = auth.authToken();
        username = auth.username();
        state = State.LOGGED_IN;
        out.printf(RESET_TEXT_COLOR + "You are signed in as " + username + "\n");
        return "register";
    }

    public String login(String... params) throws Exception {
        if (state != State.LOGGED_OUT) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE + "you are already logged in. type help for options.");
        }
        if (params.length != 2) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "Error, you are expected to type: login <username> <password>" + "\n");
        }
        AuthData auth = server.login(params[0], params[1]);
        authToken = auth.authToken();
        username = auth.username();
        state = State.LOGGED_IN;
        out.printf("You are signed in as " + username + " " + "\n");
        return "login";
    }


    //PostLogin UI
    //Help, Logout, CreateGame, ListGames, PlayGame, ObserveGame
    private void userSignedIn() throws Exception {
        if (state == State.LOGGED_OUT) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "You must be logged in!");
        }
    }

    public String logout() throws Exception {
        userSignedIn();
        server.logout(authToken);
        out.printf(RESET_TEXT_COLOR + "%s logged out.\n", username);
        var myName = username;
        authToken = null;
        username = null;
        state = State.LOGGED_OUT;
        return "logout";
    }

    public String createGame(String... params) throws Exception {
        userSignedIn();
        if (params.length >= 1) {
            var myGameName = String.join(" ", params);
            server.createGame(myGameName, authToken);
            return String.format(RESET_TEXT_COLOR + myGameName + " has been created! Have fun playing!" + "\n");
        }
        throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "Error, you are expected to type: create <game name>" + "\n");
    }

    public String listGames() throws Exception {
        userSignedIn();
        savedGames = server.listGames(authToken);
        // no games found
        if (savedGames.length == 0) {
            return "No games yet.";
        }
        var result = new StringBuilder();
        for (int i = 0; i < savedGames.length; i++) {
            GameData saves = savedGames[i];
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
        int myGameNumber = chessValidGameNumber(params[0]);
        GameData myGame = savedGames[myGameNumber - 1];
        String myColor = params[1].toUpperCase();
        server.joinGame(myGame.gameID(), myColor, authToken);

        //Adding in the websocket stuff here
        openGameNumber = myGame.gameID();
        yourColor = ChessGame.TeamColor.valueOf(myColor);
        state = State.IN_GAME;
        gameWebSocket = new WebSocketFacade(server.getTheServerURL(), this);

        gameWebSocket.connect(authToken, openGameNumber);

        MakeChessBoard.createChessBoard(myGame.game() != null ? myGame.game() : new ChessGame(), myColor.equals("WHITE"));
        return "joined " + myGame.gameName() + " as " + myColor + "\n";
    }

    public String observeGame(String... params) throws Exception {
        userSignedIn();
        if (params.length != 1) {
            throw new Exception("usage: observe <NUMBER>");
        }
        int gameNumber = chessValidGameNumber(params[0]);
        GameData game = savedGames[gameNumber - 1];

        yourColor = null;
        openGameNumber = game.gameID();
        state = State.OBSERVE;
        gameWebSocket = new WebSocketFacade(server.getTheServerURL(), this);
        gameWebSocket.connect(authToken, openGameNumber);

        return "observe";
    }

    //helpers
    private void requireInGame() throws Exception {
        if (state != State.IN_GAME && state != State.OBSERVE) {
            throw new Exception("you must be in a game. type help for options.");
        }
    }

    private ChessPosition chessValidPosition(String pos) throws Exception {
        if (pos.length() != 2) {
            throw new Exception("invalid position '" + pos + "'. example: e2");
        }
        int col = switch (pos.charAt(0)) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new Exception("invalid column '" + pos.charAt(0) + "'. use a-h.");
        };
        int row;
        try {
            row = Integer.parseInt(String.valueOf(pos.charAt(1)));
        } catch (NumberFormatException e) {
            throw new Exception("invalid row '" + pos.charAt(1) + "'. use 1-8.");
        }
        if (row < 1 || row > 8) {
            throw new Exception("row must be between 1 and 8.");
        }
        return new ChessPosition(row, col);
    }


    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> onNotification((ServerMessage.Notification) message);
            case LOAD_GAME -> onLoadGame((ServerMessage.Load_Game) message);
            case ERROR -> onError((ServerMessage.Error) message);
        }
        chessUserPrompt();
    }

    private void onNotification(ServerMessage.Notification message) {
        out.print("\n" + SET_TEXT_COLOR_MAGENTA + message.message + "\n");
    }

    private void onLoadGame(ServerMessage.Load_Game message) {
        currentGame = message.game;

        boolean whitePerspective = (yourColor != ChessGame.TeamColor.BLACK);
        out.print("\n");
        MakeChessBoard.createChessBoard(currentGame, whitePerspective);
    }

    private void onError(ServerMessage.Error message) {
        out.print(SET_TEXT_COLOR_RED + message.errorMessage + "\n");
    }
}


