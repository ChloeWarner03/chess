package client;

//Working on this one as well  need to fix a lot of things nad fisnh

//imports
import model.GameData;

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
    private void printPrompt() {
      if (state == State.LOGGED_OUT) {
          System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED OUT] >>> " + SET_TEXT_COLOR_GREEN);
      } else {
          System.out.print("\n" + RESET_TEXT_COLOR + "[" + username + "] >>> " + SET_TEXT_COLOR_GREEN);
      }
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD +
                "♕ Welcome to Chess! Type 'help' to get started." + RESET_TEXT_BOLD_FAINT);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Exception e) {
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage());
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        return "";
    }

    public String help() {
        return "";
    }
}
