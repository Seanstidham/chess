package ui;
import java.util.Scanner;

public class ChessClientUI {
    //okay so imma setup prelogin first
    private static Scanner scanner = new Scanner(System.in);
    private static String LOGGED_IN_MESSAGE = "[LOGGED-IN] >>> ";
    private static String LOGGED_OUT_MESSAGE = "[LOGGED-OUT] >>> ";
    private static boolean isloggedIn = false;
    private static ServerFacade facade = new ServerFacade();
    //okay now I should have everything

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

}
