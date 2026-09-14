package direction;

import ludoboard.Board;
import ludoboard.PlayerColor;

// Strategy: clockwise and counter-clockwise pieces compute their
// next track cell and Approach distance differently.
public interface MovementDirectionStrategy {

    int nextPosition(int currentPosition, int steps, Board board);

    int stepsToApproach(int currentPosition, PlayerColor color, Board board);

    int getRequiredApproachPassCount();

    String getLabel();
}
