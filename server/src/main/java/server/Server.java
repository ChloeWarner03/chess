package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import service.UserService;

import java.util.Map;

public class Server {

    //These are my services and data access
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final GameService gameService = new GameService(dataAccess);
    private final Gson gson = new Gson();
    private final Javalin javalin;

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        //Endpoints
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.delete("/db", this::clear);

        javalin.post("/game", this::createGame);
        javalin.get("/game", this::listGames);
        javalin.put("/game", this::joinGame);

        //Exception handlers
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.result(gson.toJson(
                    Map.of("message", "Error: unauthorized")));
        });

        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result(gson.toJson(
                    Map.of("message", "Error: already taken")));
        });

        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.result(gson.toJson(
                    Map.of("message", "Error: bad request")));
        });

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result(gson.toJson(
                    Map.of("message", "Error: " + e.getMessage())));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    //Register a new user
    private void register(Context ctx)
            throws DataAccessException, BadRequestException {

        UserData user = gson.fromJson(ctx.body(), UserData.class);

        AuthData result = userService.register(user);

        ctx.result(gson.toJson(result));
    }

    //Login a user
    private void login(Context ctx)
            throws DataAccessException, UnauthorizedException {

        UserData user = gson.fromJson(ctx.body(), UserData.class);

        AuthData result = userService.login(user);

        ctx.result(gson.toJson(result));
    }

    //Logout a user
    private void logout(Context ctx)
            throws DataAccessException, UnauthorizedException {

        String authToken = ctx.header("authorization");

        userService.logout(authToken);

        ctx.result("{}");
    }

    //Clear database
    private void clear(Context ctx)
            throws DataAccessException {

        gameService.clear();

        ctx.result("{}");
    }

    //Create a game
    private void createGame(Context ctx)
            throws DataAccessException,
            UnauthorizedException,
            BadRequestException {

        String authToken = ctx.header("authorization");

        var body = gson.fromJson(ctx.body(), Map.class);

        String gameName = (String) body.get("gameName");

        int gameID = gameService.createGame(authToken, gameName);

        ctx.result(gson.toJson(Map.of("gameID", gameID)));
    }

    //List all games
    private void listGames(Context ctx)
            throws DataAccessException,
            UnauthorizedException {

        String authToken = ctx.header("authorization");

        var result = gameService.listGames(authToken);

        ctx.result(gson.toJson(Map.of("games", result)));
    }

    //Join a game
    private void joinGame(Context ctx)
            throws DataAccessException,
            UnauthorizedException,
            BadRequestException,
            AlreadyTakenException {

        //Get the auth token from the header
        String authToken = ctx.header("authorization");
        //Get the game info from the request body
        var body = gson.fromJson(ctx.body(), Map.class);
        String playerColor = (String) body.get("playerColor");
        //Check if gameID is null before parsing
        Object gameIDObj = body.get("gameID");
        if (gameIDObj == null) {
            throw new BadRequestException("Missing gameID");
        }
        int gameID = ((Double) gameIDObj).intValue();
        gameService.joinGame(authToken, playerColor, gameID);
        ctx.json("{}");
    }
}