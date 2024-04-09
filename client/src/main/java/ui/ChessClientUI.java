package ui;
import java.util.Scanner;
import chess.ChessGame;
import com.google.gson.Gson;


public class ChessClientUI {
    //okay so imma setup prelogin first
    private static Scanner scanner = new Scanner(System.in);
    private static String LOGGED_IN_MESSAGE = "[LOGGED-IN] >>> ";
    private static String LOGGED_OUT_MESSAGE = "[LOGGED-OUT] >>> ";
    private static boolean isloggedIn = false;
    private static ServerFacade facade = new ServerFacade(8080);
    //okay now I should have everything
    private static  Gson gson = new Gson();
    private static ChessGame.TeamColor teamColor;
    private static GameUI gameUI = null;

    public ChessClientUI() {
        preloginUI();
    }
    private static void preloginUI() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        System.out.println("\nWelcome to the Chess Game");
        while (!isloggedIn) {
            System.out.println("\nOptions:");
            System.out.println("1. Help");
            System.out.println("2. Quit");
            System.out.println("3. Login");
            System.out.println("4. Register");
            System.out.print(LOGGED_OUT_MESSAGE);
            String userInput = scanner.nextLine().trim().toLowerCase();
            if (userInput.equals("1") || userInput.equals("help")) {
                helpDisplay();
            } else if (userInput.equals("2") || userInput.equals("quit")) {
                nukeProgram();
            } else if (userInput.equals("3") || userInput.equals("login")) {
                login();
            } else if (userInput.equals("4") || userInput.equals("register")) {
                register();
            } else {
                System.out.println("Invalid input, Please try again.");
            }
        }
    }
    //okay now I gotta write classes that perform all those functions
    private static void helpDisplay() {
        System.out.println("Help - All help commands");
        System.out.println("Quit - Exit Program");
        System.out.println("Login - Sign in to play");
        System.out.println("Register - Create an account");
    }
    private static void nukeProgram() {
        System.out.println("Thank you so much for playing my game");
        System.exit(0);
    }
    private static void login() {
        System.out.println("\nPlease enter your login information:");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        //i need to eventually build the facade in the other file, but for now I'll just string some spaghetti code together
        String logingoodEnding = facade.login(username, password);
        if(logingoodEnding != null) {
            System.out.println("\nLogged in as: " + EscapeSequences.SET_TEXT_COLOR_GREEN+ username);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            isloggedIn = true;
            postloginUI(logingoodEnding);
        }
    }
    private static void register() {
        System.out.println("\nCreate an account!:");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        ///more spaghetti code, ill just keep doing this and cover everything in the pregame md
        String registergoodEnding = facade.register(username, password, email);
        if(registergoodEnding!=null) {
            System.out.println("\nLogged in as: " + EscapeSequences.SET_TEXT_COLOR_GREEN+ username);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            isloggedIn = true;
            postloginUI(registergoodEnding);
        }
    }
    //okay now the Post login UI
    private static void postloginUI(String authToken) {
        System.out.println("\nLogged in Options:");
        System.out.println("1. Help");
        System.out.println("2. Logout");
        System.out.println("3. Create Game");
        System.out.println("4. List Games");
        System.out.println("5. Join Game");
        System.out.println("6. Join Observer");
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            System.out.print(LOGGED_IN_MESSAGE);
            String userInput = scanner.nextLine().toLowerCase();
            if (userInput.equals("1") || userInput.equals("help")) {
                helpDisplay1();
            } else if (userInput.equals("2") || userInput.equals("logout")) {
                logout(authToken);
                loggedIn = false;
            } else if (userInput.equals("3") || userInput.equals("create game")) {
                createGame(authToken);
            } else if (userInput.equals("4") || userInput.equals("list games")) {
                listGames(authToken);
            } else if (userInput.equals("5") || userInput.equals("join game")) {
                joinGame(authToken);
            } else if (userInput.equals("6") || userInput.equals("join observer")) {
                joinObserver(authToken);
            } else {
                System.out.println("Invalid input, enter a valid input.");
            }
        }
    }
    //okay now i need these functions
    //helpdisplay1
    private static void helpDisplay1() {
        System.out.println("1. Help - All Help Commands"); //placeholder until i write all the commands
        System.out.println("2. Logout - Logout and return to main menu");
        System.out.println("3. Create Game - All Commands");
        System.out.println("4. List Games - All Commands"); //please don't do that
        System.out.println("5. Join Game - All Commands");
        System.out.println("6. Join Observer - All Commands");
    }
    //logout
    private static void logout(String authToken) {
        boolean lefttheGame = facade.logout(authToken);
        if (lefttheGame) {
            isloggedIn = false;
            preloginUI();
        }
    }
    //creategame
    private static void createGame(String authToken) {
//        Allows the user to input a name for the new game. Calls the server create API to create the game. This does not join the player to the created game; it only creates the new game in the server.
        System.out.println("\nEnter name for new game:");
        String thenameoftheGame = scanner.nextLine();
        boolean gamecreationWorked = facade.createGame(authToken, thenameoftheGame);
        if (gamecreationWorked) {
            postloginUI(authToken);
        }
    }
    //listgame
    private static void listGames(String authToken) {
        System.out.println("List of all the Games: \n");
        facade.listGames(authToken);
    }
    //joingame
    private static void joinGame(String authToken) {
        System.out.println("Enter the number of the game you want to join: ");
        int gameDigits = Integer.parseInt(scanner.nextLine());
        System.out.println("Which color do you want to play as?: ");
        String playerColor = scanner.nextLine();

        if (playerColor.equalsIgnoreCase("white")) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        if (facade.joinGame(gameDigits, playerColor, authToken)) {
            gameUI = new GameUI(teamColor, authToken, facade.farumAzula.get(gameDigits));
            gameUI.run();
        }
    }
    //joinobserver
    private static void joinObserver(String authToken) {
        System.out.println("Enter the number of the game you want to join: ");
        int gameDigits = Integer.parseInt(scanner.nextLine());
        if (facade.joinObserver(gameDigits, authToken)) {
            gameUI = new GameUI(null, authToken, facade.farumAzula.get(gameDigits));
            gameUI.run();
        }
    }
}
