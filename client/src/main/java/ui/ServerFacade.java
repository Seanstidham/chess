package ui;
import com.google.gson.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
public class ServerFacade {
    //okay first the imports
    //for list, join, and join observ imma need a way to store the stuff and the only way ik how to do that is with Hashmaps
    private Map<Integer, Integer> farumAzula; //sorry been playing a lot of Elden Ring lately
    private static String BASE_URL = "http://localhost:";
    private int serverPort;
    public ServerFacade(int serverPort) {
        this.farumAzula = new HashMap<>();
        this.serverPort = serverPort;
    }
    //okay i wanna try something lemme cook on this
    //my idea is to separate the HTTP connection is 2 different methods and have each one call them to handle the setup and the execution of it
    //let me cook this might be good or it might be bad
    private HttpURLConnection prepareConnection(String endpoint, String method, boolean doOutput, String contentType) throws IOException {
        URL url = new URL(BASE_URL + serverPort + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(doOutput);
        conn.setRequestProperty("Content-type", contentType);
        return conn;
    }
    private String executeRequest(HttpURLConnection conn, JsonObject requestData) {
        try {
            if (conn == null)
                return null;

            if (requestData != null) {
                String jsonData = new Gson().toJson(requestData);
                conn.getOutputStream().write(jsonData.getBytes());
            }
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return conn.getHeaderField("Authorization");
            } else {
                String errorMessage;
                try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream())) {
                    JsonObject errorResponse = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
                    errorMessage = errorResponse.get("message").getAsString();
                }
                System.out.println(errorMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //okay so now i need to see if I can set up all of these functions
    //login
    public String login(String username, String password) {
        try {
            HttpURLConnection conn = prepareConnection("/session", "POST", true, "application/json");

            JsonObject loginData = new JsonObject();
            loginData.addProperty("username", username);
            loginData.addProperty("password", password);

            String authToken = executeRequest(conn, loginData);
            if (authToken != null) {
                System.out.println(EscapeSequences.ERASE_SCREEN);
                System.out.println("Login successful");
                return authToken;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //register
    public String register(String username, String password, String email) {
        try {
            HttpURLConnection conn = prepareConnection("/user", "POST", true, "application/json");

            JsonObject registrationData = new JsonObject();
            registrationData.addProperty("username", username);
            registrationData.addProperty("password", password);
            registrationData.addProperty("email", email);

            String authToken = executeRequest(conn, registrationData);
            if (authToken != null) {
                System.out.println(EscapeSequences.ERASE_SCREEN);
                System.out.println("Registration successful!");
                return authToken;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //logout
    public boolean logout(String authToken) {
        try {
            HttpURLConnection conn = prepareConnection("/session", "DELETE", false, null);
            conn.setRequestProperty("Authorization", authToken);

            String response = executeRequest(conn, null);
            if (response != null) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
                System.out.println("Logout successful");
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                return true;
            } else {
                System.out.println("Logout failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //create game
    public boolean createGame(String authToken, String gameName) {
        try {
            HttpURLConnection conn = prepareConnection("/game", "POST", true, "application/json");
            conn.setRequestProperty("authorization", authToken);

            JsonObject gameData = new JsonObject();
            gameData.addProperty("gameName", gameName);

            String authToken1 = executeRequest(conn, gameData);
            if (authToken1 != null) {
                System.out.println("Game created successfully!");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //list games
    public void listGames(String authToken) {
        try {
            HttpURLConnection conn = prepareConnection("/game", "GET", false, null);
            conn.setRequestProperty("authorization", authToken);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JsonObject responseObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonArray gamesArray = responseObject.getAsJsonArray("games");
                if (gamesArray != null) {
                    int gameNum = 1;
                    farumAzula.clear();
                    for (JsonElement gameElement : gamesArray) {
                        JsonObject gameObject = gameElement.getAsJsonObject();
                        int gameID = gameObject.get("gameID").getAsInt();
                        String gameName = gameObject.get("gameName").getAsString();
                        String whiteUsername = gameObject.has("whiteUsername") ? gameObject.get("whiteUsername").getAsString() : null;
                        String blackUsername = gameObject.has("blackUsername") ? gameObject.get("blackUsername").getAsString() : null;
                        farumAzula.put(gameNum, gameID);
                        System.out.print("Game #: " + gameNum + ", Game Name: " + gameName);
                        System.out.print(", Players: ");
                        if (whiteUsername == null) {
                            System.out.print("White is Empty, ");
                        } else {
                            System.out.print(whiteUsername + ", ");
                        }
                        if (blackUsername == null) {
                            System.out.println("Black is Empty");
                        } else {
                            System.out.println(blackUsername);
                        }
                        gameNum++;
                    }
                } else {
                    System.out.println("No games found.");
                }
            } else {
                String error = conn.getResponseMessage();
                System.out.println("Failed to function with the list: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //join game
    public boolean joinGame(int gameID, String whiteOrBlack, String authToken) {
        try {
            HttpURLConnection conn = prepareConnection("/game", "PUT", true, "application/json");

            JsonObject joinGameData = new JsonObject();
            joinGameData.addProperty("playerColor", whiteOrBlack.toUpperCase());
            int realID = -1;
            if (farumAzula.containsKey(gameID)) {
                realID = farumAzula.get(gameID);
            }
            joinGameData.addProperty("gameID", realID);

            String authTokenResponse = executeRequest(conn, joinGameData);
            if (authTokenResponse != null) {
                System.out.println("Joined game successfully!");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //join as observer
    public boolean joinObserver(int gameID, String authToken) {
        try {
            HttpURLConnection conn = prepareConnection("/game", "PUT", true, "application/json");
            conn.setRequestProperty("Authorization", authToken);

            JsonObject joinObserverData = new JsonObject();
            joinObserverData.addProperty("playerColor", "");
            int realID = -1;
            if (farumAzula.containsKey(gameID)) {
                realID = farumAzula.get(gameID);
            }
            joinObserverData.addProperty("gameID", realID);

            String authTokenResponse = executeRequest(conn, joinObserverData);
            if (authTokenResponse != null) {
                System.out.println("Joined game as observer successfully!");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //alright this is either going to be a dumpster fire or its gonna work
}
