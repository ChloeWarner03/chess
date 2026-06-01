package client;
//NEED TO LOOK AT AGAIN AND WRITE THE TESTS
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();
    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }
    public ServerFacade(String url) {
        serverUrl = url;
    }

    //helpers (need ot make better, personal)
    private <T> T sendToTheServer(String method, String path,
                                  Object body, Class<T> responseClass,
                                  String authToken) throws Exception {

        // Step 1: Open a connection to the server
        URL url = new URL(serverUrl + path);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", "application/json");

        // Step 2: Attach our auth token if we have one
        if (authToken != null) {
            http.setRequestProperty("Authorization", authToken);
        }

        // Step 3: Attach a body if we have one (convert Java object → JSON string)
        if (body != null) {
            http.setDoOutput(true);
            try (OutputStream os = http.getOutputStream()) {
                os.write(gson.toJson(body).getBytes());
            }
        }

        // Step 4: Actually send the request!
        http.connect();
        int status = http.getResponseCode();

        // Step 5: Check if something went wrong (4xx or 5xx status code)
        if (status >= 400) {
            thereIsAnError(http, status);
        }

        // Step 6: Read and return the response if we need one
        return readTheResponse(http, responseClass);
    }

    private void thereIsAnError(HttpURLConnection http, int status) throws Exception {
        InputStream errStream = http.getErrorStream();
        String errorMessage = "Error " + status; // fallback if we can't read the body

        if (errStream != null) {
            // The server sends back JSON like: { "message": "Error: already taken" }
            record ErrorResponse(String message) {}
            var errBody = gson.fromJson(new InputStreamReader(errStream), ErrorResponse.class);
            if (errBody != null && errBody.message() != null) {
                errorMessage = errBody.message(); // use the server's actual message!
            }
        }

        throw new Exception(errorMessage);
    }

    private <T> T readTheResponse(HttpURLConnection http, Class<T> responseClass) throws Exception {
        // Some requests don't need a response body (like logout or joinGame)
        if (responseClass == null) {
            return null;
        }

        // Read the response stream and convert JSON → Java object
        try (InputStream in = http.getInputStream()) {
            return gson.fromJson(new InputStreamReader(in), responseClass);
        }
    }
//this part is the register
    public AuthData register(String username, String password, String email) throws Exception {
        var body = Map.of(
                "username", username,
                "password", password,
                "email",    email
     );
        return sendToTheServer("POST", "/user", body, AuthData.class, null);
    }
    //login
    public AuthData login(String username, String password) throws Exception {
        var body = Map.of(
                "username", username,
                "password", password
        );
        return sendToTheServer("POST", "/session", body, AuthData.class, null);
    }

    //logout
    public void logout(String authToken) throws Exception {
        //sendToTheServer("DELETE", "/session", null, null, authToken);
    }
    //create the games
    public int createGame(String gameName, String authToken) throws Exception {
        var body = Map.of("gameName", gameName);
        //record CreateResponse(int gameID) {}
       // var response = sendToTheServer("POST", "/game", body, CreateResponse.class, authToken);
        return response.gameID();
    }

    //join the games
    public void joinGame(int gameID, String playerColor, String authToken) throws Exception {
        var body = Map.of(
                "gameID",      gameID,
                "playerColor", playerColor
        //);
        //sendToTheServer("PUT", "/game", body, null, authToken);
    }

    //list the games
    public GameData[] listGames(String authToken) throws Exception {
        record ListResponse(GameData[] games) {}
        //var response = sendToTheServer("GET", "/game", null, ListResponse.class, authToken);
        //return response.games();
    }


//clear
    public void clear() throws Exception {
        sendToTheServer("DELETE", "/db", null, null, null);
    }

}