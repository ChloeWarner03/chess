package client;
//NEED TO LOOK AT AGAIN AND WRITE THE TESTS
import  com.google.gson.Gson;
import  model.AuthData;
import model.GameData;

import  java.io.*;
import java.net.*;
import java.util.Map;

public class  ServerFacade {
    private final String myUrlForServer;


    private  final Gson gson = new Gson();
    public  ServerFacade(int  port) {
        myUrlForServer =  "http://localhost:" + port;
    }
    public ServerFacade(String  url) {
        myUrlForServer  = url;
    }

    public String getTheServerURL() {
        return myUrlForServer;
    }

    //helpers (need ot make better, personal)
    private <T> T  sendToTheServer(String method,  String path,
                                  Object body,  Class<T> responseClass,
                                  String authToken) throws Exception {
        URL myUrl  =  new URL(myUrlForServer + path);
        HttpURLConnection myConnection = (HttpURLConnection) myUrl.openConnection();
        myConnection.setRequestMethod( method);
        myConnection.setRequestProperty( "Content-Type", "application/json");
        if (authToken  !=  null) {
            myConnection.setRequestProperty( "Authorization", authToken);
        }
        if (body !=  null) {
            attachAsJson(myConnection,  body);
        }
        myConnection.connect();
        int theResponseCode =  myConnection.getResponseCode();
        if (theResponseCode  >=  400) {
             errorOut(myConnection ,  theResponseCode);
        }
        return  readTheResponse( myConnection , responseClass);
    }

    private void  attachAsJson(HttpURLConnection  myConnection, Object body) throws Exception {
        myConnection.setDoOutput(true);

        try (OutputStream myOutputStream  = myConnection.getOutputStream()) {
            myOutputStream.write(gson.toJson(body).getBytes());
        }
    }

    private void errorOut(HttpURLConnection  myConnection, int theResponseCode) throws Exception {
        InputStream myErrorStream  = myConnection.getErrorStream();
        String thereIsAnError  = "Error " + theResponseCode;

        if  (myErrorStream != null) {
            //server sends back json like: { "message": "Error: already taken" }
            record  ErrorResponse(String message) {}
            var myErrorBody = gson.fromJson(new InputStreamReader(myErrorStream), ErrorResponse.class);
            if  (myErrorBody != null && myErrorBody.message() != null) {
                thereIsAnError = myErrorBody.message();
            }
        }
        throw  new  Exception(thereIsAnError);
    }

    //These are the required methods for the server:


    private <T>  T readTheResponse(HttpURLConnection myConnection, Class<T> responseClass) throws Exception {
        if (responseClass  == null) {
            return null;
        }
        try (InputStream  myInputStream = myConnection.getInputStream()) {
            return  gson.fromJson(new InputStreamReader(myInputStream), responseClass);
        }
    }


//this part is the register
    public AuthData  register(String username, String password, String email) throws Exception {
        var myRegisterBody  = Map.of(
                "username",  username,
              "password" ,  password,
               "email",     email
        );
        return sendToTheServer("POST", "/user", myRegisterBody, AuthData.class, null);
    }
    //login
    public  AuthData  login(String username, String password) throws Exception {
        var myLoginBody = Map.of(
                "username",  username,
                "password",  password
        );
        return  sendToTheServer("POST",  "/session",  myLoginBody, AuthData.class, null);
    }

    //logout
    public void logout (String authToken) throws Exception {
        sendToTheServer("DELETE",   "/session", null, null, authToken);
    }

    //create the games
    public  int  createGame ( String gameName,  String authToken) throws Exception {
        var chessGameBody = Map.of("gameName", gameName);
        record  CreatedChessGame(int gameID) {}
        var  myNewGame  =  sendToTheServer("POST",  "/game", chessGameBody, CreatedChessGame.class, authToken);
        return  myNewGame.gameID();
    }

    //join the games
    public  void  joinGame( int gameID,  String playerColor, String authToken) throws Exception {
        var myJoinBody = Map.of(
                "gameID",       gameID,
                "playerColor", playerColor
        );
        sendToTheServer( "PUT", "/game" , myJoinBody,  null, authToken);
    }
    //list the games
    public GameData[]  listGames(String authToken) throws Exception {
        record ListResponse(GameData[]  games)  {}
        var myGameList = sendToTheServer ("GET", "/game", null, ListResponse.class, authToken);
        return  myGameList.games() ;
    }


//clear
    public void clear()  throws  Exception {
        sendToTheServer( "DELETE",  "/db", null, null, null);
    }

}