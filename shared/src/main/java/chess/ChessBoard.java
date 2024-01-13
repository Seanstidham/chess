package chess;

import java.util.HashMap;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    /* Things I need to do to make bishop test work
    1 first create a chessboard with an 8X8 array
    2 add the piece to the position given in the args
    3 define the moves the bishop can make
    4 return if it has valid moves based on the position

     */

    // create the board
//    private final ChessPiece[][] board;
    //arrays werent working, going to try a Hashmap (i need more practice anyways)

    private HashMap<ChessPosition, ChessPiece> board;

    public ChessBoard() {
        this.board = new HashMap<>();
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board.put(position, piece);



//        int row = position.getRow();
//        int col = position.getColumn();
//        board[row][col] = piece;

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        ChessPiece curr = board.get(position);
        for (ChessPosition key : board.keySet()) {
            if (position.getRow() == key.getRow() && position.getColumn() == key.getColumn()) {
                curr = board.get(key);
                break;
            }
        }
        return curr;


//        int row = position.getRow();
//        int col = position.getColumn();
//        return board[row][col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //for the purposes of this section i think the chessboard reset should just clear it all
        board.clear();

//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                board[i][j] = null;
//            }
//        }
    }
}
