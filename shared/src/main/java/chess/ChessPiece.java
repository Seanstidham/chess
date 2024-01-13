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

    private ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return PieceType.BISHOP;
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
                ChessPiece BlankSpaceCheck = board.getPiece(newPosition);
                if (BlankSpaceCheck == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    break;
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


