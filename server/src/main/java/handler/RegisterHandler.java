package handler;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import service.RegisterService;
import request.RegisterRequest;
import result.RegisterResult;
import dataAccess.DataAccessException;

public class RegisterHandler {
//    The server handler classes serve as a translator between HTTP and Java. Your handlers will convert an HTTP request
//    into Java usable objects & data. The handler then calls the appropriate service. When the service responds it converts
//    the response object back to JSON and sends the HTTP response.

    //alright imma take care of the imports
    //so i wanna private the service and the Gson/Json stuff
    private RegisterService registerService;
    private Gson gson;

    // okay i can kinda set this up like the service a bit

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
        this.gson = new Gson();
    }

    public String register(Request request, Response response) {
        response.type("application/json");

        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
        // so now it needs a case to give the error message of it being blank
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            response.status(400);
            return gson.toJson(new RegisterResult(null, null, "Error: bad request"));
        }

        //so now i need a way to manage the success and 2 other response cases
        //mr google is telling me that the best way to do this is with a try statement
        //looks like its just a more complex if statement, gonna model it off my Register Service and see if  i can get the hang of it
        try {
            RegisterResult registerResult = registerService.register(registerRequest);
            if (registerResult.message() != null) {
                response.status(403);
            } else {
                response.status(200);
                if (registerResult.authToken() != null) {
                    response.header("authorization", registerResult.authToken());
                }
            }
            return gson.toJson(registerResult);
        }
        catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new RegisterResult(null, null, "Error: " + e.getMessage()));

        }
        //okay according to chatGPT and Mr. Google that should be good
    }

}
