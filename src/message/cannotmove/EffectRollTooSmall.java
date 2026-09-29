package message.cannotmove;

import message.GameMessage;

/**
A Sick effect cut the roll down to zero cells, so the piece cannot move (T-12).
@param pieceLabel the name of the piece
*/
public record EffectRollTooSmall(String pieceLabel) implements GameMessage {}