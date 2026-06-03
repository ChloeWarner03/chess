package client;

//Working on this one as well  need to fix a lot of things nad fisnh

//imports

import java.util.Arrays;
import java.util.Scanner;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import chess.ChessPiece;

import static ui.EscapeSequences.*;


public class ChessClient {

    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private GameData[] savedGames = new GameData[0];

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
                System.out.print(SET_TEXT_COLOR_MAGENTA + result);
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
            System.out.print(RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_RED);
        } else {
            System.out.print(
                    RESET_TEXT_COLOR +
                            "[" + username + "] (help for options) >>> " +
                            SET_TEXT_COLOR_RED);
        }
    }
    //make sure that the game number is valid before using it
    private int chessVaildGameNumber(String s) throws Exception {
        int number;
        try {
            number = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new Exception("needs to be a valid number, please try again");
        }
        if (savedGames.length == 0) {
            throw new Exception("run 'list' first to see the games");
        }
        if (number < 1 || number > savedGames.length) {
            throw new Exception("pick a number between 1 and " + savedGames.length);
        }
        return number;
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.LOGGED_OUT) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            }
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    //shows different commands depending on whether the user is logged in or not
    public String help() {
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_BLUE + """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                      quit - playing chess
                      help - with possible commands
                    """ + RESET_TEXT_COLOR;
        }
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

    //PRelogin UI
    // Help, Quit, Login, Register
    public String register(String... params) throws Exception {
        if (params.length == 3) {
            AuthData myAuth = server.register(params[0], params[1], params[2]);
            authToken = myAuth.authToken();
            username = myAuth.username();
            state = State.LOGGED_IN;
            return "You are signed in as " + username;
        }
        throw new Exception("Error, you are expected to type: register <username> <password> <email>");
    }

    public String login(String... params) throws Exception {
        if (params.length == 2) {
            AuthData myAuth = server.login(params[0], params[1]);
            authToken = myAuth.authToken();
            username = myAuth.username();
            state = State.LOGGED_IN;
            return "You are signed in as " + username + " ";
        }
        throw new Exception("Error, you are expected to type: login <username> <password>");
    }


        //PostLogin UI
        //Help, Logout, CreateGame, ListGames, PlayGame, ObserveGame
    private void assertSignedIN() throws Exception {
        if (state == State.LOGGED_OUT) {
           throw new Exception("You must be logged in!");
        }
    }
    public String logout() throws Exception {
            assertSignedIN();
            server.logout(authToken);
            var myName = username;
            authToken = null;
            username = null;
            state = State.LOGGED_OUT;
            return String.format("%s logged out.%n", myName);
        }

    public String createGame(String... params) throws Exception {
        assertSignedIN();
        if (params.length >= 1) {
            var myGameName = String.join(" ", params);
            server.createGame(myGameName, authToken);
            return String.format(myGameName + " has been created! Have fun playing!");
        }
        throw new Exception("Error, you are expected to type: create <game name>");
    }

    public String listGames() throws Exception {
        assertSignedIN();
        savedGames = server.listGames(authToken);
        var result = new StringBuilder();
        for (int i = 0; i < savedGames.length; i++) {
            result.append(i + 1)
                    .append(". ")
                    .append(savedGames[i].gameName())
                    .append('\n');
        }
        return result.toString();
    }

    public String playGame(String... params) throws Exception {
        assertSignedIN();
        if (params.length == 2) {
            int myGameNumber = chessVaildGameNumber(params[0]);
            GameData myGame = savedGames[myGameNumber - 1];
            String myColor = params[1].toUpperCase();
            server.joinGame(myGame.gameID(), myColor, authToken);
            MakeChessBoard.createChessBoard(myGame.game() != null ? myGame.game() : new ChessGame(), myColor.equals("WHITE"));
            return "joined " + myGame.gameName() + " as " + myColor + "\n";
        }
        throw new Exception("Error, you are expected to type:  <number> <WHITE|BLACK>");
    }

    public String observeGame(String... params) throws Exception {
        assertSignedIN();
        if (params.length == 1) {
            int myGameNumber = chessVaildGameNumber(params[0]);
            GameData myGame = savedGames[myGameNumber - 1];
            MakeChessBoard.createChessBoard(myGame.game() != null ? myGame.game() : new ChessGame(), true);
            return String.format("watching %s", myGame.gameName());
        }
        throw new Exception("Error, you are expected to type:  <number>");
    }
}


