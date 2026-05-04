package chess;

//These are my imports
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
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


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor; //this is from the video
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
        if (type == PieceType.KING) { //The error when commiting says that I can use a switch function for this, replace the chain with switch expressions (need to look into)
            return moveKing(board, myPosition);
        } else if (type == PieceType.QUEEN) {
            return moveQueen (board, myPosition);
        } else if (type == PieceType.BISHOP) {
            return moveBishop (board, myPosition);
        } else if (type == PieceType.KNIGHT) {
            return moveKnight (board, myPosition);
        } else if (type == PieceType.ROOK) {
            return moveRook (board, myPosition);
        } else {
            return movePawn (board, myPosition);
        }
    }
    // the pawn is a weird one, I should make it a little more simple
    private List<ChessMove> movePawn(ChessBoard board, ChessPosition pos) {
        List<ChessMove> moves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();

        int direction;
        int startRow;
        int endRow;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            endRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            endRow = 1;
        }

        // one square forward
        int oneforward = row + direction;
        ChessPosition oneStep = new ChessPosition(oneforward, col);
        if (board.getPiece(oneStep) == null) {
            promotePawn(pos, oneStep, oneforward, endRow, moves);

            // two squares forward only if on starting row
            if (row == startRow) {
                int doublerow = row + 2 * direction;
                ChessPosition doubleStep = new ChessPosition(doublerow, col);
                if (board.getPiece(doubleStep) == null) {
                    moves.add(new ChessMove(pos, doubleStep, null));
                }
            }
        }

        // diagonal captures
        capture(board, pos, oneforward, col - 1, endRow, moves); // left
        capture(board, pos, oneforward, col + 1, endRow, moves); // right

        return moves;
    }
    //These are going to be the different mothods for the different pieces

    //KING!!!
    private List<ChessMove> moveKing (ChessBoard board, ChessPosition pos) {
        List<ChessMove> moves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();
        trymove(board, pos, row + 1, col, moves); //this is up
        trymove(board, pos, row - 1, col, moves); // this is down
        trymove(board, pos, row , col - 1, moves); //this is to the left
        trymove(board, pos, row , col + 1, moves); //right
        trymove(board, pos, row + 1 , col -1, moves); // up and to the left
        trymove(board, pos, row + 1 , col +1, moves); // up and to the right
        trymove(board, pos, row - 1 , col -1, moves); // down and to the left
        trymove(board, pos, row - 1 , col + 1, moves); // up and to the right

        return moves;
    }

    private List<ChessMove> moveQueen(ChessBoard board, ChessPosition pos) {
        List<ChessMove> moves = new ArrayList<>();
        moves.addAll(moveRook(board, pos)); //Rook moves for the Queen
        moves.addAll(moveBishop(board, pos)); //Bishop moves for the Queen
        return moves;
    }

    private List<ChessMove> moveBishop(ChessBoard board, ChessPosition pos) {
        List<ChessMove> moves = new ArrayList<>();
        int row = pos.getRow(); //The error says that I should refactor this method to reduce the cognitive complexity (need to look into)
        int col = pos.getColumn(); //Will need to do

        for (int i = 1; row + i <= 8 && col - i >= 1; i++) {   // up to the left
            if (slide(board, pos, row + i, col - i, moves)) break;
        }
        for (int i = 1; row + i <= 8 && col + i <= 8; i++) {   // up to the right
            if (slide(board, pos, row + i, col + i, moves)) break;
        }
        for (int i = 1; row - i >= 1 && col - i >= 1; i++) {   // down to the left
            if (slide(board, pos, row - i, col - i, moves)) break;
        }
        for (int i = 1; row - i >= 1 && col + i <= 8; i++) {   // down to the right
            if (slide(board, pos, row - i, col + i, moves)) break;
        }
        return moves;
    }
//options, up 2, right 1 or left 1. right 2, up 1 or down 1, down 2, right 1 or left 1, left 2, up 1 and down 1
    private List<ChessMove> moveKnight (ChessBoard board, ChessPosition pos) {
        List<ChessMove> moves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();
        trymove(board, pos, row + 2, col + 1, moves); //up 2, right 1
        trymove(board, pos, row + 2, col - 1 , moves); //up 2, left 1
        trymove(board, pos, row - 2, col + 1, moves); //down 2, right 1
        trymove(board, pos, row - 2, col - 1, moves); //down 2, left 1
        trymove(board, pos, row + 1 , col + 2, moves); //right 2 up 1
        trymove(board, pos, row - 1 , col + 2, moves); //right 2 down 1
        trymove(board, pos, row + 1 , col - 2, moves); //left 2 up 1
        trymove(board, pos, row - 1 , col - 2, moves); //left 2 down 1
        return moves;
    }

    private List<ChessMove> moveRook (ChessBoard board, ChessPosition pos) {
        List<ChessMove> moves = new ArrayList<>(); //need to look at again
        int row = pos.getRow();
        int col = pos.getColumn();
        for (int j = col - 1;j >= 1; j--) {        // left
            if (slide(board, pos, row, j, moves)) break;
        }
        for (int i = row + 1;i <= 8; i++) {        // up
            if (slide(board, pos, i, col, moves)) break;
        }
        for (int j = col + 1;j <= 8; j++) {        // right
            if (slide(board, pos, row, j, moves)) break;
        }
        for (int i = row - 1;i >= 1; i--) {        // down
            if (slide(board, pos, i, col, moves)) break;
        }
        return moves;
    }


    //These are going to be the helper functions that I create as I go


    //try the move and see if it works
    private void trymove(ChessBoard board, ChessPosition from, int newRow, int newCol, List<ChessMove> moves){
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) return; //you are not on the board anymore

            ChessPosition area = new ChessPosition(newRow, newCol);
            ChessPiece piecethere = board.getPiece(area);

            if (piecethere == null || piecethere.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(from, area, null)); // empty or enemy, can move there
            }

        }

    //This one I made for the pieces that slide, will use for queen, rook and bishop

    private boolean slide(ChessBoard board, ChessPosition from, int newRow, int newCol, List<ChessMove> moves) {
        ChessPosition area = new ChessPosition(newRow, newCol);
        ChessPiece piecethere = board.getPiece(area);
        if (piecethere == null) {
            moves.add(new ChessMove(from, area, null));
            return false; //keep going
        } else if (piecethere.getTeamColor() != pieceColor) { //first we want o see what team they are on
            moves.add(new ChessMove(from, area, null));
            return true; //take the piece and stop
        } else {
            return true; //our side and stop
        }
    }

    //capture diagonally for the pawn
    private void capture(ChessBoard board, ChessPosition from, int newRow, int newCol, int endRow, List<ChessMove> moves) {
        if (newCol < 1 || newCol > 8) return; // off the board
        ChessPosition placement = new ChessPosition(newRow, newCol);
        ChessPiece piecethere = board.getPiece(placement);
        if (piecethere != null && piecethere.getTeamColor() != pieceColor) {
            promotePawn(from, placement, newRow, endRow, moves);
        }
    }

    //this is when the pawn is at then end and it can be promoted
    private void promotePawn(ChessPosition from, ChessPosition placement, int placementrow, int endRow, List<ChessMove> moves) {
        if (placementrow == endRow) {
            moves.add(new ChessMove(from, placement, PieceType.QUEEN));
            moves.add(new ChessMove(from, placement, PieceType.ROOK));
            moves.add(new ChessMove(from, placement, PieceType.BISHOP));
            moves.add(new ChessMove(from, placement, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, placement, null));
        }
    }


    //Override Stuff

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
