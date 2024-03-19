package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
public class ServerFacadeTests {
    //Write positive and negative unit tests for each method on your ServerFacade class (all the methods used to call your server).
    static ServerFacade facade;
    private static Server server;
    static int serverPort;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        serverPort = port;
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    //Make sure you clear your database between each test. You can do this in a method that has the @BeforeEach annotation.
    @BeforeEach
    public void clearDatabase() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + serverPort + "/db"))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            if (statusCode == 200) {
                System.out.println("Database cleared");
            } else {
                String errorMessage = response.body();
                System.out.println("Failed: " + errorMessage);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void negativeloginTest() {
        //login without registering first
        String badendingauthToken = facade.login("shouldntwork", "shouldntwork");
        Assertions.assertNull(badendingauthToken);
    }

    @Test
    public void positiveloginTest() {
        String goodendingauthToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(goodendingauthToken);
        boolean logoutWorks = facade.logout(goodendingauthToken);
        Assertions.assertTrue(logoutWorks);
        goodendingauthToken = facade.login("validUsername", "validPassword");
        Assertions.assertNotNull(goodendingauthToken);
    }
    @Test
    public void negativeregisterTest() {
        String badendingauthToken = facade.register("newUser", "newPassword", "newEmail");
        Assertions.assertNotNull(badendingauthToken); //you want it to try to register the same info
        badendingauthToken = facade.register("newUser", "newPassword", "newEmail");
        Assertions.assertNull(badendingauthToken);
    }
    @Test
    public void positiveregisterTest() {
        String goodendingauthToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(goodendingauthToken);
    }
    @Test
    public void negativelogoutTest() {
        boolean badendingLogout = facade.logout("invalidAuthToken");
        Assertions.assertFalse(badendingLogout);
    }
    @Test
    public void positivelogoutTest() {
        String goodendingauthToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(goodendingauthToken);
        boolean logoutWorks = facade.logout(goodendingauthToken);
        Assertions.assertTrue(logoutWorks);
    }
    @Test
    public void negativecreategameTest() {
        boolean badendingcreateGame = facade.createGame("invalidAuthToken", "BadEnding");
        Assertions.assertFalse(badendingcreateGame, "Without ID it should game end itself");
    }
    @Test
    public void positivecreategameTest() {
        String goodendingauthToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(goodendingauthToken);
        boolean goodendingcreateGame = facade.createGame(goodendingauthToken, "GoodEnding");
        Assertions.assertTrue(goodendingcreateGame);
    }
    @Test
    public void negativelistgamesTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        facade.listGames("invalidAuthToken");
        System.setOut(System.out);
        String magicConch = outputStream.toString();
        Assertions.assertTrue(magicConch.contains("Failed"));
    }
    @Test
    public void positivelistgamestest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);
        facade.createGame(authToken, "EldenRing");
        facade.listGames(authToken);
        Assertions.assertDoesNotThrow(() -> facade.listGames(authToken));
    }
    @Test
    public void negativejoingameTest() {
        String badendingauthToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(badendingauthToken);
        boolean joingameWorks = facade.joinGame(1000, "white", "invalidAuthToken");
        Assertions.assertFalse(joingameWorks);
    }
    @Test
    public void positivejoingameTest() {
        String goodendingauthToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(goodendingauthToken);
        boolean creategameWorks = facade.createGame(goodendingauthToken, "FrenziedFlame");
        Assertions.assertTrue(creategameWorks);
        facade.listGames(goodendingauthToken);
        boolean joingameWorks = facade.joinGame(1, "WHITE", goodendingauthToken);
        Assertions.assertTrue(joingameWorks);
    }
    @Test
    public void negativejoinobserverTest() {
        String badendingauthToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(badendingauthToken);
        boolean joingameWorks = facade.joinObserver(1000, "invalidAuthToken");
        Assertions.assertFalse(joingameWorks);
    }
    @Test
    public void positivejoinobserverTest() {
        String goodendingauthToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(goodendingauthToken);
        boolean creategameWorks = facade.createGame(goodendingauthToken, "AgeofStars");
        Assertions.assertTrue(creategameWorks);
        facade.listGames(goodendingauthToken);
        boolean joingameworks = facade.joinObserver(1, goodendingauthToken);
        Assertions.assertTrue(joingameworks);
    }

}
