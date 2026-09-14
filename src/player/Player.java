package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ludoboard.Board;
import ludoboard.PlayerColor;

public abstract class Player {

    private static final int PIECES_PER_PLAYER = 4;

    private final PlayerColor color;
    private final List<Piece> pieces;

    protected Player(PlayerColor color) {
        this.color = color;
        this.pieces = buildPieces(color);
    }

    public PlayerColor getColor() {
        return color;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public void leaveBase(Piece piece, Board board) {
        requireOwnership(piece);
        piece.leaveBase(board.getEntryCellPosition(color));
    }

    // Rule 1: move a piece already on the shared track forward by the dice's face value.
    public void moveForward(Piece piece, int steps, Board board) {
        requireOwnership(piece);
        int newPosition = board.getPositionAfterMoving(piece.getTrackPosition(), steps);
        piece.moveTo(newPosition);
    }

    private void requireOwnership(Piece piece) {
        if (piece.getColor() != color) {
            throw new IllegalArgumentException(piece + " does not belong to " + color);
        }
    }

    private static List<Piece> buildPieces(PlayerColor color) {
        List<Piece> newPieces = new ArrayList<>(PIECES_PER_PLAYER);
        for (int pieceNumber = 1; pieceNumber <= PIECES_PER_PLAYER; pieceNumber++) {
            newPieces.add(new Piece(color, pieceNumber));
        }
        return Collections.unmodifiableList(newPieces);
    }
}
