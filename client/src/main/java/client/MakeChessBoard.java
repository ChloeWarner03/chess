package client;
import chess.ChessGame;
import chess.ChessBoard;
import static ui.EscapeSequences.*;



public class MakeChessBoard {
    public static void createChessBoard(ChessGame game, boolean seeBlackSide) {
        ChessBoard board = game.getBoard();
        createChessBoardCol(seeBlackSide);
        if (seeBlackSide) {
            for (int row = 1; row <= 8; row++) {
                createChessBoardRow(board, row, false);
            }
        } else {
            for (int row = 8; row >= 1; row--) {
                createChessBoardRow(board, row, true);
            }
        }

        createChessBoardCol(seeBlackSide);
    }

    public static void createChessBoardCol(boolean seeBlackSide) {
        String[] cols;
        if (seeBlackSide) {
            cols = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else {
            cols = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        }
        //get the spacing right
        String border = "   ";
        for (String col : cols) {
            border += " " + col + " ";}
        System.out.println(border);
    }

    public static void createChessBoardRow(ChessBoard board, int row, boolean whitePerspective) {
    }

}