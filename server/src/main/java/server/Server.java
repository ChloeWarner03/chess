package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.BeenTakenException;
import service.BadRequest;
import service.gameplayManage;
import service.UnauthorizedException;
import service.userManage;
import dataaccess.SqlAccess;

import java.util.Map;

//There is a recommendation for this one to have a constant instead of duplicating
// for gameID, game, authroization and message

public class Server {

    //services and data access
    private final DataAccess dataAccess;
    //For phase 4 I changed MemoryDataAccess to this
    private final userManage userManage;
    private final gameplayManage gameplayManage;
    private final Gson gson = new Gson();
    private final Javalin javalin;

    public Server() {
        try {
            dataAccess = new SqlAccess();
        } catch (DataException e) {
            throw new RuntimeException(e) ;

        }

        userManage = new userManage(dataAccess);
        gameplayManage = new gameplayManage(dataAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        //Endpoints
        javalin.post("/user",  this::register);
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

        javalin.exception(BeenTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result(gson.toJson(
                    Map.of("message", "Error: already taken")));
        });

        javalin.exception(BadRequest.class, (e, ctx) -> {
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
    //run
    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }
    //stop
    public void stop() {
        javalin.stop();
    }

    //register new user
    private void register(Context ctx)
            throws DataException, BadRequest {

        UserData user = gson.fromJson(ctx.body(), UserData.class);
        AuthData result = userManage.register(user);

        ctx.result(gson.toJson(result));
    }

    //Login user
    private void  login(Context ctx)
            throws DataException,  UnauthorizedException {

        UserData user = gson.fromJson(ctx.body(), UserData.class);
        AuthData  result = userManage.login(user);

        ctx.result (gson.toJson(result));
    }

    //Logout user
    private void  logout(Context ctx)
            throws  DataException, UnauthorizedException {

        String authToken  = ctx.header("authorization");
        userManage.logout(authToken);

        ctx.result("{}");
    }

    //Create
    private void createGame (Context ctx)
            throws  DataException,
            UnauthorizedException ,
            BadRequest {

        String authToken = ctx.header("authorization");

        var body =  gson.fromJson(ctx.body(), Map.class);
        String gameName  =  (String) body.get("gameName");


        int gameID  = gameplayManage.createGame(authToken, gameName);
        ctx.result (gson.toJson(Map.of("gameID", gameID)));
    }

    //List games
    private void  listGames(Context ctx)
            throws  DataException,
            UnauthorizedException  {

        String authToken =  ctx.header("authorization");
        var result =  gameplayManage.listGames(authToken);

        ctx.result(gson.toJson(Map.of("games", result)));
    }

    //Join a game
    private void  joinGame(Context ctx)
            throws  DataException,
            UnauthorizedException ,
            BadRequest ,
            BeenTakenException  {

        //get token
        String authToken =  ctx.header("authorization");
        //get info
        var body = gson.fromJson(ctx.body(), Map.class);
        String playerColor = (String) body.get("playerColor");
        //Check if gameID is null before parsing
        if (body.get("gameID") == null) {
            throw new BadRequest("Missing gameID");
        }
        int gameID = ((Double) body.get("gameID")).intValue();
        gameplayManage.joinGame(authToken, playerColor, gameID);
        ctx.json("{}");
    }

    //Clear
    private void clear(Context ctx)
            throws DataException {

        gameplayManage.clear();
        ctx.result("{}");
    }


}