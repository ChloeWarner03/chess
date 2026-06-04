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
            cols = new String[]{RESET_TEXT_COLOR  + "a","b","c","d","e","f","g","h"};
        } else {
            cols = new String[]{RESET_TEXT_COLOR + "h","g","f","e","d","c","b","a"};
        }
        //get the spacing right
        String border = "  ";
        for (String col : cols) {
            border +=  "  " + col + " ";}
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
                System.out.print(SET_BG_COLOR_BLACK);
            } else {
                System.out.print(SET_BG_COLOR_WHITE);
            }
            System.out.print(chessPieceImages(board.getPiece(new ChessPosition(row, col))));
        }
        System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR + " " + row);
    }

    private static String chessPieceImages(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        //FIxing the color

        switch (piece.getPieceType()) {
            case KING:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return SET_TEXT_COLOR_MAGENTA + WHITE_KING;
                } else {return SET_TEXT_COLOR_BLUE + BLACK_KING;
                }


            case QUEEN:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return SET_TEXT_COLOR_MAGENTA +WHITE_QUEEN;
                } else { return SET_TEXT_COLOR_BLUE +BLACK_QUEEN;
                }

            case BISHOP:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return SET_TEXT_COLOR_MAGENTA +WHITE_BISHOP;
                } else { return SET_TEXT_COLOR_BLUE +BLACK_BISHOP;
                }

            case KNIGHT:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) { return SET_TEXT_COLOR_MAGENTA +WHITE_KNIGHT;
                } else { return SET_TEXT_COLOR_BLUE +BLACK_KNIGHT;
                }

            case ROOK:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) { return SET_TEXT_COLOR_MAGENTA +WHITE_ROOK;
                } else { return SET_TEXT_COLOR_BLUE +BLACK_ROOK;
                }
            case PAWN:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return SET_TEXT_COLOR_MAGENTA +WHITE_PAWN;
                } else { return SET_TEXT_COLOR_BLUE +BLACK_PAWN;
                }
        }
        return EMPTY;
    }


}