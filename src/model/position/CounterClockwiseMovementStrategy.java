package model.position;

import model.board.Board;
import config.enums.PlayerColor;

// T-1: tails means counter-clockwise - passes Approach twice before Home, shared for equality.
public final class CounterClockwiseMovementStrategy implements MovementDirectionStrategy {

    private static final int REQUIRED_APPROACH_PASS_COUNT = 2;
    private static final String LABEL = "Counter-Clockwise";

    private static final CounterClockwiseMovementStrategy SHARED_INSTANCE =
            new CounterClockwiseMovementStrategy();

    private CounterClockwiseMovementStrategy() {
    }

    public static CounterClockwiseMovementStrategy getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int nextPosition(int currentPosition, int steps, Board board) {
        return board.getPositionAfterMovingBackward(currentPosition, steps);
    }

    @Override
    public int stepsToApproach(int currentPosition, PlayerColor color, Board board) {
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
