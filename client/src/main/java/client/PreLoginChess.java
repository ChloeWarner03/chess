package client;

import model.AuthData;
import java.io.PrintStream;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PreLoginChess {

    private final SharedChess shared;
    private final ServerFacade server;
    private final PrintStream out;

    public PreLoginChess(SharedChess  shared,  ServerFacade server, PrintStream out) {
        this.shared =  shared;
        this.server =  server ;
        this.out = out;
    }

    //PRelogin UI
    // Help, Quit, Login, Register
    public String register(String... params) throws Exception {
        if (shared.state != State.LOGGED_OUT) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE + "You are already logged in.");
        }
        if (params.length != 3) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "Error, you are expected to type: register <username> <password> <email>");
        }
        AuthData auth = server.register(params[0], params[1], params[2]);
        shared.authToken = auth.authToken();
        shared.username = auth.username();
        shared.state = State.LOGGED_IN;
        out.printf(RESET_TEXT_COLOR + "You are signed in as " + shared.username + "\n");
        return "register";
    }

    public String login(String... params) throws Exception {
        if (shared.state != State.LOGGED_OUT) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE + "You are already logged in.");
        }
        if (params.length != 2) {
            throw new Exception(RESET_TEXT_COLOR + SET_TEXT_COLOR_MAGENTA + "Error, you are expected to type: login <username> <password>" + "\n");
        }
        AuthData auth = server.login(params[0], params[1]);
        shared.authToken = auth.authToken();
        shared.username = auth.username();
        shared.state = State.LOGGED_IN;
        out.printf("You are signed in as " + shared.username + " " + "\n");
        return "login";
    }


}
