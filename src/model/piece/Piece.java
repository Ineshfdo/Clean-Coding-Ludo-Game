package model.piece;

import config.enums.PieceLocation;
import config.enums.PlayerColor;
import model.effect.movement.MovementEffect;
import model.effect.restriction.NoRestrictionState;
import model.effect.restriction.PieceRestrictionState;
import model.player.PlayerColorLabels;
import model.position.MovementDirectionStrategy;

// Rule 6: a piece travels Base -> track -> HomeStraight -> Home, then stops.
public final class Piece {

    private final PlayerColor color;
    private final int pieceNumber;

    private PieceLocation location;
    private int trackPosition;
    private int homeStraightIndex;

    private MovementDirectionStrategy movementDirectionStrategy;
    private MovementDirectionStrategy originalMovementDirectionStrategy;
    private int adoptedForBlockSize;

    private int approachPassCount;
    private int captureCount;

    private MovementEffect individualEffect = MovementEffect.none();
    private MovementEffect blockEffect = MovementEffect.none();
    private int blockEffectAssignedForBlockSize;

    private PieceRestrictionState restrictionState = NoRestrictionState.getInstance();

    public Piece(PlayerColor color, int pieceNumber) {
        this.color = color;
        this.pieceNumber = pieceNumber;
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
    public MovementDirectionStrategy getMovementDirectionStrategy() {
        return movementDirectionStrategy;
    }

    // T-5: direction assigned at Base exit, unaffected by blocks.
    public MovementDirectionStrategy getOriginalMovementDirectionStrategy() {
        return originalMovementDirectionStrategy;
    }

    // T-5: true while a block's direction replaces its own.
    public boolean hasAdoptedBlockDirection() {
        return movementDirectionStrategy != originalMovementDirectionStrategy;
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

    public void assignMovementDirection(MovementDirectionStrategy movementDirectionStrategy) {
        this.movementDirectionStrategy = movementDirectionStrategy;
        this.originalMovementDirectionStrategy = movementDirectionStrategy;
    }

    // T-4/T-5: borrows a block's direction while grouped, remembering the block size.
    public void adoptBlockDirection(MovementDirectionStrategy blockDirection, int blockSize) {
        this.movementDirectionStrategy = blockDirection;
        this.adoptedForBlockSize = blockSize;
    }

    public int getAdoptedForBlockSize() {
        return adoptedForBlockSize;
    }

    // T-5: resumes the direction from Base exit.
    public void restoreOriginalDirection() {
        this.movementDirectionStrategy = originalMovementDirectionStrategy;
    }

    // T-14: Gamma permanently reverses both the active and original direction.
    public void reverseDirection() {
        MovementDirectionStrategy reversed = movementDirectionStrategy.reverse();

        this.movementDirectionStrategy = reversed;
        this.originalMovementDirectionStrategy = reversed;
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

        this.movementDirectionStrategy = null;
        this.originalMovementDirectionStrategy = null;
        this.adoptedForBlockSize = 0;

        this.individualEffect = MovementEffect.none();
        this.blockEffect = MovementEffect.none();
        this.blockEffectAssignedForBlockSize = 0;

        this.restrictionState = NoRestrictionState.getInstance();
    }

    private void requireLocation(PieceLocation requiredLocation) {
        if (location != requiredLocation) {
            throw new IllegalStateException(this + " is not on " + requiredLocation);
        }
    }

    @Override
    public String toString() {
        return PlayerColorLabels.shortCodeOf(color) + pieceNumber;
    }
}
