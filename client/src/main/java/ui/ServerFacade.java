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
    private static String BASE_URL = "http://localhost:8080";
    public ServerFacade() {
        this.farumAzula = new HashMap<>();
    }
    //okay i wanna try something lemme cook on this
    //my idea is to separate the HTTP connection is 2 different methods and have each one call them to handle the setup and the execution of it
    //let me cook this might be good or it might be bad
    private HttpURLConnection prepareConnection(String endpoint, String method, boolean doOutput, String contentType) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(doOutput);
        conn.setRequestProperty("Content-type", contentType);
        return conn;
    }
    private String executeRequest(HttpURLConnection conn, JsonObject requestData) throws IOException {
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
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
            JsonObject errorResponse = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
            String errorMessage = errorResponse.get("message").getAsString();
            System.out.println(errorMessage);
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
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
                System.out.println("Logout successful");
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                return true;
            } else {
                String errorMessage = conn.getResponseMessage();
                System.out.println(errorMessage);
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
                    int gameNumber = 1;
                    farumAzula.clear();
                    for (JsonElement gameElement : gamesArray) {
                        JsonObject gameObject = gameElement.getAsJsonObject();
                        int gameID = gameObject.get("gameID").getAsInt();
                        String gameName = gameObject.get("gameName").getAsString();
                        String whiteUsername = getStringOrNull(gameObject, "whiteUsername");
                        String blackUsername = getStringOrNull(gameObject, "blackUsername");
                        farumAzula.put(gameNumber, gameID);
                        System.out.print("Game #: " + gameNumber + ", Game Name: " + gameName);
                        System.out.print(", Players: ");
                        if (whiteUsername == null) {
                            System.out.print("White Player is Empty, ");
                        } else {
                            System.out.print(whiteUsername + ", ");
                        }

                        if (blackUsername == null) {
                            System.out.println("Black is Empty");
                        } else {
                            System.out.println(blackUsername);
                        }
                        gameNumber++;
                    }
                } else {
                    System.out.println("No games found.");
                }
            } else {
                String error = conn.getResponseMessage();
                System.out.println("Failed to list games: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String getStringOrNull(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return element != null && !element.isJsonNull() ? element.getAsString() : null;
    }
    //join game
    //join as observer

}
