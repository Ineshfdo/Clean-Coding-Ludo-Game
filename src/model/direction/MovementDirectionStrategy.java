package model.direction;

import config.enums.PlayerColor;
import model.board.Board;

/**
 * Strategy for the two directions in which a piece can travel: clockwise and counter-clockwise. The
 * two directions move and measure the distance to the Approach cell in different ways, so the
 * movement code asks this interface and never checks the direction itself.
 */
public interface MovementDirectionStrategy {

    /**
     * Moves along the track in this direction.
     *
     * @param currentPosition the track position to start from
     * @param steps the number of steps to move
     * @param board the board that gives the track
     * @return the track position after the move
     */
    int nextPosition(int currentPosition, int steps, Board board);

    /**
     * Counts the steps from a track cell to the Approach cell of a colour, in this direction.
     *
     * @param currentPosition the track position to start from
     * @param color the colour whose Approach cell is the target
     * @param board the board that gives the track
     * @return the number of steps; 0 when the piece stands on the Approach cell
     */
    int countStepsToApproach(int currentPosition, PlayerColor color, Board board);

    /**
     * Gives the number of times a piece must pass its Approach cell before it may enter its
     * HomeStraight.
     *
     * @return one for clockwise, two for counter-clockwise
     */
    int getRequiredApproachPassCount();

    /**
     * Gives the text shown on the console.
     *
     * @return the name of the direction
     */
    String getLabel();

    /**
     * Lets callers check the direction without instanceof (T-14).
     *
     * @return true for clockwise, false for counter-clockwise
     */
    boolean isClockwise();

    /**
     * Gives the opposite direction. The Gamma cell uses it to reverse a piece or a blockade (T-14).
     *
     * @return the opposite direction
     */
    MovementDirectionStrategy reverse();
}
