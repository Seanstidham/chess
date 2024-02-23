package handler;
import com.google.gson.Gson;
import spark.Response;
import service.ClearApplicationService;
import result.ClearApplicationResult;
import dataAccess.DataAccessException;

public class ClearApplicationHandler {
    //seems its a pretty similar method to register handler
    //aight time to cook (jesse)
    private ClearApplicationService clearApplicationService;
    private Gson gson;

    public ClearApplicationHandler(ClearApplicationService clearApplicationService) {
        this.clearApplicationService = clearApplicationService;
        this.gson = new Gson();
    }

    public String clearDatabase(Response response) {
        response.type("application/json");

        //gonna be a pro at these try methods when i'm done here
        try {
            //success
            clearApplicationService.clearApplication();
            response.status(200);
            return "{\"message\": \"Success\"}";
            //time for the other
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ClearApplicationResult("Error: " + e.getMessage()));
        }
    }
    //okay handler done, time to throw it into the server file
}
