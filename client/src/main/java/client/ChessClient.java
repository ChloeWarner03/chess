package client;

//Working on this one as well  need to fix a lot of things nad fisnh

//imports

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;


public class ChessClient implements NotificationHandler {

    private final ServerFacade server;
    private PrintStream out;

    private final SharedChess shared;
    private final ChessHelpers helpers;
    private final PreLoginChess preLogin;
    private final PostLoginChess postLogin;
    private final InGameChess inGame;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        out = System.out;
        shared = new SharedChess();
        helpers = new ChessHelpers(shared);
        preLogin = new PreLoginChess(shared, server, out);
        postLogin = new PostLoginChess(shared, server, out, helpers, this);
        inGame = new InGameChess(shared, helpers);
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
        System.out.print(helpers.help());
    }

    //keeps looping
    // waiting for chess users to input something
    private void welcomeChessLoop() {
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            helpers.chessUserPrompt();
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

    public String eval(String input) throws Exception {
        String[] tokens = input.toLowerCase().split(" ");
        String command = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (command.equals("quit")) {
            return "quit";
        }
        if (command.equals("help")) {
            return helpers.help();
        }


        if (shared.state == State.LOGGED_OUT) {
            if (command.equals("register")) {
                return preLogin.register(params);
            }
            if (command.equals("login")) {
                return preLogin.login(params);
            }

        } else if (shared.state == State.LOGGED_IN) {
            if (command.equals("logout")) {
                return postLogin.logout();
            }

            if (command.equals("create")) {
                return postLogin.createGame(params);
            }
            if (command.equals("list")) {
                return postLogin.listGames();
            }

            if (command.equals("play")) {
                return postLogin.playGame(params);
            }
            if (command.equals("observe")) {
                return postLogin.observeGame(params);
            }

            } else if (shared.state == State.IN_GAME) {

            if (command.equals("move")) {
                return inGame.makeMove(params);
            }
            if (command.equals("resign")) {
               return inGame.resign();}

            if (command.equals("leave")) {
                return inGame.leave();}

            if (command.equals("redraw")){
                return inGame.redraw();}

            if (command.equals("highlight")) {
                return inGame.highlight(params);}

            } else if (shared.state == State.OBSERVE) {

            if (command.equals("leave")) {
                return inGame.leave();}

            if (command.equals("redraw")) {
                return inGame.redraw();}

            if (command.equals("highlight")) {
                return inGame.highlight(params);}

        }

        return helpers.help();
    }





    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> whenNotified((ServerMessage.Notification) message);
            case LOAD_GAME -> whenGameLoaded((ServerMessage.LoadGame) message);
            case ERROR -> whenError((ServerMessage.Error) message);
        }
        helpers.chessUserPrompt();
    }

    private void whenNotified(ServerMessage.Notification message) {
        out.print("\n" + SET_TEXT_COLOR_GREEN + message.message + "\n");
    }

    private void whenGameLoaded(ServerMessage.LoadGame message) {
        shared.currentGame = message.game;

        boolean whitePerspective = (shared.yourColor != ChessGame.TeamColor.BLACK);
        out.print("\n");
        MakeChessBoard.createChessBoard(shared.currentGame, whitePerspective);
    }

    private void whenError(ServerMessage.Error message) {
        out.print(SET_TEXT_COLOR_RED + message.errorMessage + "\n");
    }
}


