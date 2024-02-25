package handler;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import service.LoginService;
import request.LoginRequest;
import result.LoginResult;
import dataAccess.DataAccessException;

public class LoginHandler {
    //gonna go kinda fast with these, i wanna get login and logout completed in this session
    private  LoginService loginService;
    private  Gson gson;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
        this.gson = new Gson();
    }

    public String login(Request request, Response response) {
        response.type("application/json");

        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

        try {
            LoginResult loginResult = loginService.login(loginRequest);
            int statusCode = loginResult.message() != null ? 401 : 200;
            response.status(statusCode);
            return gson.toJson(loginResult);
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new LoginResult(null, null, "Error: " + e.getMessage()));
        }
    }

}
