package player;

import direction.MovementDirectionStrategy;
import ludoboard.PlayerColor;

// Rule 6: a piece travels Base -> track -> HomeStraight -> Home, then stops.
public final class Piece {

    private final PlayerColor color;
    private final int pieceNumber;

    private PieceLocation location;
    private int trackPosition;
    private int homeStraightIndex;
    private MovementDirectionStrategy movementDirectionStrategy;
    private MovementDirectionStrategy originalMovementDirectionStrategy;
    private int approachPassCount;
    private int captureCount;

    Piece(PlayerColor color, int pieceNumber) {
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

    void leaveBase(int entryCellPosition) {
        this.location = PieceLocation.TRACK;
        this.trackPosition = entryCellPosition;
        this.approachPassCount = 0;
    }

    void assignMovementDirection(MovementDirectionStrategy movementDirectionStrategy) {
        this.movementDirectionStrategy = movementDirectionStrategy;
        this.originalMovementDirectionStrategy = movementDirectionStrategy;
    }

    // T-5: temporarily borrows a block's shared direction while grouped with teammates.
    void adoptBlockDirection(MovementDirectionStrategy blockDirection) {
        this.movementDirectionStrategy = blockDirection;
    }

    // T-5: resumes the direction assigned when this piece left Base.
    void restoreOriginalDirection() {
        this.movementDirectionStrategy = originalMovementDirectionStrategy;
    }

    void recordApproachPass() {
        approachPassCount++;
    }

    void recordCapture() {
        captureCount++;
    }

    void moveTo(int newTrackPosition) {
        this.location = PieceLocation.TRACK;
        this.trackPosition = newTrackPosition;
    }

    void moveToHomeStraight(int newHomeStraightIndex) {
        this.location = PieceLocation.HOME_STRAIGHT;
        this.homeStraightIndex = newHomeStraightIndex;
    }

    void moveHome() {
        this.location = PieceLocation.HOME;
    }

    // T-9: every field returns to its Base default; a future temporary effect resets here too.
    void returnToBase() {
        this.location = PieceLocation.BASE;
        this.trackPosition = 0;
        this.homeStraightIndex = 0;
        this.approachPassCount = 0;
        this.captureCount = 0;
        this.movementDirectionStrategy = null;
        this.originalMovementDirectionStrategy = null;
    }

    private void requireLocation(PieceLocation requiredLocation) {
        if (location != requiredLocation) {
            throw new IllegalStateException(this + " is not on " + requiredLocation);
        }
    }

    @Override
    public String toString() {
        return color.getShortCode() + pieceNumber;
    }
}
