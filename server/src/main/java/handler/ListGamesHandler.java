package handler;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import service.ListGamesService;
import result.ListGamesResult;
import dataAccess.DataAccessException;
public class ListGamesHandler {
    private ListGamesService listGamesService;
    private Gson gson;

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
        this.gson = new Gson();
    }

    public String listGames(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");

        try {
            ListGamesResult gamesListResult = listGamesService.listGames(authToken);
            int statusCode = gamesListResult.message() != null ? 401 : 200;
            response.status(statusCode);
            return gson.toJson(gamesListResult);
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ListGamesResult(null, "Error: " + e.getMessage()));
        }
    }
}
