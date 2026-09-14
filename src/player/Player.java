package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ludoboard.Board;
import ludoboard.HomeStraightCell;
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

    // Rule 1: moves a piece forward by the dice's face
    // value, along the track or its own HomeStraight.
    public void moveForward(Piece piece, int steps, Board board) {
        requireOwnership(piece);

        if (piece.isOnHomeStraight()) {
            applyHomeStraightMove(piece, steps);
        } else {
            applyTrackMove(piece, steps, board);
        }
    }

    // Once a move would pass this color's own Approach cell, the remaining steps continue onto the HomeStraight instead of looping back around the track.
    private void applyTrackMove(Piece piece, int steps, Board board) {
        int approachPosition = board.getApproachCellPosition(color);
        int stepsToApproach = board.getForwardDistance(piece.getTrackPosition(), approachPosition);

        if (steps <= stepsToApproach) {
            piece.moveTo(board.getPositionAfterMoving(piece.getTrackPosition(), steps));
            return;
        }

        applyHomeStraightMove(piece, steps - stepsToApproach);
    }

    // Reaching or passing the last HomeStraight cell sends the piece Home, where it finishes and stops moving.
    private void applyHomeStraightMove(Piece piece, int steps) {
        int currentIndex = piece.isOnHomeStraight() ? piece.getHomeStraightIndex() : -1;
        int newIndex = currentIndex + steps;

        if (newIndex >= HomeStraightCell.CELLS_PER_HOME_STRAIGHT) {
            piece.moveHome();
            return;
        }

        piece.moveToHomeStraight(newIndex);
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
