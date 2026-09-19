package model.player;

import config.constant.BoardConstants;
import config.enums.PlayerColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.effect.movement.MovementEffect;
import model.effect.restriction.PieceRestrictionState;
import model.piece.Piece;
import model.player.rule.home.HomeStraightEntryRule;

// Owns a player's four pieces; every piece change goes through here.
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

    // T-7: total captures across all pieces.
    public int getCaptureCount() {
        return pieces.stream().mapToInt(Piece::getCaptureCount).sum();
    }

    // GAME_OVER: a player wins once all 4 pieces reach Home.
    public boolean hasAllPiecesHome() {
        return pieces.stream().allMatch(Piece::isHome);
    }

    // On the board means past Base and not yet Home.
    public int countPiecesOnBoard() {
        return (int) pieces.stream().filter(piece -> !piece.isAtBase() && !piece.isHome()).count();
    }

    public int countPiecesAtBase() {
        return (int) pieces.stream().filter(Piece::isAtBase).count();
    }

    public void recordCapture(Piece piece) {
        requireOwnership(piece);
        piece.recordCapture();
    }

    public void leaveBase(Piece piece, Board board) {
        requireOwnership(piece);
        piece.leaveBase(board.getEntryCellPosition(color));
    }

    // Rule 7/T-9: sends a piece to Base with its stored info reset.
    public void returnToBase(Piece piece) {
        requireOwnership(piece);
        piece.returnToBase();
    }

    // T-11: places a piece directly on a track cell (a jump, not a move).
    public void teleportTo(Piece piece, int trackPosition) {
        requireOwnership(piece);
        piece.moveTo(trackPosition);
    }

    // T-12: assigns a piece's own Energized/Sick effect (Alpha teleport).
    public void applyIndividualEffect(Piece piece, MovementEffect effect) {
        requireOwnership(piece);
        piece.applyIndividualEffect(effect);
    }

    // T-12: assigns the block's shared effect, recorded against its size.
    public void applyBlockEffect(Piece piece, MovementEffect effect, int blockSize) {
        requireOwnership(piece);
        piece.applyBlockEffect(effect, blockSize);
    }

    // T-12: expires every piece's movement effects by one round.
    public void tickMovementEffects() {
        for (Piece piece : pieces) {
            piece.tickIndividualEffect();
            piece.tickBlockEffect();
        }
    }

    // T-13: puts a piece into the Beta-restricted state.
    public void applyRestriction(Piece piece, PieceRestrictionState restrictionState) {
        requireOwnership(piece);
        piece.applyRestriction(restrictionState);
    }

    // T-13: expires one round of every piece's restriction.
    public void tickRestrictions() {
        for (Piece piece : pieces) {
            piece.tickRestriction();
        }
    }

    // T-13: records this round's roll for every restricted piece.
    public void recordRestrictionRoll(int rollValue) {
        for (Piece piece : pieces) {
            if (piece.getRestrictionState().forbidsMovement()) {
                piece.recordRestrictionRoll(rollValue);
            }
        }
    }

    // T-13: pieces whose Beta restriction has triggered a return to Base.
    public List<Piece> findPiecesTriggeredForReturnToBase() {
        List<Piece> triggeredPieces = new ArrayList<>();

        for (Piece piece : pieces) {
            if (piece.getRestrictionState().hasTriggeredReturnToBase()) {
                triggeredPieces.add(piece);
            }
        }

        return triggeredPieces;
    }

    // T-1: gives a piece that just left Base its coin-toss direction.
    public void assignMovementDirection(Piece piece, MovementDirectionStrategy movementDirection) {
        requireOwnership(piece);
        piece.assignMovementDirection(movementDirection);
    }

    // T-4/T-5: block members share one direction and record the block size.
    public void adoptBlockDirection(Piece piece, MovementDirectionStrategy blockDirection, int blockSize) {
        requireOwnership(piece);
        piece.adoptBlockDirection(blockDirection, blockSize);
    }

    // T-5: a piece leaving its block resumes its original direction.
    public void restoreOriginalDirection(Piece piece) {
        requireOwnership(piece);
        piece.restoreOriginalDirection();
    }

    // T-14: a Gamma teleport permanently reverses a piece's direction.
    public void reverseDirection(Piece piece) {
        requireOwnership(piece);
        piece.reverseDirection();
    }

    // Rule 1: moves a piece by the dice value using the travel direction (T-1).
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

    // T-1: reaching Approach enters HomeStraight only if the entry rule allows.
    private void applyTrackMove(
            Piece piece, int steps, Board board, HomeStraightEntryRule homeStraightEntryRule,
            MovementDirectionStrategy travelDirection) {
        int stepsToApproach = travelDirection.stepsToApproach(piece.getTrackPosition(), color, board);

        if (steps < stepsToApproach) {
            piece.moveTo(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
            return;
        }

        if (steps == stepsToApproach) {
            // Landing exactly on Approach keeps the piece on the track.
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

        if (newIndex >= BoardConstants.CELLS_PER_HOME_STRAIGHT) {
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
