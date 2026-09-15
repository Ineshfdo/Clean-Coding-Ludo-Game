package player;

import direction.MovementDirectionStrategy;
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

    // T-7: the player's total is every piece's own count, summed on demand.
    public int getCaptureCount() {
        return pieces.stream().mapToInt(Piece::getCaptureCount).sum();
    }

    public void recordCapture(Piece piece) {
        requireOwnership(piece);
        piece.recordCapture();
    }

    public void leaveBase(Piece piece, Board board) {
        requireOwnership(piece);
        piece.leaveBase(board.getEntryCellPosition(color));
    }

    // Rule 7/T-9: sends this piece to Base with all its stored information reset.
    public void returnToBase(Piece piece) {
        requireOwnership(piece);
        piece.returnToBase();
    }

    // T-1: assigns the coin toss's chosen direction to a piece that just left Base.
    public void assignMovementDirection(Piece piece, MovementDirectionStrategy movementDirection) {
        requireOwnership(piece);
        piece.assignMovementDirection(movementDirection);
    }

    // T-5: every member of a moving block shares the block's chosen direction.
    public void adoptBlockDirection(Piece piece, MovementDirectionStrategy blockDirection) {
        requireOwnership(piece);
        piece.adoptBlockDirection(blockDirection);
    }

    // T-5: a piece leaving its block resumes the direction assigned at Base exit.
    public void restoreOriginalDirection(Piece piece) {
        requireOwnership(piece);
        piece.restoreOriginalDirection();
    }

    // Rule 1: moves a piece by the dice value using T-1's travelDirection, own direction unchanged.
    public void moveForward(
            Piece piece, int steps, Board board, HomeStraightEntryRule homeStraightEntryRule,
            MovementDirectionStrategy travelDirection) {
        requireOwnership(piece);

        if (piece.isOnHomeStraight()) {
            applyHomeStraightMove(piece, steps);
        } else {
            applyTrackMove(piece, steps, board, homeStraightEntryRule, travelDirection);
        }
    }

    // T-1: reaching Approach only enters HomeStraight once homeStraightEntryRule allows it.
    private void applyTrackMove(
            Piece piece, int steps, Board board, HomeStraightEntryRule homeStraightEntryRule,
            MovementDirectionStrategy travelDirection) {
        int stepsToApproach = travelDirection.stepsToApproach(piece.getTrackPosition(), color, board);

        if (steps < stepsToApproach) {
            piece.moveTo(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
            return;
        }
        if (steps == stepsToApproach) {
            // Landing exactly on Approach keeps the piece on the track - HomeStraight starts after it.
            piece.moveTo(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
            piece.recordApproachPass();
            return;
        }

        piece.recordApproachPass();
        if (homeStraightEntryRule.forbidsEntry(piece)) {
            piece.moveTo(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
            return;
        }

        applyHomeStraightMove(piece, steps - stepsToApproach);
    }

    // Reaching or passing the last HomeStraight cell sends the piece Home.
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
