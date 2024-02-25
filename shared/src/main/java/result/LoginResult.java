package result;

//same thing as the github specs, just need to add the fail message
public record LoginResult(String username, String authToken, String message) { }
