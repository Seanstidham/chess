package server;

import spark.*;
import dataAccess.*;
import service.*;
import handler.*;


public class Server {
    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        RegisterHandler registerHandler = new RegisterHandler(new RegisterService(userDAO, authDAO));
        Spark.post("/user", registerHandler::register);

        ClearApplicationService clearApplicationService = new ClearApplicationService(authDAO, gameDAO, userDAO);
        ClearApplicationHandler clearApplicationHandler = new ClearApplicationHandler(clearApplicationService);
        Spark.delete("/db", (request, response) -> clearApplicationHandler.clearDatabase(response));

        LoginHandler loginHandler = new LoginHandler(new LoginService(userDAO, authDAO));
        Spark.post("/session", loginHandler::login);

        LogoutHandler logoutHandler = new LogoutHandler(new LogoutService(authDAO));
        Spark.delete("/session", logoutHandler::logout);

        CreateGameHandler createGameHandler = new CreateGameHandler(new CreateGameService(gameDAO, authDAO));
        Spark.post("/game", createGameHandler::createGame);

        ListGamesHandler listGamesHandler = new ListGamesHandler(new ListGamesService(gameDAO, authDAO));
        Spark.get("/game", listGamesHandler::listGames);

        JoinGameHandler joinGameHandler = new JoinGameHandler(new JoinGameService(gameDAO, authDAO));
        Spark.put("/game", joinGameHandler::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
