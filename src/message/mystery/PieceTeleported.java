package message.mystery;

import message.GameMessage;

/**
 A piece or a blockade landed on the Mystery Cell and was teleported to a random destination (T-11).
 @param pieceLabel the name of the piece, or the joined names of the blockade
 @param destinationLabel the name of the destination, for example Alpha
 @param newPosition the track cell that was reached, or -1 when the destination is Base
 */
public record PieceTeleported(
        String pieceLabel, String destinationLabel, int newPosition) implements GameMessage {
}
