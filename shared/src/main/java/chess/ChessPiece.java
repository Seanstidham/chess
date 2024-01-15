package chess;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        /* Next part, making it move
       need to define the bishops movement
      1 define my posistion
      get all possible directions based on the bishops movement
      return all possible moves
         */
        //maybe try setting it up as the only way to go for now
        //slack discussed HashSets, might be better for the valid moves
        Collection<ChessMove> validMoves = new LinkedHashSet<>();

        //okay the tricky one being the pawn
        if (getPieceType() == PieceType.PAWN){
            //first thing is i gotta define the directions based off the color
            int goForward = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
            //okay now i can write out directions
            int[][] directions = {{goForward, 0}};

            for (int[] direction : directions) {
                int deltaRow = direction[0];
                int deltaCol = direction[1];
                int newRow = myPosition.getRow() + deltaRow;
                int newCol = myPosition.getColumn() + deltaCol;


                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece SpaceCheck = board.getPiece(newPosition);
                    ChessPosition targetCheckRight = new ChessPosition(newRow, newCol + 1);
                    ChessPiece Target1 = board.getPiece(targetCheckRight);
                    ChessPosition targetCheckLeft = new ChessPosition(newRow, newCol - 1);
                    ChessPiece Target2 = board.getPiece(targetCheckLeft);
                    if (SpaceCheck == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else if (Target1 != null && Target1.getTeamColor() != getTeamColor()) {

                            validMoves.add(new ChessMove(myPosition, targetCheckRight, null));

                    } else if (Target2 != null && Target2.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft, null));
                        }
                    }
                }
            }


        //if it ain't broke
        if (getPieceType() == PieceType.KNIGHT) {

            int[][] directions = {{-2,-1}, {-1,-2}, {1,-2}, {2,-1}, {-2,1}, {-1,2}, {1,2}, {2,1}};

            for (int[] direction : directions) {
                int deltaRow = direction[0];
                int deltaCol = direction[1];
                int newRow = myPosition.getRow() + deltaRow;
                int newCol = myPosition.getColumn()+ deltaCol;

                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece SpaceCheck = board.getPiece(newPosition);
                    if (SpaceCheck == null || SpaceCheck.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }


        // okay now that Bishop works, going to try something and this is gonna be gross if this works
        // oh no, it DOES work. guess its gonna be a ton of gross if statements
        if (getPieceType() == PieceType.KING) {

            int[][] directions ={{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

            for (int[] direction : directions) {
                int deltaRow = direction[0];
                int deltaCol = direction[1];
                int newRow = myPosition.getRow() + deltaRow;
                int newCol = myPosition.getColumn() + deltaCol;

                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece SpaceCheck = board.getPiece(newPosition);
                    if (SpaceCheck == null || SpaceCheck.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }

        if (getPieceType() == PieceType.BISHOP) {

            int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

            //now i need a way that all the possible moves are read
            // lets try something a bit different and see if that works
            for (int[] direction : directions) {
                int newRow = myPosition.getRow();
                int newCol = myPosition.getColumn();
                int deltaRow = direction[0];
                int deltaCol = direction[1];

                while (true) {
                    newRow += deltaRow;
                    newCol += deltaCol;

                    // gonna try to set up an if statement instead of a whole helper function
                    if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                        break;
                    }
                    // I need to now add the new position so that we can check for blank spots
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece SpaceCheck = board.getPiece(newPosition);
                    if (SpaceCheck == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else {
                        // now to implement capturing and being blocked by same color pieces;
                        if (SpaceCheck.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                        break;
                    }
                }

            }

        }
        return validMoves;
    }
}






//        for (int[] direction : directions) {
//            int deltaRow = direction[0];
//            int deltaCol = direction[1];
//
//            for (int i = 1; i <= 7; i++) {
//                int newRow = row + i * deltaRow;
//                int newCol = col + i * deltaCol;
//
//                if (isValPos(newRow, newCol)) {
//                    ChessPiece targetPiece = board.getPiece(new ChessPosition(newRow + 1, newCol + 1));
//                    if (targetPiece == null) {
//                        validMoves.add(new ChessMove(myPosition, new ChessPosition(newRow + 1, newCol + 1), getPieceType()));
//                    }
//
//                }
//            }
//
//            // i reckon that should work for the bishop
//        }
//        return validMoves;
//    }


