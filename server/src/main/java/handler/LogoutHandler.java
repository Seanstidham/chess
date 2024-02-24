package handler;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import result.LogoutResult;
import service.LogoutService;
import spark.Request;
import spark.Response;
public class LogoutHandler {
    private LogoutService logoutService;
    private Gson gson;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
        this.gson = new Gson();
    }

    public String logout(Request request, Response response) throws DataAccessException {
        response.type("application/json");

        String authToken = request.headers("authorization");

        LogoutResult logoutResult = logoutService.logout(authToken);
        int statusCode = logoutResult.message() != null ? 401 : 200;
        response.status(statusCode);
        return gson.toJson(logoutResult);
    }
}

