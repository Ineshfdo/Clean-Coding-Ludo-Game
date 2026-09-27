package message.move;

import config.enums.PlayerColor;
import message.GameMessage;

/**
 * A single piece moved. The event tells the number of steps, the direction and both cells
 * (requirement 2).
 *
 * @param playerColor the colour of the player
 * @param pieceLabel the name of the piece
 * @param fromPosition the track cell where the move started
 * @param newPosition the track cell where the piece landed
 * @param rollValue the number of steps that the piece moved
 * @param movementDirectionLabel the direction in which the piece travelled
 */
public record PieceMoved(
        PlayerColor playerColor, String pieceLabel, int fromPosition, int newPosition,
        int rollValue, String movementDirectionLabel) implements GameMessage {
}
