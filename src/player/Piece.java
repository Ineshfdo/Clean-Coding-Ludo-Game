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
    private int approachPassCount;

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

    // T-1: set once at Base -> X by coin toss; drives this piece's path.
    public MovementDirectionStrategy getMovementDirectionStrategy() {
        return movementDirectionStrategy;
    }

    // T-1: how many times this piece has passed its own Approach cell so far.
    public int getApproachPassCount() {
        return approachPassCount;
    }

    void leaveBase(int entryCellPosition) {
        this.location = PieceLocation.TRACK;
        this.trackPosition = entryCellPosition;
        this.approachPassCount = 0;
    }

    void assignMovementDirection(MovementDirectionStrategy movementDirectionStrategy) {
        this.movementDirectionStrategy = movementDirectionStrategy;
    }

    void recordApproachPass() {
        approachPassCount++;
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

    void returnToBase() {
        this.location = PieceLocation.BASE;
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
