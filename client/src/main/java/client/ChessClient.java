package client;

//Working on this one as well  need to fix a lot of things nad fisnh

//imports
import model.GameData;
import ui.MakeChessBoard;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {

    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private GameData[] cachedGames = new GameData[0];

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }
//Helpers

    //Prints a prompt so then the user will know that input is needed
    private void chessUserPrompt() {
      if (state == State.LOGGED_OUT) {
          System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
      } else {
          System.out.print("\n" + RESET_TEXT_COLOR + "[" + username + "] >>> " + SET_TEXT_COLOR_GREEN);
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

    //In the game I need to start the program, welcome them and then keep it going
    public void run() {
        startChessProgram();
        chessWelcomeMenu();
        welcomeChessLoop();
    }

    //starts the program for the game of chess
    private void startChessProgram() {
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD +
                "♕ 240 Chess Client" + RESET_TEXT_BOLD_FAINT);
    }

    //SHow the welcome message and the help menu
    //This makes it so then the user will know what they can do
    private void chessWelcomeMenu() {
        System.out.println(SET_TEXT_COLOR_WHITE +
                "Welcome to 240 chess. Type Help to get started." + RESET_TEXT_COLOR);
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
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Exception e) {
                //shows the error in red so the user knows something went wrong
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage());
            }
        }
        System.out.println();
    }


    //PRelogin UI
    // Help, Quit, Login, Register

    //PostLogin UI
    //Help, Logout, CreateGame, ListGames, PlayGame, ObserveGame


    public String eval(String input) {
        return "";
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
}
