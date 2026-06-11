package client;
import chess.ChessPosition;
import static ui.EscapeSequences.*;

public class ChessHelpers {

    private final SharedChess shared;
    public ChessHelpers(SharedChess shared) {
        this.shared = shared;
    }
    //make sure that the game number is valid before using it
    public int chessValidGameNumber(String s) throws Exception {
        int number;
        try {
            number = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE + "needs to be a valid number, please try again" + "\n");
        }
        if (shared.savedGames.length == 0) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE + "run 'list' first to see the games" + "\n");
        }
        if (number < 1 || number > shared.savedGames.length) {
            throw new Exception(RESET_TEXT_COLOR + "pick a number between 1 and " + shared.savedGames.length + "\n");
        }
        return number;
    }

    //Prints a prompt so then the user will know that input is needed
    public void chessUserPrompt() {
        if (shared.state == State.LOGGED_OUT) {
            System.out.print(RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_BLUE);
        } else {
            System.out.print(
                    RESET_TEXT_COLOR +
                            "[" + shared.username + "] (help for options) >>> " +
                            SET_TEXT_COLOR_BLUE);
        }
    }

    //helpers
    public void playerRequiredInGame() throws Exception {
        if (shared.state != State.IN_GAME && shared.state != State.OBSERVE) {
            throw new Exception("you must be part of the game.");
        }
    }

    public ChessPosition validChessPiecePosition(String pos) throws Exception {
        if (pos.length() != 2) {
            throw new Exception("Error: wrong position input. example input: a6");
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
            default -> throw new Exception("Error: wrong column input. Letter must be between a and h.");
        };
        int row;
        try {
            row = Integer.parseInt(String.valueOf(pos.charAt(1)));
        } catch (NumberFormatException e) {
            throw new Exception("Error: wrong row input. Number must be between 1 and 8.");
        }
        if (row < 1 || row > 8) {
            throw new Exception("Error: row must be between 1 and 8.");
        }
        return new ChessPosition(row, col);
    }

    //shows different commands depending on whether the user is logged in or not
    public String help() {
        if (shared.state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_BLUE + """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - stop playing chess
                help - with possible commands
                """ + RESET_TEXT_COLOR;
        }
        if (shared.state == State.LOGGED_IN) {
            return SET_TEXT_COLOR_BLUE + """
                create <NAME> - a game
                list - games
                play <NUMBER> [WHITE|BLACK] - a game
                observe <NUMBER> - a game
                logout - when you are done
                quit - stop  playing chess
                help - with possible commands
                """ + RESET_TEXT_COLOR;
        }
        if (shared.state == State.OBSERVE) {
            return SET_TEXT_COLOR_BLUE + """
                redraw - the chess board
                highlight <position> - moves you are able to preform (example input: highlight h3)
                leave - the chess game
                help - with possible commands
                """ + RESET_TEXT_COLOR;
        }
        return SET_TEXT_COLOR_BLUE + """
            move <start> <end> [promotion] - a piece (example input: move a3 a5)
            redraw - redraw the chess board
            highlight <position> - moves you are able to preform (example input: highlight h3)
            leave - the chess game
            resign - forfeit the game
            help - with possible commands
            """ + RESET_TEXT_COLOR;
    }





}
