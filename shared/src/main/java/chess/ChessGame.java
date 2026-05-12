package chess; //This is for Phase 1 of the chess project
//These are my imputs
import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Collections;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.//Not doing till phase 1
 */
//This is setting it up
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
    private TeamColor getOpponent(TeamColor teamColor) {
        return teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private void switchTurn() {
        teamTurn = getOpponent(teamTurn);
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    private boolean pieceAttacksTarget(ChessPiece piece, ChessPosition pos, ChessPosition target, TeamColor attackingTeam) {
        if (piece == null || piece.getTeamColor() != attackingTeam) {
            return false;
        }
        for (ChessMove move : piece.pieceMoves(board, pos)) {
            if (move.getEndPosition().equals(target)) {
                return true;
            }
        }
        return false;
    }

    private boolean enemyReachesPiece(ChessPosition target, TeamColor attackingTeam) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                if (pieceAttacksTarget(board.getPiece(pos), pos, target, attackingTeam)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void moveRequest(ChessPiece piece, ChessMove move) throws InvalidMoveException {
        if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your piece or no piece there");
        }
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (legalMoves == null || !legalMoves.contains(move)) {
            throw new InvalidMoveException("That move is not legal");
        }
    }

    //These are the functions I needed to do
    private void testKing(ChessMove move, TeamColor color, Collection<ChessMove> correctMove) {
        ChessPiece doMove = board.getPiece(move.getStartPosition());
        ChessPiece takePiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), doMove);
        board.addPiece(move.getStartPosition(), null);
        if (!isInCheck(color)) {
            correctMove.add(move);
        }
        board.addPiece(move.getStartPosition(), doMove);
        board.addPiece(move.getEndPosition(), takePiece);
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    //Can I do this move?
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return Collections.emptyList();
        }
        Collection<ChessMove> correctMove = new ArrayList<>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
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
        moveRequest(playingPiece, move);
        if (move.getPromotionPiece() != null) {
            playingPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), playingPiece);
        board.addPiece(move.getStartPosition(), null);
        switchTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    //This is for check and not checkmate and this means you got to move soemthing so the kign is not in check
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        return enemyReachesPiece(kingPos, getOpponent(teamColor));
    }
    //These are going to be the helper functions for the next two
    private boolean outOfMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                if (moreMoves(teamColor, pos)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean moreMoves(TeamColor teamColor, ChessPosition pos) {
        ChessPiece piece = board.getPiece(pos);
        if (piece != null && piece.getTeamColor() == teamColor) {
            Collection<ChessMove> legalMoves = validMoves(pos);
            return legalMoves != null && !legalMoves.isEmpty();
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
        return isInCheck(teamColor) && outOfMoves(teamColor);
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && outOfMoves(teamColor);
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
