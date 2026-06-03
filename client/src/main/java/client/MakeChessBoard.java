package client;
import chess.ChessGame;
import chess.ChessBoard;
import static ui.EscapeSequences.*;
import chess.ChessPosition;
import chess.ChessPiece;



public class MakeChessBoard {
    public static void createChessBoard(ChessGame game, boolean seePinkSide) {
        ChessBoard board = game.getBoard();
        createChessBoardCol(seePinkSide);
        if (seePinkSide) {
            for (int row = 8; row >= 1; row--) {
                createChessBoardRow(board, row, true);
            }
        } else {
            for (int row = 1; row <= 8; row++) {
                createChessBoardRow(board, row, false);
            }
        }

        createChessBoardCol(seePinkSide);
    }

    public static void createChessBoardCol(boolean seePinkSide) {
        String[] cols;
        if (seePinkSide) {
            cols = new String[]{"a","b","c","d","e","f","g","h"};
        } else {
            cols = new String[]{"h","g","f","e","d","c","b","a"};
        }
        //get the spacing right
        String border = "   ";
        for (String col : cols) {
            border += " " + col + " ";}
        System.out.println(border);
    }

    public static void createChessBoardRow(ChessBoard board, int row, boolean seePinkSide) {
        int[] cols;
        if (seePinkSide) {cols = new int[]{1,2,3,4,5,6,7,8};
        } else {cols = new int[]{8,7,6,5,4,3,2,1};
        }
        System.out.print(" " + row + " ");
        for (int col : cols) {
            if ((row + col) % 2 == 0) {
                System.out.print(SET_BG_COLOR_WHITE);
            } else {
                System.out.print(SET_BG_COLOR_DARK_GREEN);
            }
            System.out.print(chessPieceImages(board.getPiece(new ChessPosition(row, col))));
        }
        System.out.println(RESET_BG_COLOR + " " + row);
    }

    private static String chessPieceImages(ChessPiece piece) {
        return "";
    }


}