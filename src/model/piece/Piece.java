package model.piece;

import config.enums.PieceLocation;
import config.enums.PlayerColor;
import exception.InvalidPieceStateException;
import model.direction.MovementDirectionStrategy;
import model.effect.movement.MovementEffect;
import model.effect.restriction.NoRestrictionState;
import model.effect.restriction.PieceRestrictionState;
import utils.color.PlayerColorNames;

// Rule 6: a piece travels Base -> track -> HomeStraight -> Home, then stops.
public final class Piece {

    private final PlayerColor color;
    private final int number;

    private PieceLocation location;
    private int trackPosition;
    private int homeStraightIndex;

    private MovementDirectionStrategy movementDirection;
    private MovementDirectionStrategy originalMovementDirection;
    private int adoptedForBlockSize;

    private int approachPassCount;
    private int captureCount;

    private MovementEffect individualEffect = MovementEffect.none();
    private MovementEffect blockEffect = MovementEffect.none();
    private int blockEffectAssignedForBlockSize;

    private PieceRestrictionState restrictionState = NoRestrictionState.getInstance();

    public Piece(PlayerColor color, int number) {
        this.color = color;
        this.number = number;
        this.location = PieceLocation.BASE;
    }

    public PlayerColor getColor() {
        return color;
    }

    public boolean isAtBase() {
        return location == PieceLocation.BASE;
    }

    public boolean isOnTrack() {
        return location == PieceLocation.TRACK;
    }

    public boolean isOnHomeStraight() {
        return location == PieceLocation.HOME_STRAIGHT;
    }

    public boolean isHome() {
        return location == PieceLocation.HOME;
    }

    public int getTrackPosition() {
        requireLocation(PieceLocation.TRACK);

        return trackPosition;
    }

    public int getHomeStraightIndex() {
        requireLocation(PieceLocation.HOME_STRAIGHT);

        return homeStraightIndex;
    }

    // T-1/T-5: current direction - its own, or its block's while grouped.
    public MovementDirectionStrategy getMovementDirection() {
        requireDirection(movementDirection);

        return movementDirection;
    }

    // False only while the piece is at Base, before its first coin toss.
    public boolean hasMovementDirection() {
        return movementDirection != null;
    }

    // T-5: direction assigned at Base exit, unaffected by blocks.
    public MovementDirectionStrategy getOriginalMovementDirection() {
        requireDirection(originalMovementDirection);

        return originalMovementDirection;
    }

    // T-5: true while a block's direction replaces its own.
    public boolean hasAdoptedBlockDirection() {
        return movementDirection != originalMovementDirection;
    }

    // T-1: times this piece has passed its Approach cell.
    public int getApproachPassCount() {
        return approachPassCount;
    }

    // T-7: opponent pieces this piece has captured.
    public int getCaptureCount() {
        return captureCount;
    }

    // T-12: own Energized/Sick status, used when moving alone.
    public MovementEffect getIndividualEffect() {
        return individualEffect;
    }

    // T-12: shared block status; check hasActiveBlockEffectForSize() first.
    public MovementEffect getBlockEffect() {
        return blockEffect;
    }

    // T-12: true only if the block effect matches this block size.
    public boolean hasActiveBlockEffectForSize(int currentBlockSize) {
        return blockEffect.isActive() && blockEffectAssignedForBlockSize == currentBlockSize;
    }

    // T-13: current movement restriction, e.g. Beta-restricted.
    public PieceRestrictionState getRestrictionState() {
        return restrictionState;
    }

    // Mutators below are public, but only Player should call them.
    public void leaveBase(int entryCellPosition) {
        this.location = PieceLocation.TRACK;
        this.trackPosition = entryCellPosition;
        this.approachPassCount = 0;
    }

    public void assignMovementDirection(MovementDirectionStrategy movementDirection) {
        this.movementDirection = movementDirection;
        this.originalMovementDirection = movementDirection;
    }

    // T-4/T-5: borrows a block's direction while grouped, remembering the block size.
    public void adoptBlockDirection(MovementDirectionStrategy blockDirection, int blockSize) {
        this.movementDirection = blockDirection;
        this.adoptedForBlockSize = blockSize;
    }

    public int getAdoptedForBlockSize() {
        return adoptedForBlockSize;
    }

    // T-5: resumes the direction from Base exit.
    public void restoreOriginalDirection() {
        this.movementDirection = originalMovementDirection;
    }

    // T-14: Gamma permanently reverses both the active and original direction.
    public void reverseDirection() {
        MovementDirectionStrategy reversed = movementDirection.reverse();

        this.movementDirection = reversed;
        this.originalMovementDirection = reversed;
    }

    public void recordApproachPass() {
        approachPassCount++;
    }

    public void recordCapture() {
        captureCount++;
    }

    // T-12: assigned once, when teleported to Alpha.
    public void applyIndividualEffect(MovementEffect effect) {
        this.individualEffect = effect;
    }

    // T-12: records block size, so a different regrouping ignores it.
    public void applyBlockEffect(MovementEffect effect, int blockSize) {
        this.blockEffect = effect;
        this.blockEffectAssignedForBlockSize = blockSize;
    }

    // T-12: once per round; the 4-round effect eventually expires.
    public void tickIndividualEffect() {
        this.individualEffect = individualEffect.afterRoundElapses();
    }

    public void tickBlockEffect() {
        this.blockEffect = blockEffect.afterRoundElapses();
    }

    // T-13: assigned once, when teleported to Beta.
    public void applyRestriction(PieceRestrictionState restrictionState) {
        this.restrictionState = restrictionState;
    }

    // T-13: once per round; the Beta restriction eventually expires.
    public void tickRestriction() {
        this.restrictionState = restrictionState.afterRoundElapses();
    }

    // T-13: records this round's roll toward the consecutive-3 rule.
    public void recordRestrictionRoll(int rollValue) {
        this.restrictionState = restrictionState.afterRollRecorded(rollValue);
    }

    public void moveTo(int newTrackPosition) {
        this.location = PieceLocation.TRACK;
        this.trackPosition = newTrackPosition;
    }

    public void moveToHomeStraight(int newHomeStraightIndex) {
        this.location = PieceLocation.HOME_STRAIGHT;
        this.homeStraightIndex = newHomeStraightIndex;
    }

    public void moveHome() {
        this.location = PieceLocation.HOME;
    }

    // T-9/T-12/T-13: resets every field to its Base default.
    public void returnToBase() {
        this.location = PieceLocation.BASE;
        this.trackPosition = 0;
        this.homeStraightIndex = 0;
        this.approachPassCount = 0;
        this.captureCount = 0;

        this.movementDirection = null;
        this.originalMovementDirection = null;
        this.adoptedForBlockSize = 0;

        this.individualEffect = MovementEffect.none();
        this.blockEffect = MovementEffect.none();
        this.blockEffectAssignedForBlockSize = 0;

        this.restrictionState = NoRestrictionState.getInstance();
    }

    private void requireDirection(MovementDirectionStrategy direction) {
        if (direction == null) {
            throw new InvalidPieceStateException(this + " has no movement direction yet");
        }
    }

    private void requireLocation(PieceLocation requiredLocation) {
        if (location != requiredLocation) {
            throw new InvalidPieceStateException(
                    this + " is at " + location + ", not " + requiredLocation);
        }
    }

    // T-1/T-5: a throwaway copy with only what the home-entry rules read, so previews never touch this piece.
    public Piece copyForPreview(int extraApproachPasses) {
        Piece preview = new Piece(color, number);

        preview.movementDirection = originalMovementDirection;
        preview.originalMovementDirection = originalMovementDirection;
        preview.approachPassCount = approachPassCount + extraApproachPasses;
        preview.captureCount = captureCount;

        return preview;
    }

    @Override
    public String toString() {
        return PlayerColorNames.shortCodeOf(color) + number;
    }
}
