package chess; //This is for Phase 1 of the chess project
//These are my imputs
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.//Not doing till phase 1
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    //These are my personal helper functions
    private void testKing(ChessMove move, TeamColor color, Collection<ChessMove> correctMove) {
        ChessPiece doMove = board.getPiece(move.getStartPosition());
        ChessPiece takePiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), doMove); // make the move temporarily
        board.addPiece(move.getStartPosition(), null);
        boolean kingInRisk = isInCheck(color);        // if the king is safe add the move
        if (!kingInRisk) {
            correctMove.add(move);
        }
        board.addPiece(move.getStartPosition(), doMove); // undo the move so the board goes back to normal
        board.addPiece(move.getEndPosition(), takePiece);
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> correctMove = new ArrayList<>();
        Collection<ChessMove> entireMoves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : entireMoves) {
            testKing(move, piece.getTeamColor(), correctMove);
        }
        return correctMove;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */

    // see if you can make move
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece playingPiece = board.getPiece(move.getStartPosition());
        if (playingPiece == null || playingPiece.getTeamColor() != teamTurn) { // if there is no piece there or it is not your turn
            throw new InvalidMoveException("Not your piece or no piece there");
        }
        Collection<ChessMove> rightMove = validMoves(move.getStartPosition()); // get all the legal moves for that piece
        if (rightMove == null || !rightMove.contains(move)) { // if the move is not in the legal moves list throw an exception
            throw new InvalidMoveException("That move is not legal");
        }
        if (move.getPromotionPiece() != null) { // if the pawn is getting promoted swap it out
            playingPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), playingPiece); // put the piece in the new spot and clear the old spot
        board.addPiece(move.getStartPosition(), null);
        if (teamTurn == TeamColor.WHITE) { // switch whose turn it is
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    //This is for check and not checkmate and this means you got to move soemthing so the kign is not in check
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition foundKing = null; //find the king
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    foundKing = pos; // I found the king!
                }
            }
        }
        TeamColor otherTeam; //who is other side
        if (teamColor == TeamColor.WHITE) {
            otherTeam = TeamColor.BLACK;
        } else {
            otherTeam = TeamColor.WHITE;
        }
        for (int row = 1; row <= 8; row++) { //see if they can reach king
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == otherTeam) {
                    for (ChessMove move : piece.pieceMoves(board, pos)) {
                        if (move.getEndPosition().equals(foundKing)) {
                            return true; //king is in check!
                        }
                    }
                }
            }
        }
        return false; // king is safe
    }


    //These are going to be the helper functions for the next two
    private boolean outOfMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                if (moreMoves(teamColor, pos)) {
                    return false; // found a legal move
                }
            }
        }
        return true; // no legal moves found
    }

    private boolean moreMoves(TeamColor teamColor, ChessPosition pos) {
        ChessPiece piece = board.getPiece(pos);
        if (piece != null && piece.getTeamColor() == teamColor) {
            Collection<ChessMove> rightMove = validMoves(pos);
            return rightMove != null && rightMove.size() > 0;
        }
        return false;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
//This is for Checkmate not just in check
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return outOfMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return outOfMoves(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
