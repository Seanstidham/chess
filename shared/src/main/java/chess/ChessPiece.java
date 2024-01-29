package chess;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    //implementing the hasMoved stuff from ChessMove
    private boolean hasMoved;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = false;

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

    //Implementing the has moved ones now
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved() {
        hasMoved = true;
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

        //alright queen time, then ill mess with Pawn promotion again
        if (getPieceType() == PieceType.QUEEN) {

            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

            for (int[] direction : directions) {
                int deltaRow = direction[0];
                int deltaCol = direction[1];
                int newRow = myPosition.getRow();
                int newCol = myPosition.getColumn();

                while (true) {
                    newRow += deltaRow;
                    newCol += deltaCol;


                    if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                        break;
                    }
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece SpaceCheck = board.getPiece(newPosition);
                    if (SpaceCheck == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else {
                        if (SpaceCheck.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                        break;
                    }
                }
            }


        }

        //alright rook first then we'll do the queen
        if (getPieceType() == PieceType.ROOK) {

            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direction : directions) {
                int deltaRow = direction[0];
                int deltaCol = direction[1];
                int newRow = myPosition.getRow();
                int newCol = myPosition.getColumn();

                while (true) {
                    newRow += deltaRow;
                    newCol += deltaCol;


                    if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                        break;
                    }
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece SpaceCheck = board.getPiece(newPosition);
                    if (SpaceCheck == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else {
                        if (SpaceCheck.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                        break;
                    }


                }
            }
        }

        //okay the tricky one being the pawn
        if (getPieceType() == PieceType.PAWN) {
            //first thing is i gotta define the directions based off the color
            int goForward = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;

            int[][] directions;
            //okay now i can write out directions
            //maybe an if statement to figure out the intial 2 move
            if (getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) {
                directions = new int[][]{{goForward, 0}, {goForward + 1, 0}};
            } else if (getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) {
                directions = new int[][]{{goForward, 0}, {goForward - 1, 0}};
            } else {
                directions = new int[][]{{goForward, 0}};
            }


            for (int[] direction : directions) {

                int deltaRow = direction[0];
                int deltaCol = direction[1];
                int newRow = myPosition.getRow() + deltaRow;
                int newCol = myPosition.getColumn() + deltaCol;

                //implementing promotion
                // will come back to it, it hard af or im stupid af
//                if (newRow == 8 || newRow == 1) {
//
//                }

                if (newRow > 1 && newRow < 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece SpaceCheck = board.getPiece(newPosition);
                    ChessPosition targetCheckRight = new ChessPosition(newRow, newCol + 1);
                    ChessPiece Target1 = board.getPiece(targetCheckRight);
                    ChessPosition targetCheckLeft = new ChessPosition(newRow, newCol - 1);
                    ChessPiece Target2 = board.getPiece(targetCheckLeft);
                    if (SpaceCheck == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else {
                        if (Target1 != null && Target1.getTeamColor() != getTeamColor()) {

                            validMoves.add(new ChessMove(myPosition, targetCheckRight, null));
                        }
                        if (Target2 != null && Target2.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft, null));
                        }
                        break;

                    }
                    if (Target1 != null && Target1.getTeamColor() != getTeamColor()) {

                        validMoves.add(new ChessMove(myPosition, targetCheckRight, null));

                    }
                    if (Target2 != null && Target2.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, targetCheckLeft, null));
                    }
                }
                //let me cook
                if (newRow == 1 || newRow == 8){
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece SpaceCheck = board.getPiece(newPosition);
                    ChessPosition targetCheckRight = new ChessPosition(newRow, newCol + 1);
                    ChessPiece Target1 = board.getPiece(targetCheckRight);
                    ChessPosition targetCheckLeft = new ChessPosition(newRow, newCol - 1);
                    ChessPiece Target2 = board.getPiece(targetCheckLeft);
                    if (SpaceCheck == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition,PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, newPosition,PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, newPosition,PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, newPosition,PieceType.BISHOP));
                        //lets see if this works
                    } else {
                        if (Target1 != null && Target1.getTeamColor() != getTeamColor()) {

                            validMoves.add(new ChessMove(myPosition, targetCheckRight,PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, targetCheckRight,PieceType.KNIGHT));
                            validMoves.add(new ChessMove(myPosition, targetCheckRight,PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, targetCheckRight,PieceType.BISHOP));
                        }
                        if (Target2 != null && Target2.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft,PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft,PieceType.KNIGHT));
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft,PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft,PieceType.BISHOP));
                        }
                        break;

                    }
                    if (Target1 != null && Target1.getTeamColor() != getTeamColor()) {

                        validMoves.add(new ChessMove(myPosition,targetCheckRight,PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, targetCheckRight,PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, targetCheckRight,PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, targetCheckRight,PieceType.BISHOP));

                    }
                    if (Target2 != null && Target2.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, targetCheckLeft,PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, targetCheckLeft,PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, targetCheckLeft,PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, targetCheckLeft,PieceType.BISHOP));
                        //alright lets see if this horrendous thing works
                    }
                }
            }

        }


        //if it ain't broke
        if (getPieceType() == PieceType.KNIGHT) {

            int[][] directions = {{-2, -1}, {-1, -2}, {1, -2}, {2, -1}, {-2, 1}, {-1, 2}, {1, 2}, {2, 1}};

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


        // okay now that Bishop works, going to try something and this is gonna be gross if this works
        // oh no, it DOES work. guess its gonna be a ton of gross if statements
        if (getPieceType() == PieceType.KING) {

            int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        ChessPiece that = (ChessPiece) o;
//        return pieceColor == that.pieceColor && type == that.type;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    //    @Override
//    public int hashCode() {
//        return Objects.hash(pieceColor, type);
//    }



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


