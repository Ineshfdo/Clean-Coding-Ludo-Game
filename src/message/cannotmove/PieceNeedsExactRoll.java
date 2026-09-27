package message.cannotmove;

import message.GameMessage;

/**
 * A piece on the HomeStraight needs an exact roll to reach Home (rule 10).
 *
 * @param pieceLabel the name of the piece
 */
public record PieceNeedsExactRoll(String pieceLabel) implements GameMessage {
}
