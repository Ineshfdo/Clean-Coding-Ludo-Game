package model.direction;

import config.enums.PlayerColor;
import model.board.Board;

/**
 * Counter-clockwise movement, given by a tails coin toss (T-1). A counter-clockwise piece must pass
 * its Approach cell twice before it enters its HomeStraight. The class is a Singleton, so all
 * counter-clockwise pieces share one object.
 */
public final class CounterClockwiseMovementStrategy implements MovementDirectionStrategy {

    private static final int REQUIRED_APPROACH_PASS_COUNT = 2;
    private static final String LABEL = "Counter-Clockwise";

    private static final CounterClockwiseMovementStrategy SHARED_INSTANCE =
            new CounterClockwiseMovementStrategy();

    private CounterClockwiseMovementStrategy() {
    }

    /**
     * Gives the one shared counter-clockwise direction.
     *
     * @return the shared instance
     */
    public static CounterClockwiseMovementStrategy getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int nextPosition(int currentPosition, int steps, Board board) {
        return board.getPositionAfterMovingBackward(currentPosition, steps);
    }

    @Override
    public int countStepsToApproach(int currentPosition, PlayerColor color, Board board) {
        return board.getForwardDistance(board.getApproachCellPosition(color), currentPosition);
    }

    @Override
    public int getRequiredApproachPassCount() {
        return REQUIRED_APPROACH_PASS_COUNT;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public boolean isClockwise() {
        return false;
    }

    @Override
    public MovementDirectionStrategy reverse() {
        return ClockwiseMovementStrategy.getInstance();
    }
}
