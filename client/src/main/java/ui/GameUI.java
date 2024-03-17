package ui;
import chess.ChessGame;
import chess.ChessPosition;
import chess.ChessPiece;
import chess.ChessBoard;

public class GameUI {
//    For now, when a user joins or observes a game, the client should draw the initial state of a Chess game in the terminal, but not actually enter gameplay mode.
//    The chessboard should be drawn twice, once in each orientation
    //okay so imma need a function for black and a function for white
    //then imma need a function for the different piece types and their letters
    //but i also need a run function for my Client to work
    private static ChessBoard boardUI = new ChessBoard();

    public static void run() {
        boardUI.resetBoard();
        chessboardBlack();
        System.out.println();
        chessboardWhite();
    }

    private static void chessboardBlack() {
        System.out.println("  h  g  f  e  d  c  b  a ");
        //now i need to do the board iteration
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + "");
            for (int j = 8; j >= 1; j--) {
                ChessPiece thechosenPiece = boardUI.getPiece(new ChessPosition(i,j));
                String textColor;
                if (thechosenPiece == null) {
                    textColor = "";
                } else if (thechosenPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    textColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
                } else {
                    textColor = EscapeSequences.SET_TEXT_COLOR_RED;
                }
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + textColor + pieceType(thechosenPiece));
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + textColor + pieceType(thechosenPiece));
                }
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
    private static void chessboardWhite() {
        System.out.println("  a  b  c  d  e  f  g  h ");
        for (int i = 8; i >= 1; i--) {
            System.out.print(i + " ");
            for (int j = 1; j <= 8; j++) {
                ChessPiece thechosenPiece = boardUI.getPiece(new ChessPosition(i, j));
                String textColor;
                if (thechosenPiece == null) {
                    textColor = "";
                } else if (thechosenPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    textColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
                } else {
                    textColor = EscapeSequences.SET_TEXT_COLOR_RED;
                }
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + textColor + pieceType(thechosenPiece));
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + textColor + pieceType(thechosenPiece));
                }
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        System.out.println("  a  b  c  d  e  f  g  h ");
    }
}
