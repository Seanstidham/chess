package ui;
import chess.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;

public class GameUI implements GameHandler{
// Okay time to scrap everything and start implementing GameHandler
    static ChessGame game;
    static ChessBoard mancalaBoard;
    static Scanner scanner = new Scanner(System.in);
    static String BASE_URL = "http://localhost:8080";
    static String GAME_MESSAGE = "[JOINED-GAME] >>>";
    static Gson gson = new Gson();
    ChessGame.TeamColor teamColor;
    int gameID;
    static String authToken;
    WebSocketFacade socket = new WebSocketFacade(BASE_URL, GameUI.this);

    public GameUI(ChessGame.TeamColor teamColor, String authToken, int gameID) {
        this.teamColor = teamColor;
        GameUI.authToken = authToken;
        this.gameID = gameID;
    }

    public void run() {
       boolean isyourrefridgeratorrunning = true;
       printinitOptions();
       if (teamColor == null) {
           socket.joinObserver(gameID, authToken);
       } else {
           socket.joinPlayer(authToken, gameID, teamColor);
       }
       while (isyourrefridgeratorrunning) {
           System.out.print(GAME_MESSAGE);
           String playerInput = scanner.nextLine().toLowerCase();
           if ("1".equals(playerInput) || "help".equals(playerInput)) {
               displayHelpText();
           } else if ("2".equals(playerInput) || "redraw".equals(playerInput)) {
               reDrawBoard();
           } else if ("3".equals(playerInput) || "leave".equals(playerInput)) {
               leave();
               isyourrefridgeratorrunning = false;
           } else if ("4".equals(playerInput) || "move".equals(playerInput)) {
               makeMove();
           } else if ("5".equals(playerInput) || "resign".equals(playerInput)) {
               resign();
           } else if ("6".equals(playerInput) || "legal moves".equals(playerInput)) {
               displayLegalMoves();
           }
       }
    }
    private void printinitOptions() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        System.out.println("\nOptions:");
        System.out.println("1. Help");
        System.out.println("2. Redraw Chess Board");
        System.out.println("3. Leave");
        System.out.println("4. Make Move");
        System.out.println("5. Resign");
        System.out.println("6. Highlight Legal Moves");
    }
    private static void displayHelpText() {
        System.out.println("Help - Possible commands");
        System.out.println("Redraw - Reprint the Chess Board");
        System.out.println("Leave - Exit Game, quit playing chess");
        System.out.println("Move - Make a move if it is your turn");
        System.out.println("Resign - Surrender the game");
        System.out.println("Legal Moves - Enter a piece and show where it can move");
    }

    private void reDrawBoard() {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            chessboardWhite(mancalaBoard);
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            chessboardBlack(mancalaBoard);
        } else {
            chessboardWhite(mancalaBoard);
        }
    }
    private void leave() {
        try {
            socket.leave(gameID, authToken);
            System.out.println("You are alone now.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void makeMove() {
        if (teamColor == null) {
            System.out.println("Cannot make move as observer");
            return;
        }

        if (game.getTeamTurn() == null) {
            System.out.println("Cannot make move, game is over.");
            return;
        }

        System.out.println("Enter Column: ");
        String column = scanner.nextLine().toLowerCase();
        int colVal;
        if ("a".equals(column)) {
            colVal = 1;
        } else if ("b".equals(column)) {
            colVal = 2;
        } else if ("c".equals(column)) {
            colVal = 3;
        } else if ("d".equals(column)) {
            colVal = 4;
        } else if ("e".equals(column)) {
            colVal = 5;
        } else if ("f".equals(column)) {
            colVal = 6;
        } else if ("g".equals(column)) {
            colVal = 7;
        } else if ("h".equals(column)) {
            colVal = 8;
        } else {
            System.out.println("Invalid column input.");
            return;
        }

        System.out.println("Enter Row: ");
        String row = scanner.nextLine().toLowerCase();
        int rowVal = Integer.parseInt(row);

        ChessPiece piece = mancalaBoard.getPiece(new ChessPosition(rowVal, colVal));

        if (piece == null) {
            System.out.println("No piece on that square");
            return;
        }

        if (rowVal < 1 || rowVal > 8) {
            System.out.println("Incorrect input on the board");
            return;
        }

        System.out.println("Enter Column Where You Want To Move: ");
        column = scanner.nextLine().toLowerCase();
        int newColVal; //switch to avoid the gay ass no duplicated code rule
        switch (column) {
            case "a":
                newColVal = 1;
                break;
            case "b":
                newColVal = 2;
                break;
            case "c":
                newColVal = 3;
                break;
            case "d":
                newColVal = 4;
                break;
            case "e":
                newColVal = 5;
                break;
            case "f":
                newColVal = 6;
                break;
            case "g":
                newColVal = 7;
                break;
            case "h":
                newColVal = 8;
                break;
            default:
                System.out.println("Invalid column input.");
                return;
        }

        System.out.println("Enter Row Where You Want To Move: ");
        row = scanner.nextLine().toLowerCase();
        int newRowVal = Integer.parseInt(row);

        if (newRowVal < 1 || newRowVal > 8) {
            System.out.println("Incorrect input on the board");
            return;
        }

        ChessMove newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), null);
        Collection<ChessMove> validMoves = game.validMoves(new ChessPosition(rowVal, colVal));

        if (!validMoves.contains(newMove)) {
            System.out.println("Invalid move.");
            return;
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRowVal == 8) || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRowVal == 1)) {
                System.out.println("Enter what piece you would like to promote to. (q, k, b, r)");
                String newPiece = scanner.nextLine().toLowerCase();
                if ("q".equals(newPiece)) {
                    newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.QUEEN);
                } else if ("k".equals(newPiece)) {
                    newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.KNIGHT);
                } else if ("b".equals(newPiece)) {
                    newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.BISHOP);
                } else if ("r".equals(newPiece)) {
                    newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.ROOK);
                } else {
                    System.out.println("Not Valid.");
                }
            }
        }

        socket.makeMove(gameID, newMove, authToken);
    }
    private void resign() {
        System.out.println("Are you sure you want to resign? (Y/N)");
        String answer = scanner.nextLine().toLowerCase();
        if ("y".equals(answer)) {
            socket.resign(gameID, authToken);
            game.setTeamTurn(null);
            System.out.println("You have resigned.");
        } else if ("n".equals(answer)) {
            System.out.println("Continuing with the game.");
        } else {
            System.out.println("Invalid input. Please enter 'Y' or 'N'.");
        }
    }
    private void displayLegalMoves() {
        System.out.println("Enter Column: ");
        String column = scanner.nextLine().toLowerCase();
        int colnum = avoidingDuplication(column);


        System.out.println("Enter Row: ");
        String row = scanner.nextLine().toLowerCase();
        int rowVal = Integer.parseInt(row);

        if (rowVal < 1 || rowVal > 8) {
            System.out.println("Incorrect input on the board");
            return;
        }

        ChessPosition position = new ChessPosition(rowVal, colnum);
        ChessPiece piece = mancalaBoard.getPiece(position);

        if (piece == null) {
            System.out.println("No piece on the board");
            return;
        }

        Collection<ChessMove> validMoves = game.validMoves(position);

        if (validMoves.isEmpty()) {
            System.out.println("No legal moves available for this piece.");
        } else {
            System.out.println("Legal moves for the selected piece:");
            for (int i = 8; i >= 1; i--) {
                System.out.print(i + " ");
                for (int j = 1; j <= 8; j++) {
                    ChessPosition currentPosition = new ChessPosition(i, j);
                    boolean isLegalMove = false;
                    for (ChessMove move : validMoves) {
                        if (move.getEndPosition().equals(currentPosition)) {
                            isLegalMove = true;
                            break;
                        }
                    }
                    if (isLegalMove) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                    } else if ((i + j) % 2 == 0) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    }
                    ChessPiece currentPiece = mancalaBoard.getPiece(currentPosition);

                    String textColor;
                    if (currentPiece == null) {
                        textColor = "";
                    } else if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        textColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
                    } else {
                        textColor = EscapeSequences.SET_TEXT_COLOR_RED;
                    }
                    System.out.print(textColor + pieceType(currentPiece));
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
        }
    }
    private int avoidingDuplication(String col) {
        int colNum = 0;
        if (col.equals("a")) {
            colNum = 1;
        } else if (col.equals("b")) {
            colNum = 2;
        } else if (col.equals("c")) {
            colNum = 3;
        } else if (col.equals("d")) {
            colNum = 4;
        } else if (col.equals("e")) {
            colNum = 5;
        } else if (col.equals("f")) {
            colNum = 6;
        } else if (col.equals("g")) {
            colNum = 7;
        } else if (col.equals("h")) {
            colNum = 8;
        }
        return colNum;
    }

    private static void chessboardBlack(ChessBoard board) {
        System.out.println("  h  g  f  e  d  c  b  a ");
        for (int rank = 1; rank <= 8; rank++) {
            System.out.print(rank + " ");
            for (int file = 8; file >= 1; file--) {
                ChessPiece piece = board.getPiece(new ChessPosition(rank, file));
                String textColor = (piece != null && piece.getTeamColor() == ChessGame.TeamColor.BLACK) ?
                        EscapeSequences.SET_TEXT_COLOR_BLUE :
                        EscapeSequences.SET_TEXT_COLOR_RED;
                String bgColor = ((rank + file) % 2 == 0) ?
                        EscapeSequences.SET_BG_COLOR_DARK_GREY :
                        EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                System.out.print(bgColor + textColor + pieceType(piece));
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        System.out.println("  h  g  f  e  d  c  b  a ");
    }
    private static String pieceType(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }
        char pieceSymbol = ' ';
        ChessPiece.PieceType pieceType = piece.getPieceType();
        if (pieceType == ChessPiece.PieceType.PAWN) {
            pieceSymbol = 'p';
        } else if (pieceType == ChessPiece.PieceType.ROOK) {
            pieceSymbol = 'r';
        } else if (pieceType == ChessPiece.PieceType.KNIGHT) {
            pieceSymbol = 'n';
        } else if (pieceType == ChessPiece.PieceType.BISHOP) {
            pieceSymbol = 'b';
        } else if (pieceType == ChessPiece.PieceType.QUEEN) {
            pieceSymbol = 'q';
        } else if (pieceType == ChessPiece.PieceType.KING) {
            pieceSymbol = 'k';
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            pieceSymbol = Character.toUpperCase(pieceSymbol);
        }
        return " " + pieceSymbol + " ";
    }
    private static void chessboardWhite(ChessBoard board) {
        System.out.println("  a  b  c  d  e  f  g  h ");
        int j = 8;
        while (j >= 1) {
            System.out.print(j + " ");
            int i = 1;
            while (i <= 8) {
                ChessPiece thechosenPiece = board.getPiece(new ChessPosition(j, i));
                String textColor;
                if (thechosenPiece == null) {
                    textColor = "";
                } else if (thechosenPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    textColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
                } else {
                    textColor = EscapeSequences.SET_TEXT_COLOR_RED;
                }
                if ((j + i) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + textColor + pieceType(thechosenPiece));
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + textColor + pieceType(thechosenPiece));
                }
                i++;
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
            j--;
        }
        System.out.println("  a  b  c  d  e  f  g  h ");
    }
    @Override
    public void updateGame(LoadGameMessage game) {
        Object updatedGame = game.getGame();
        ChessGame chessGame = gson.fromJson(updatedGame.toString(), ChessGame.class);
        System.out.println("RECEIVED GAME MESSAGE");
        this.game = chessGame;
        this.mancalaBoard = chessGame.getBoard();
        if(this.game.isInCheckmate(ChessGame.TeamColor.WHITE) || this.game.isInCheckmate(ChessGame.TeamColor.BLACK)
                || this.game.isInStalemate(ChessGame.TeamColor.WHITE) || this.game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            this.game.setTeamTurn(null);
        }
        if(teamColor == ChessGame.TeamColor.WHITE) {
            chessboardWhite(mancalaBoard);
        } else if (teamColor == ChessGame.TeamColor.BLACK){
            chessboardBlack(mancalaBoard);
        } else {
            chessboardWhite(mancalaBoard);
        }
        System.out.print("[IN-GAME] >>> ");
    }

    @Override
    public void printMessage(NotificationMessage message) {
        System.out.println(message.getNotificationMessage());
        System.out.print("[IN-GAME] >>> ");
    }
}
