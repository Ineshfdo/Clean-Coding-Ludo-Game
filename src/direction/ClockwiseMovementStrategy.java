package direction;

import ludoboard.Board;
import ludoboard.PlayerColor;

// T-1: heads means clockwise - a shared instance so same-direction pieces compare equal.
public final class ClockwiseMovementStrategy implements MovementDirectionStrategy {

    private static final int REQUIRED_APPROACH_PASS_COUNT = 1;
    private static final String LABEL = "Clockwise";

    private static final ClockwiseMovementStrategy SHARED_INSTANCE = new ClockwiseMovementStrategy();

    private ClockwiseMovementStrategy() {
    }

    public static ClockwiseMovementStrategy getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int nextPosition(int currentPosition, int steps, Board board) {
        return board.getPositionAfterMoving(currentPosition, steps);
    }

    @Override
    public int stepsToApproach(int currentPosition, PlayerColor color, Board board) {
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
}
