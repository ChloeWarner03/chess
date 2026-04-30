package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece [8][8]; //wrote
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece; //wrote
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return squares[position.getRow()-1][position.getColumn()-1];
        //wrote but copied from above with return
        //Without the = piece part
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() { //NEED TO DO
        squares = new ChessPiece[8][8]; //new board time!

        //the order of the first row is going to be rook, knight, bishop, queen, king, bishop, knight, rook
        ChessPiece.PieceType[] firstRow = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK,
        };

        //PUT IN THE ROWS
        for (int col = 1; col <= 8; col++) {

            //this is the special row
            addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, firstRow[col - 1]));
            addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, firstRow[col - 1]));
            //this is for the pawns
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

    }

    //Override Objects

    @Override //Added the hashstuff
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares); //Changed from Objects to Arrays
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
