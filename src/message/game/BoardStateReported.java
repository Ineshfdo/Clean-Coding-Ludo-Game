package message.game;

import message.GameMessage;

/**
The round is over.
The board state is reported: where every piece stands.
@param roundNumber the number of the round that has ended
*/
public record BoardStateReported(int roundNumber) implements GameMessage {}