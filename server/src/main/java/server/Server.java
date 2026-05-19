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
import service.GameService;
import service.BadRequestException;

public class Server {
    //These are my services and data access
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final Gson gson = new Gson();
    private final GameService gameService = new GameService(dataAccess);
    private final Javalin javalin;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        //This is where I put my endpoints
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.delete("/db", this::clear);
        javalin.post("/game", this::createGame);
        javalin.get("/game", this::listGames);
        javalin.put("/game", this::joinGame);
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

    //This handles logging out a user
    private void logout(Context ctx) {
        try {
            //Get the auth token from the header
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.json("{}");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    //This clears everything out
    private void clear(Context ctx) {
        try {
            gameService.clear();
            ctx.json("{}");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    //This is thrown when the request is missing required info
    public class BadRequestException extends Exception {
        public BadRequestException(String message) {
            super(message);
        }
    }

    //This handles creating a new game
    private void createGame(Context ctx) {
        try {
            //Get the auth token from the header
            String authToken = ctx.header("authorization");
            //Get the game name from the request body
            var body = gson.fromJson(ctx.body(), Map.class);
            String gameName = (String) body.get("gameName");
            //Try to create the game
            int gameID = gameService.createGame(authToken, gameName);
            ctx.json(gson.toJson(Map.of("gameID", gameID)));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: unauthorized"));
        } catch (service.BadRequestException e) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    //This handles listing all games
    private void listGames(Context ctx) {
        try {
            //Get the auth token from the header
            String authToken = ctx.header("authorization");
            var result = gameService.listGames(authToken);
            ctx.json(gson.toJson(Map.of("games", result)));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    //This handles joining a game
    private void joinGame(Context ctx) {
        try {
            //Get the auth token from the header
            String authToken = ctx.header("authorization");
            //Get the game info from the request body
            var body = gson.fromJson(ctx.body(), Map.class);
            String playerColor = (String) body.get("playerColor");
            int gameID = ((Double) body.get("gameID")).intValue();
            gameService.joinGame(authToken, playerColor, gameID);
            ctx.json("{}");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: unauthorized"));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(Map.of("message", "Error: already taken"));
        } catch (service.BadRequestException e) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}