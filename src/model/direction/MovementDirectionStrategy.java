package model.direction;

import config.enums.PlayerColor;
import model.board.Board;

// Strategy: clockwise and counter-clockwise pieces move and measure Approach distance differently.
public interface MovementDirectionStrategy {

    int nextPosition(int currentPosition, int steps, Board board);

    int countStepsToApproach(int currentPosition, PlayerColor color, Board board);

    int getRequiredApproachPassCount();

    String getLabel();

    // T-14: lets callers check the direction without instanceof.
    boolean isClockwise();

    // T-14: the opposite direction; Gamma uses it to reverse a piece or block.
    MovementDirectionStrategy reverse();
}
