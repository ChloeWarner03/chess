package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

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
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        //throw new RuntimeException("Not implemented");
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
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (type == PieceType.KNIGHT) {
            checkAndAdd(board, myPosition, row + 2, col + 1, moves);
            checkAndAdd(board, myPosition, row + 2, col - 1, moves);
            checkAndAdd(board, myPosition, row - 2, col + 1, moves);
            checkAndAdd(board, myPosition, row - 2, col - 1, moves);
            checkAndAdd(board, myPosition, row + 1, col + 2, moves);
            checkAndAdd(board, myPosition, row + 1, col - 2, moves);
            checkAndAdd(board, myPosition, row - 1, col + 2, moves);
            checkAndAdd(board, myPosition, row - 1, col - 2, moves);
        }
        if (type == PieceType.KING) {
            checkAndAdd(board, myPosition, row + 1, col, moves);     // up
            checkAndAdd(board, myPosition, row - 1, col, moves);     // down
            checkAndAdd(board, myPosition, row, col + 1, moves);     // right
            checkAndAdd(board, myPosition, row, col - 1, moves);     // left
            checkAndAdd(board, myPosition, row + 1, col + 1, moves); // up-right
            checkAndAdd(board, myPosition, row + 1, col - 1, moves); // up-left
            checkAndAdd(board, myPosition, row - 1, col + 1, moves); // down-right
            checkAndAdd(board, myPosition, row - 1, col - 1, moves); // down-left
        }

        return moves;
    }

    private void checkAndAdd(ChessBoard board, ChessPosition from, int newRow, int newCol, List<ChessMove> moves) {
        if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) return; // off the board

        ChessPosition destination = new ChessPosition(newRow, newCol);
        ChessPiece occupant = board.getPiece(destination);

        if (occupant == null || occupant.getTeamColor() != pieceColor) {
            moves.add(new ChessMove(from, destination, null)); // empty or enemy, can move there
        }

    }
}
