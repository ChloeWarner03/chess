package client;

import chess.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

import java.util.Scanner;

public class InGameChess {
    private final SharedChess shared;
    private final ChessHelpers helpers;

    public InGameChess(SharedChess shared, ChessHelpers helpers) {
        this.shared = shared;
        this.helpers = helpers;
    }

    public String makeMove(String... params) throws Exception {
        helpers.playerRequiredInGame();

        if (params.length < 2 || params.length > 3) {
            throw new Exception("Error: type: move <start> <end> [promotion] (example: move e4 e6)");
        }

        ChessPosition start;
        ChessPosition end;

        try {
            start = helpers.validChessPiecePosition(params[0]);
            end = helpers.validChessPiecePosition(params[1]);
        } catch (Exception e) {
            throw new Exception("Error: need to type: example: move e2 e4");
        }

        ChessPiece.PieceType promotion = null;

        if (params.length == 3) {
            switch (params[2].toLowerCase()) {
                case "queen" -> promotion = ChessPiece.PieceType.QUEEN;
                case "rook" -> promotion = ChessPiece.PieceType.ROOK;
                case "bishop" -> promotion = ChessPiece.PieceType.BISHOP;
                case "knight" -> promotion = ChessPiece.PieceType.KNIGHT;
                default -> throw new Exception("Error: you are not able to promote that piece");
            }
        }

        ChessMove move = new ChessMove(start, end, promotion);

        shared.gameWebSocket.make_move(shared.authToken, shared.openGameNumber, move);

        return "move has been sent!";
    }

    public String redraw() throws Exception {
        helpers.playerRequiredInGame();

        MakeChessBoard.createChessBoard(
                shared.currentGame,
                shared.yourColor == ChessGame.TeamColor.WHITE || shared.yourColor == null
        );

        return "";
    }
    public String highlight(String... params) throws Exception {
        helpers.playerRequiredInGame();

        if (params.length != 1) {
            throw new Exception("Type: highlight <position>");
        }

        MakeChessBoard.highlightMoves(
                shared.currentGame,
                params[0],
                shared.yourColor == ChessGame.TeamColor.WHITE || shared.yourColor == null
        );

        return "";
    }

    // HElpers to get it from WEBSOCKET FACADE
    public String resign() throws Exception {
        helpers.playerRequiredInGame();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to resign the game? type: (yes or no) ");

        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            shared.gameWebSocket.resign(shared.authToken, shared.openGameNumber);
            return "resigned";
        }

        return "cancelled";
    }
    public String leave() throws Exception {
        helpers.playerRequiredInGame();
        shared.gameWebSocket.leave(shared.authToken, shared.openGameNumber);

        shared.state = State.LOGGED_IN;

        return "You have left the game";
    }
}
