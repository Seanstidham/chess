package handler;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import service.JoinGameService;
import request.JoinGameRequest;
import result.JoinGameResult;
import dataAccess.DataAccessException;

public class JoinGameHandler {
    private JoinGameService joinGameService;
    private Gson gson;

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
        this.gson = new Gson();
    }

    public String joinGame(Request request, Response response) {
        response.type("application/json");

        JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
        String authToken = request.headers("authorization");

        if (joinGameRequest == null || joinGameRequest.gameID() == null) {
            response.status(400);
            return gson.toJson(new JoinGameResult("Error: bad request"));
        }

        try {
            JoinGameResult joinGameResult = joinGameService.joinGame(authToken, joinGameRequest);

            if (joinGameResult.message() != null) {
                String message = joinGameResult.message();
                if (message.contains("unauthorized")) {
                    response.status(401);
                } else if (message.contains("already taken")) {
                    response.status(403);
                }
            }

            return gson.toJson(joinGameResult);
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new JoinGameResult("Error: " + e.getMessage()));
        }
    }

}
