package chess;

import java.util.Arrays;
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
                    ChessPiece spacecheckQueen = board.getPiece(newPosition);
                    if (spacecheckQueen == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else {
                        if (spacecheckQueen.getTeamColor() != getTeamColor()) {
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

                do {
                    newRow = newRow + deltaRow;
                    newCol = newCol + deltaCol;


                    if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                        ChessPosition newpositionRook;
                        newpositionRook = new ChessPosition(newRow, newCol);
                        ChessPiece spacecheckRook;
                        spacecheckRook = board.getPiece(newpositionRook);
                        if (spacecheckRook == null) {
                            validMoves.add(new ChessMove(myPosition, newpositionRook, null));
                        } else {
                            if (spacecheckRook.getTeamColor() != getTeamColor()) {
                                validMoves.add(new ChessMove(myPosition, newpositionRook, null));
                            }
                            break;
                        }
                    } else {
                        break;
                    }


                } while (true);
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

                if (newRow > 1 && newRow < 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newpositionPawn = new ChessPosition(newRow, newCol);
                    ChessPiece spacecheckPawn = board.getPiece(newpositionPawn);
                    ChessPosition targetCheckRight = new ChessPosition(newRow, newCol + 1);
                    ChessPiece targetPawn1 = board.getPiece(targetCheckRight);
                    ChessPosition targetCheckLeft = new ChessPosition(newRow, newCol - 1);
                    ChessPiece targetPawn2 = board.getPiece(targetCheckLeft);
                    if (spacecheckPawn == null) {
                        validMoves.add(new ChessMove(myPosition, newpositionPawn, null));
                    } else {
                        if (targetPawn1 != null && targetPawn1.getTeamColor() != getTeamColor()) {

                            validMoves.add(new ChessMove(myPosition, targetCheckRight, null));
                        }
                        if (targetPawn2 != null && targetPawn2.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft, null));
                        }
                        break;

                    }
                    if (targetPawn1 != null && targetPawn1.getTeamColor() != getTeamColor()) {

                        validMoves.add(new ChessMove(myPosition, targetCheckRight, null));

                    }
                    if (targetPawn2 != null && targetPawn2.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, targetCheckLeft, null));
                    }
                }
                //let me cook
                if (newRow == 1 || newRow == 8){
                    ChessPosition newPosition1 = new ChessPosition(newRow, newCol);
                    ChessPiece spacecheckPawn1 = board.getPiece(newPosition1);
                    ChessPosition targetCheckRight1 = new ChessPosition(newRow, newCol + 1);
                    ChessPiece target1 = board.getPiece(targetCheckRight1);
                    ChessPosition targetCheckLeft1 = new ChessPosition(newRow, newCol - 1);
                    ChessPiece target2 = board.getPiece(targetCheckLeft1);
                    if (spacecheckPawn1 == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition1,PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, newPosition1,PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, newPosition1,PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, newPosition1,PieceType.BISHOP));
                        //lets see if this works
                    } else {
                        if (target1 != null && target1.getTeamColor() != getTeamColor()) {

                            validMoves.add(new ChessMove(myPosition, targetCheckRight1,PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, targetCheckRight1,PieceType.KNIGHT));
                            validMoves.add(new ChessMove(myPosition, targetCheckRight1,PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, targetCheckRight1,PieceType.BISHOP));
                        }
                        if (target2 != null && target2.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft1,PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft1,PieceType.KNIGHT));
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft1,PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, targetCheckLeft1,PieceType.BISHOP));
                        }
                        break;

                    }
                    if (target1 == null || target1.getTeamColor() == getTeamColor()) {
                    } else {

                        validMoves.add(new ChessMove(myPosition,targetCheckRight1,PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, targetCheckRight1,PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, targetCheckRight1,PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, targetCheckRight1,PieceType.BISHOP));

                    }
                    if (null == target2 || target2.getTeamColor() == getTeamColor()) {
                        continue;
                    }
                    validMoves.add(new ChessMove(myPosition, targetCheckLeft1,PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, targetCheckLeft1,PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, targetCheckLeft1,PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, targetCheckLeft1,PieceType.BISHOP));
                    //alright lets see if this horrendous thing works
                }
            }
        }
        if (getPieceType() == PieceType.KNIGHT) {
            int[][] directions = {{-2, -1}, {-1, -2}, {1, -2}, {2, -1}, {-2, 1}, {-1, 2}, {1, 2}, {2, 1}};
            Arrays.stream(directions).forEach(direction -> {
                int deltaRow = direction[0];
                int deltaCol = direction[1];
                int newRow = myPosition.getRow() + deltaRow;
                int newCol = myPosition.getColumn() + deltaCol;
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newpositionKnight = new ChessPosition(newRow, newCol);
                    ChessPiece spacecheckKnight = board.getPiece(newpositionKnight);
                    if (spacecheckKnight == null || spacecheckKnight.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newpositionKnight, null));
                    }
                }
            });
        }
        if (getPieceType() == PieceType.KING) {
            int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
            for (int[] direction : directions) {
                int deltaRow = direction[0];
                int deltaCol = direction[1];
                int newRow = myPosition.getRow() + deltaRow;
                int newCol = myPosition.getColumn() + deltaCol;
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newpositionKing = new ChessPosition(newRow, newCol);
                    ChessPiece spacecheckKing = board.getPiece(newpositionKing);
                    if (spacecheckKing == null || spacecheckKing.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newpositionKing, null));
                    }
                }
            }
        }
        if (getPieceType() == PieceType.BISHOP) {
            int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
            for (int[] direction : directions) {
                int newRow = myPosition.getRow();
                int newCol = myPosition.getColumn();
                int deltaRow = direction[0];
                int deltaCol = direction[1];
                do {
                    newRow = newRow + deltaRow;
                    newCol = newCol + deltaCol;
                    if (newRow < 1 || newRow > 8 || newCol > 8 || newCol < 1) {
                        break;
                    }
                    ChessPosition newpositionBishop;
                    newpositionBishop = new ChessPosition(newRow, newCol);
                    ChessPiece spacecheckBishop;
                    spacecheckBishop = board.getPiece(newpositionBishop);
                    if (spacecheckBishop == null) {
                        validMoves.add(new ChessMove(myPosition, newpositionBishop, null));
                    } else {
                        if (spacecheckBishop.getTeamColor() != getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newpositionBishop, null));
                        }
                        break;
                    }
                } while (true);
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
    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}






