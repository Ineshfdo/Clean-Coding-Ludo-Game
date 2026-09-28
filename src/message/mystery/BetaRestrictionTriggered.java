package message.mystery;

import message.GameMessage;

/**
 Two rolls of 3 in a row sent a Beta-restricted piece or blockade back to Base (T-13).
 @param pieceLabel the name of the piece, or the joined names of the blockade
 */
public record BetaRestrictionTriggered(String pieceLabel) implements GameMessage {
}
