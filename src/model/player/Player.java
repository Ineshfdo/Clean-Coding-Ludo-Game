package model.player;
import model.effect.MovementEffect;
import model.piece.Piece;
import model.piece.PieceRestrictionState;
import model.player.rule.HomeStraightEntryRule;

import model.position.MovementDirectionStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import config.constant.BoardConstants;
import model.board.Board;
import config.enums.PlayerColor;

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

    // GAME_OVER: this player has won once every one of its 4 pieces has reached Home.
    public boolean hasAllPiecesHome() {
        return pieces.stream().allMatch(Piece::isHome);
    }

    // Observer pattern reporting: "on the board" means past Base and not yet Home.
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

    // Rule 7/T-9: sends this piece to Base with all its stored information reset.
    public void returnToBase(Piece piece) {
        requireOwnership(piece);
        piece.returnToBase();
    }

    // T-11: places a piece directly onto a track cell - a jump, not a normal move.
    public void teleportTo(Piece piece, int trackPosition) {
        requireOwnership(piece);
        piece.moveTo(trackPosition);
    }

    // T-12: assigns this piece's own Energized/Sick status, from a Mystery Cell Alpha teleport.
    public void applyIndividualEffect(Piece piece, MovementEffect effect) {
        requireOwnership(piece);
        piece.applyIndividualEffect(effect);
    }

    // T-12: assigns the shared Energized/Sick status for the block this piece teleported
    // with, recorded against that block's exact size.
    public void applyBlockEffect(Piece piece, MovementEffect effect, int blockSize) {
        requireOwnership(piece);
        piece.applyBlockEffect(effect, blockSize);
    }

    // T-12: expires every piece's temporary movement effects by one round.
    public void tickMovementEffects() {
        for (Piece piece : pieces) {
            piece.tickIndividualEffect();
            piece.tickBlockEffect();
        }
    }

    // T-13: places this piece into the Beta-restricted state after a Mystery Cell Beta teleport.
    public void applyRestriction(Piece piece, PieceRestrictionState restrictionState) {
        requireOwnership(piece);
        piece.applyRestriction(restrictionState);
    }

    // T-13: expires one round of every piece's movement restriction.
    public void tickRestrictions() {
        for (Piece piece : pieces) {
            piece.tickRestriction();
        }
    }

    // T-13: records this round's roll against every currently restricted piece's tracking.
    public void recordRestrictionRoll(int rollValue) {
        for (Piece piece : pieces) {
            if (piece.getRestrictionState().forbidsMovement()) {
                piece.recordRestrictionRoll(rollValue);
            }
        }
    }

    // T-13: every piece whose Beta restriction has now triggered the consecutive-roll condition.
    public List<Piece> findPiecesTriggeredForReturnToBase() {
        List<Piece> triggeredPieces = new ArrayList<>();
        for (Piece piece : pieces) {
            if (piece.getRestrictionState().hasTriggeredReturnToBase()) {
                triggeredPieces.add(piece);
            }
        }
        return triggeredPieces;
    }

    // T-1: assigns the coin toss's chosen direction to a piece that just left Base.
    public void assignMovementDirection(Piece piece, MovementDirectionStrategy movementDirection) {
        requireOwnership(piece);
        piece.assignMovementDirection(movementDirection);
    }

    // T-4/T-5: every member of a moving block shares the block's chosen direction, and records
    // the block's current size so a later new arrival can be detected and fairly re-compared.
    public void adoptBlockDirection(Piece piece, MovementDirectionStrategy blockDirection, int blockSize) {
        requireOwnership(piece);
        piece.adoptBlockDirection(blockDirection, blockSize);
    }

    // T-5: a piece leaving its block resumes the direction assigned at Base exit.
    public void restoreOriginalDirection(Piece piece) {
        requireOwnership(piece);
        piece.restoreOriginalDirection();
    }

    // T-14: a Gamma teleport permanently reverses this piece's direction (Strategy pattern -
    // ClockwiseMovementStrategy is replaced with CounterClockwiseMovementStrategy, or vice versa).
    public void reverseDirection(Piece piece) {
        requireOwnership(piece);
        piece.reverseDirection();
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
