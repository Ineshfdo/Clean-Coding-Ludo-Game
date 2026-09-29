package message.move;

import message.GameMessage;

/**
A piece or a blockade reached Home and is removed from play.
@param pieceLabel the name of the piece, or the joined names of the blockade
*/
public record PieceReachedHome(String pieceLabel) implements GameMessage {}
