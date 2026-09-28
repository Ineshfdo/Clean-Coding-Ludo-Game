package model.direction;

import config.enums.PlayerColor;
import model.board.Board;

/**
 Clockwise movement, given by a heads coin toss (T-1).
 A clockwise piece must pass its Approach cell once before it enters its HomeStraight.
 The class is a Singleton, so all clockwise pieces share one object.
 */
public final class ClockwiseMovementStrategy implements MovementDirectionStrategy {

    private static final int REQUIRED_APPROACH_PASS_COUNT = 1;
    private static final String LABEL = "Clockwise";

    private static final ClockwiseMovementStrategy SHARED_INSTANCE = new ClockwiseMovementStrategy();

    private ClockwiseMovementStrategy() {
    }

    /**
     Gives the one shared clockwise direction.
     @return the shared instance
     */
    public static ClockwiseMovementStrategy getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int nextPosition(int currentPosition, int steps, Board board) {
        return board.getPositionAfterMoving(currentPosition, steps);
    }

    @Override
    public int countStepsToApproach(int currentPosition, PlayerColor color, Board board) {
        return board.getForwardDistance(currentPosition, board.getApproachCellPosition(color));
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
        return true;
    }

    @Override
    public MovementDirectionStrategy reverse() {
        return CounterClockwiseMovementStrategy.getInstance();
    }
}
