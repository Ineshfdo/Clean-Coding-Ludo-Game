package command;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import player.Piece;
import player.Player;

// T-5: separates a piece from its block, restores its own direction, then moves it.
public final class BreakBlockCommand implements Command {

    private final Player player;
    private final Piece piece;
    private final Command moveCommand;

    public BreakBlockCommand(Player player, Piece piece, Command moveCommand) {
        this.player = player;
        this.piece = piece;
        this.moveCommand = moveCommand;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        player.restoreOriginalDirection(piece);
        messages.publish(GameMessage.pieceLeftBlock(piece.toString()));
        moveCommand.execute(messages);
    }

    @Override
    public CommandType getType() {
        return moveCommand.getType();
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
