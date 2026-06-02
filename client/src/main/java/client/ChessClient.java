package client;

//Working on this one as well  need to fix a lot of things nad fisnh

//imports

import java.util.Arrays;
import java.util.Scanner;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import ui.MakeChessBoard;

import static ui.EscapeSequences.*;


public class ChessClient{

    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private GameData[] cachedGames = new GameData[0];

    public ChessClient(String serverUrl) throws ResponseException {
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
        System.out.println(RESET_TEXT_COLOR + "Welcome to 240 chess. Type Help to get started." + SET_TEXT_COLOR_BLUE);
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
                var msg = e.toString();
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
            System.out.print(RESET_TEXT_COLOR + "[" + username + "] >>> " + SET_TEXT_COLOR_RED);
        }
    }
    //make sure that the game number is valid before using it
    private int chessVaildGameNumber(String s) throws Exception {
        int number;
        try {
            number = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new Exception("that is not a valid number, try again!");
        }
        if (cachedGames.length == 0) {
            throw new Exception("run 'list' first so we can see the games!");
        }
        if (number < 1 || number > cachedGames.length) {
            throw new Exception("pick a number between 1 and " + cachedGames.length);
        }
        return number;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        }

        public String eval (String input){
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
        public String help () {
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

        //PostLogin UI
        //Help, Logout, CreateGame, ListGames, PlayGame, ObserveGame
    }

