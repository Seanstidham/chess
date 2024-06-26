package chess;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    // so first time to take care of the privates
    private TeamColor teamTurn;
    private ChessBoard chessBoard;
    ChessMove thelastMove;


    public ChessGame() {
        // now the inits
        chessBoard = new ChessBoard();
        //reset the board each game
        chessBoard.resetBoard();
        //now i need to set the first turn to white
        teamTurn = TeamColor.WHITE;
        //and finally reset the last_move storage
        thelastMove = null;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }
    public ChessMove getLastMove() {
        return thelastMove;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }
    public void setLastMove(ChessMove move) {
        this.thelastMove = move;
    }


    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // okay so only if I have implemented everything else and have enough time ill implement the extra credit

        //first things first I need a way to get the piece
        ChessPiece currPiece = chessBoard.getPiece(startPosition);
        //if that works now a Space check
        if (currPiece == null){
            return null;
        }
        //so its going to be like validMoves in my ChessPiece file I think
        //need to import a linkedHashSet
        Collection<ChessMove> everyMove = currPiece.pieceMoves(chessBoard, startPosition);
        Collection<ChessMove> validMoves = new LinkedHashSet<>();
        //wait hold up i need to implement the check and checkmate first
        //then I can take every move and then only take the ones that dont violate check or checkmate
        //aight let me test this theory
        //okay implemented check and checkmate, now I have something to filter every move through

        // so mr google is telling me that my logic sucks
        /*
        my current logic is only filtering the current piece, when what I actually need to do to simulate EVERY OTHER MOVE AND MAKE SURE THAT DOESNT RESULT IN YOUR OWN CHECK/CHECKMATE
        I have only 1 idea for how to do this, being to write a movePiece function, then and undoMove function

         */
        for (ChessMove move : everyMove) {
            //wait I have a addPiece function maybe just adding a delete function would be easier
            //id assume id have to add it to the Chessboard file
            //okay lets hope im not wrong with that
            //okay assuming that its a correct guess for it id need to init a start and an end
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            // i need to add a SpaceCheck that restores the piece on the end position
            ChessPiece spaceCheck3 = chessBoard.getPiece(end);

            //now i gotta simulate the move maybe a move the pieces and then check if in check then move it all back prolly
            // imma cook with that
            chessBoard.addPiece(end, currPiece);
            chessBoard.roofPiece(start, currPiece);


            if (!isInCheck(currPiece.getTeamColor())) {
                validMoves.add(move);
            }

            //now put everything back
            chessBoard.addPiece(start, currPiece);
            chessBoard.roofPiece(end, currPiece);

            if (spaceCheck3 != null) {
                chessBoard.addPiece(end, spaceCheck3);
            }


        }
        return validMoves;
        //still breaking, porque?
        // oh wait my code wouldnt bring a piece that was on the end space back, it would just delete them on the whole board

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //aight let me cook i think this is what I'm going to need
        // 1 a currPiece like in validMoves
        // then i need to make sure it actually contains the move
        // and I need to check if its the correct turn
        //then at the end I need to update the last_move and update the team color
        ChessPiece currPiece = chessBoard.getPiece(move.getStartPosition());
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        //okay should probably start with the team color check cause that would break a lot quicker than anything else
        if (currPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Wrong Team Turn");
        }
        //aight now that should work so now lets do the move check
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Move Invalid");
        }
        if (move.getPromotionPiece() == null) {
            chessBoard.addPiece(end, currPiece);
            chessBoard.roofPiece(start, currPiece);
        }
        else if (move.getPromotionPiece() == ChessPiece.PieceType.QUEEN || move.getPromotionPiece() == ChessPiece.PieceType.BISHOP || move.getPromotionPiece() == ChessPiece.PieceType.KNIGHT || move.getPromotionPiece() == ChessPiece.PieceType.ROOK) {
            chessBoard.addPiece(end, new ChessPiece(currPiece.getTeamColor(), move.getPromotionPiece()));
            chessBoard.roofPiece(start, currPiece);
        }

        //set the last_move
        currPiece.setMoved();
        thelastMove = move;

        // now update the team turn
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // first get the position of the king
        //this whole function isn't working, imma try something
        //let me cook
        ChessPosition kingPos = null;

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece pieceCheck = chessBoard.getPiece(new ChessPosition(row, col));
                if (pieceCheck != null && pieceCheck.getTeamColor() == teamColor && pieceCheck.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPos = new ChessPosition(row, col);
                }
            }
        }
        if (kingPos == null) {
            return false;
        }
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece pieceCheck = chessBoard.getPiece(new ChessPosition(row, col));
                if (pieceCheck != null && pieceCheck.getTeamColor() != teamColor) {
                    Collection<ChessMove> getMoves = pieceCheck.pieceMoves(chessBoard, new ChessPosition(row, col));
                    if (getMoves != null && (getMoves.contains(new ChessMove(new ChessPosition(row, col), kingPos, null)) || getMoves.contains(new ChessMove(new ChessPosition(row, col), kingPos, ChessPiece.PieceType.QUEEN)))) {
                        return true;
                    }
                }
            }
        }
                // Okay NOW I think i figured it out


        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //first things first, it has to be in check first
        if (!isInCheck(teamColor)) {
            return false;
        }
        //now I think we can just iterate the whole board and make sure getMoves isnt empty
        int row = 1;
        while (row < 9) {
            int col = 1;
            while (col < 9) {
                ChessPiece piececheckCheckmate;
                piececheckCheckmate = chessBoard.getPiece(new ChessPosition(row,col));
                if (piececheckCheckmate != null && piececheckCheckmate.getTeamColor() == teamColor) {
                    Collection<ChessMove> getmovesCheckmate = validMoves(new ChessPosition(row,col));
                    if (getmovesCheckmate != null && !getmovesCheckmate.isEmpty()) {
                        return false;
                    }
                }

                col++;
            }
            row++;
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //gonna be real idk how stalemate works, gonna look that up real quick
        //okay mr google says its when the king isnt in check but you cant move without being put into check
        //so first things first, cant be in check
        if (isInCheck(teamColor)) {
            return false;
        }
        //now prolly need something like a full board iteration to scan all the moves
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                //literally just the same logic as checkmate pretty sure
                ChessPiece piececheckStalemate = chessBoard.getPiece(new ChessPosition(row, col));
                if (piececheckStalemate != null && piececheckStalemate.getTeamColor() == teamColor) {
                    Collection<ChessMove> getMoves = validMoves(new ChessPosition(row, col));
                    if (getMoves != null && !getMoves.isEmpty()) {
                        return false;
                    }
                }
            }
            // think this should work, cause as long as get moves is something and contains something it should return false
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
