package model.piece;
import config.enums.PieceLocation;
import model.effect.MovementEffect;

import model.player.PlayerColorLabels;
import model.position.MovementDirectionStrategy;
import config.enums.PlayerColor;

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

    // T-1/T-5: the direction currently driving this piece - its own, or a block's while grouped.
    public MovementDirectionStrategy getMovementDirectionStrategy() {
        return movementDirectionStrategy;
    }

    // T-5: the direction assigned at Base exit, unaffected by any block it later joins.
    public MovementDirectionStrategy getOriginalMovementDirectionStrategy() {
        return originalMovementDirectionStrategy;
    }

    // T-5: true once a block's direction has replaced this piece's own, pending restoration.
    public boolean hasAdoptedBlockDirection() {
        return movementDirectionStrategy != originalMovementDirectionStrategy;
    }

    // T-1: how many times this piece has passed its own Approach cell so far.
    public int getApproachPassCount() {
        return approachPassCount;
    }

    // T-7: how many opponent pieces this piece itself has captured.
    public int getCaptureCount() {
        return captureCount;
    }

    // T-12: this piece's own Energized/Sick status, used only while it moves alone.
    public MovementEffect getIndividualEffect() {
        return individualEffect;
    }

    // T-12: the shared Energized/Sick status of the block this piece was teleported into -
    // only meaningful while hasActiveBlockEffectForSize() confirms it still matches the
    // CURRENT block; use that to decide whether to trust this value.
    public MovementEffect getBlockEffect() {
        return blockEffect;
    }

    // T-12: true only if this piece's stored block effect was genuinely assigned to a group
    // of exactly this size. Guards against a stale effect leaking into a later, unrelated
    // regrouping of the same size - the same size-membership check T-4/T-5 already use for
    // adoptedForBlockSize, applied here to the effect instead of the direction.
    public boolean hasActiveBlockEffectForSize(int currentBlockSize) {
        return blockEffect.isActive() && blockEffectAssignedForBlockSize == currentBlockSize;
    }

    // T-13: this piece's current movement-restriction state, e.g. Beta-restricted after a teleport.
    public PieceRestrictionState getRestrictionState() {
        return restrictionState;
    }

    // Mutators below are called only by Player (the owning aggregate) - kept public because
    // model.player is a separate package from model.piece, but every call site outside this
    // class still goes exclusively through Player's own API, never directly from elsewhere.
    public void leaveBase(int entryCellPosition) {
        this.location = PieceLocation.TRACK;
        this.trackPosition = entryCellPosition;
        this.approachPassCount = 0;
    }

    public void assignMovementDirection(MovementDirectionStrategy movementDirectionStrategy) {
        this.movementDirectionStrategy = movementDirectionStrategy;
        this.originalMovementDirectionStrategy = movementDirectionStrategy;
    }

    // T-4/T-5: temporarily borrows a block's shared direction while grouped with teammates,
    // and records how many members the block had when that direction was decided - so a
    // later arrival changing the block's size can be detected and re-compared fairly,
    // without recalculating on every ordinary round where membership hasn't changed.
    public void adoptBlockDirection(MovementDirectionStrategy blockDirection, int blockSize) {
        this.movementDirectionStrategy = blockDirection;
        this.adoptedForBlockSize = blockSize;
    }

    public int getAdoptedForBlockSize() {
        return adoptedForBlockSize;
    }

    // T-5: resumes the direction assigned when this piece left Base.
    public void restoreOriginalDirection() {
        this.movementDirectionStrategy = originalMovementDirectionStrategy;
    }

    // T-14: a Gamma teleport permanently reverses direction - unlike T-5's temporary
    // block-borrowed direction, both the active AND original direction are replaced.
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

    // T-12: assigned once, at the moment this piece is teleported to Alpha.
    public void applyIndividualEffect(MovementEffect effect) {
        this.individualEffect = effect;
    }

    // T-12: records which block size this effect was assigned to, so a later regrouping of a
    // different size (or different members) can tell it no longer applies.
    public void applyBlockEffect(MovementEffect effect, int blockSize) {
        this.blockEffect = effect;
        this.blockEffectAssignedForBlockSize = blockSize;
    }

    // T-12: called once per round so a 4-round effect eventually expires back to None.
    public void tickIndividualEffect() {
        this.individualEffect = individualEffect.afterRoundElapses();
    }

    public void tickBlockEffect() {
        this.blockEffect = blockEffect.afterRoundElapses();
    }

    // T-13: assigned once, at the moment this piece is teleported to Beta.
    public void applyRestriction(PieceRestrictionState restrictionState) {
        this.restrictionState = restrictionState;
    }

    // T-13: called once per round so a 4-round Beta restriction eventually expires.
    public void tickRestriction() {
        this.restrictionState = restrictionState.afterRoundElapses();
    }

    // T-13: records this round's roll toward the Beta restriction's consecutive-3 condition.
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

    // T-9/T-12/T-13: every field returns to its Base default, including any temporary
    // movement effect or Beta restriction.
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
