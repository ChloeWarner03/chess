package server;

//These are my imports
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.UserData;
import service.AlreadyTakenException;
import service.UserService;
import java.util.Map;
import service.UnauthorizedException;

public class Server {
    //These are my services and data access
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final Gson gson = new Gson();
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        //This is where I put my endpoints
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        //This catches any errors I did not handle
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        });
    }

    //This is for registering a new user
    private void register(Context ctx) {
        try {
            //Get the user info from the request
            UserData user = gson.fromJson(ctx.body(), UserData.class);
            //Try to register them
            var result = userService.register(user);
            ctx.json(gson.toJson(result));
        } catch (AlreadyTakenException e) {
            //Username is taken
            ctx.status(403);
            ctx.json(Map.of("message", "Error: already taken"));
        } catch (DataAccessException e) {
            //Something went wrong
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    //This handles logging in a user
    private void login(Context ctx) {
        try {
            //Get the user info from the request
            UserData user = gson.fromJson(ctx.body(), UserData.class);
            //Try to log them in
            var result = userService.login(user);
            ctx.json(gson.toJson(result));
        } catch (UnauthorizedException e) {
            //Wrong username or password
            ctx.status(401);
            ctx.json(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}