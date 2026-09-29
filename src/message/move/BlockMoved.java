package message.move;

import message.GameMessage;

/**
A blockade moved.
The event tells its direction, its type and both cells (T-4, T-13).
@param blockLabel the joined names of the pieces of the blockade
@param fromPosition the track cell where the move started
@param newPosition the track cell where the blockade landed
@param blockTypeLabel the type of the blockade: Same-Direction or Opposite-Direction
@param movementDirectionLabel the direction in which the blockade travelled
*/
public record BlockMoved(String blockLabel, int fromPosition, int newPosition, String blockTypeLabel,String movementDirectionLabel) implements GameMessage {}