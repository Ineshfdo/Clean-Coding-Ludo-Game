package model.piece;

import config.enums.PieceLocation;
import config.enums.PlayerColor;
import exception.InvalidPieceStateException;
import model.direction.MovementDirectionStrategy;
import model.effect.movement.MovementEffect;
import model.effect.restriction.NoRestrictionState;
import model.effect.restriction.PieceRestrictionState;
import utils.color.PlayerColorNames;

/**
 * One game piece. A piece travels from Base along the shared track, then along the HomeStraight of
 * its colour, and stops at Home (rule 6). It stores its location, direction, captures, Approach
 * passes, movement effects and restriction.
 *
 * The methods that change a piece are public, but only Player should call them, so that the owner
 * of the piece is always checked.
 */
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

    /**
     * Creates a piece at Base.
     *
     * @param color the colour of the owner
     * @param number the number of the piece, counted from 1
     */
    public Piece(PlayerColor color, int number) {
        this.color = color;
        this.number = number;
        this.location = PieceLocation.BASE;
    }

    /**
     * Gives the colour of the piece.
     *
     * @return the colour of the owner
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * Tells whether the piece is at Base.
     *
     * @return true at Base
     */
    public boolean isAtBase() {
        return location == PieceLocation.BASE;
    }

    /**
     * Tells whether the piece is on the shared track.
     *
     * @return true on the track
     */
    public boolean isOnTrack() {
        return location == PieceLocation.TRACK;
    }

    /**
     * Tells whether the piece is on its HomeStraight.
     *
     * @return true on the HomeStraight
     */
    public boolean isOnHomeStraight() {
        return location == PieceLocation.HOME_STRAIGHT;
    }

    /**
     * Tells whether the piece has reached Home.
     *
     * @return true at Home
     */
    public boolean isHome() {
        return location == PieceLocation.HOME;
    }

    /**
     * Gives the track cell of the piece.
     *
     * @return the track position
     * @throws InvalidPieceStateException if the piece is not on the track
     */
    public int getTrackPosition() {
        requireLocation(PieceLocation.TRACK);

        return trackPosition;
    }

    /**
     * Gives the HomeStraight cell of the piece.
     *
     * @return the index of the cell, counted from the Approach cell
     * @throws InvalidPieceStateException if the piece is not on its HomeStraight
     */
    public int getHomeStraightIndex() {
        requireLocation(PieceLocation.HOME_STRAIGHT);

        return homeStraightIndex;
    }

    /**
     * Gives the direction in which the piece moves now: its own direction, or the direction of its
     * blockade while it is grouped (T-1, T-5).
     *
     * @return the current direction
     * @throws InvalidPieceStateException if the piece has no direction yet
     */
    public MovementDirectionStrategy getMovementDirection() {
        requireDirection(movementDirection);

        return movementDirection;
    }

    /**
     * Tells whether the piece has had its first coin toss.
     *
     * @return false only while the piece is at Base, before its first coin toss
     */
    public boolean hasMovementDirection() {
        return movementDirection != null;
    }

    /**
     * Gives the direction that the piece got when it left Base. Blockades do not change it (T-5).
     *
     * @return the original direction
     * @throws InvalidPieceStateException if the piece has no direction yet
     */
    public MovementDirectionStrategy getOriginalMovementDirection() {
        requireDirection(originalMovementDirection);

        return originalMovementDirection;
    }

    /**
     * Tells whether the direction of a blockade replaces the own direction of the piece (T-5).
     *
     * @return true while the piece travels in the direction of its blockade
     */
    public boolean hasAdoptedBlockDirection() {
        return movementDirection != originalMovementDirection;
    }

    /**
     * Gives the number of times the piece has passed its Approach cell (T-1).
     *
     * @return the number of passes
     */
    public int getApproachPassCount() {
        return approachPassCount;
    }

    /**
     * Gives the number of opponent pieces that this piece has captured (T-7).
     *
     * @return the number of captures
     */
    public int getCaptureCount() {
        return captureCount;
    }

    /**
     * Gives the own Energized or Sick effect of the piece. It is used when the piece moves alone
     * (T-12).
     *
     * @return the individual effect; the none effect when there is no effect
     */
    public MovementEffect getIndividualEffect() {
        return individualEffect;
    }

    /**
     * Gives the shared effect of the blockade of the piece. Check {@link
     * #hasActiveBlockEffectForSize(int)} first (T-12).
     *
     * @return the blockade effect; the none effect when there is no effect
     */
    public MovementEffect getBlockEffect() {
        return blockEffect;
    }

    /**
     * Tells whether the blockade effect belongs to a blockade of the given size (T-12).
     *
     * @param currentBlockSize the size of the blockade that the piece is in now
     * @return true only when the effect is active and was given to a blockade of this size
     */
    public boolean hasActiveBlockEffectForSize(int currentBlockSize) {
        return blockEffect.isActive() && blockEffectAssignedForBlockSize == currentBlockSize;
    }

    /**
     * Gives the current movement restriction of the piece, for example the Beta restriction (T-13).
     *
     * @return the restriction state; the free state when nothing restricts the piece
     */
    public PieceRestrictionState getRestrictionState() {
        return restrictionState;
    }

    /**
     * Puts the piece on the track at its Entry cell and resets its Approach passes.
     *
     * @param entryCellPosition the track position of the Entry cell
     */
    public void leaveBase(int entryCellPosition) {
        this.location = PieceLocation.TRACK;
        this.trackPosition = entryCellPosition;
        this.approachPassCount = 0;
    }

    /**
     * Sets the direction of the piece. The current and the original direction become the same.
     *
     * @param movementDirection the direction given by the coin toss
     */
    public void assignMovementDirection(MovementDirectionStrategy movementDirection) {
        this.movementDirection = movementDirection;
        this.originalMovementDirection = movementDirection;
    }

    /**
     * Uses the direction of a blockade while the piece is grouped, and remembers the size of the
     * blockade (T-4, T-5).
     *
     * @param blockDirection the direction of the blockade
     * @param blockSize the number of pieces in the blockade
     */
    public void adoptBlockDirection(MovementDirectionStrategy blockDirection, int blockSize) {
        this.movementDirection = blockDirection;
        this.adoptedForBlockSize = blockSize;
    }

    /**
     * Gives the size of the blockade whose direction the piece uses.
     *
     * @return the size of that blockade; 0 when the piece has not adopted a blockade direction
     */
    public int getAdoptedForBlockSize() {
        return adoptedForBlockSize;
    }

    /**
     * Goes back to the direction that the piece got when it left Base (T-5).
     */
    public void restoreOriginalDirection() {
        this.movementDirection = originalMovementDirection;
    }

    /**
     * Reverses the direction for good. The current and the original direction both change, after a
     * Gamma teleport (T-14).
     */
    public void reverseDirection() {
        MovementDirectionStrategy reversed = movementDirection.reverse();

        this.movementDirection = reversed;
        this.originalMovementDirection = reversed;
    }

    /**
     * Adds one Approach pass.
     */
    public void recordApproachPass() {
        approachPassCount++;
    }

    /**
     * Adds one capture.
     */
    public void recordCapture() {
        captureCount++;
    }

    /**
     * Sets the own effect of the piece. It is given once, when the piece is teleported to Alpha
     * (T-12).
     *
     * @param effect the Energized or Sick effect
     */
    public void applyIndividualEffect(MovementEffect effect) {
        this.individualEffect = effect;
    }

    /**
     * Sets the shared effect of a blockade and records the size of the blockade, so that a
     * different regrouping ignores it (T-12).
     *
     * @param effect the Energized or Sick effect of the blockade
     * @param blockSize the number of pieces in the blockade
     */
    public void applyBlockEffect(MovementEffect effect, int blockSize) {
        this.blockEffect = effect;
        this.blockEffectAssignedForBlockSize = blockSize;
    }

    /**
     * Uses up one round of the own effect, so that an effect of four rounds expires in the end
     * (T-12).
     */
    public void tickIndividualEffect() {
        this.individualEffect = individualEffect.afterRoundElapses();
    }

    /**
     * Uses up one round of the blockade effect (T-12).
     */
    public void tickBlockEffect() {
        this.blockEffect = blockEffect.afterRoundElapses();
    }

    /**
     * Sets the movement restriction. It is given once, when the piece is teleported to Beta (T-13).
     *
     * @param restrictionState the restriction to apply
     */
    public void applyRestriction(PieceRestrictionState restrictionState) {
        this.restrictionState = restrictionState;
    }

    /**
     * Uses up one round of the restriction. The Beta restriction expires after four rounds (T-13).
     */
    public void tickRestriction() {
        this.restrictionState = restrictionState.afterRoundElapses();
    }

    /**
     * Records the first roll of this turn for the rule of two consecutive rolls of 3 (T-13).
     *
     * @param rollValue the value of the roll
     */
    public void recordRestrictionRoll(int rollValue) {
        this.restrictionState = restrictionState.afterRollRecorded(rollValue);
    }

    /**
     * Puts the piece on a track cell.
     *
     * @param newTrackPosition the track position to move to
     */
    public void moveTo(int newTrackPosition) {
        this.location = PieceLocation.TRACK;
        this.trackPosition = newTrackPosition;
    }

    /**
     * Puts the piece on a HomeStraight cell.
     *
     * @param newHomeStraightIndex the index of the cell, counted from the Approach cell
     */
    public void moveToHomeStraight(int newHomeStraightIndex) {
        this.location = PieceLocation.HOME_STRAIGHT;
        this.homeStraightIndex = newHomeStraightIndex;
    }

    /**
     * Sends the piece Home. A piece that is Home does not move again.
     */
    public void moveHome() {
        this.location = PieceLocation.HOME;
    }

    /**
     * Sends the piece back to Base and resets every stored value to its Base default (T-9, T-12,
     * T-13).
     */
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

    /**
     * Makes a throwaway copy with only the values that the home-entry rules read, so that a preview
     * never changes this piece (T-1, T-5).
     *
     * @param extraApproachPasses the number of Approach passes to add to the copy
     * @return the copy
     */
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
