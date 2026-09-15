package command;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import player.Piece;

// Rule 10: announces a HomeStraight piece needs an exact roll to reach Home.
public final class ExactRollRequiredCommand implements Command {

    private final Piece piece;

    public ExactRollRequiredCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        messages.publish(GameMessage.pieceNeedsExactRoll(piece.toString()));
    }

    @Override
    public CommandType getType() {
        return CommandType.BLOCKED;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
