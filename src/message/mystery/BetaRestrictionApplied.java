package message.mystery;

import message.GameMessage;

/**
 A teleported piece or blockade cannot move for four rounds (T-13).
 @param pieceLabel the name of the piece, or the joined names of the blockade
 */
public record BetaRestrictionApplied(String pieceLabel) implements GameMessage {
}
