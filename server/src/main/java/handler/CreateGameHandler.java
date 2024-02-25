package handler;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import service.CreateGameService;
import request.CreateGameRequest;
import result.CreateGameResult;
import dataAccess.DataAccessException;

public class CreateGameHandler {
    private CreateGameService createGameService;
    private Gson gson;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
        this.gson = new Gson();
    }

    public String createGame(Request request, Response response) {
        response.type("application/json");

        try {
            CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
            String authToken = request.headers("authorization");

            if (createGameRequest == null || createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
                response.status(400);
                return gson.toJson(new CreateGameResult(null, "Error: bad request"));
            }

            if (authToken == null || authToken.isEmpty()) {
                response.status(401);
                return gson.toJson(new CreateGameResult(null, "Error: unauthorized"));
            }

            CreateGameResult createGameResult = createGameService.createGame(authToken, createGameRequest);
            response.status(createGameResult.message() != null ? 401 : 200);

            return gson.toJson(createGameResult);
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new CreateGameResult(null, "Error: " + e.getMessage()));
        }
    }
}
