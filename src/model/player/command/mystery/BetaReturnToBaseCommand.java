package model.player.command.mystery;

import config.enums.CommandType;
import java.util.List;
import java.util.stream.Collectors;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

// T-13: consecutive 3s send a Beta-restricted piece/block back to Base.
public final class BetaReturnToBaseCommand implements Command {

    private final Player player;
    private final List<Piece> returningPieces;

    public BetaReturnToBaseCommand(Player player, List<Piece> returningPieces) {
        this.player = player;
        this.returningPieces = returningPieces;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        for (Piece piece : returningPieces) {
            player.returnToBase(piece);
        }

        messages.publish(GameMessage.betaRestrictionTriggered(describeLabel()));
    }

    private String describeLabel() {
        return returningPieces.stream().map(Piece::toString).collect(Collectors.joining("+"));
    }

    @Override
    public CommandType getType() {
        return CommandType.RETURN_TO_BASE;
    }

    @Override
    public Piece getAffectedPiece() {
        return returningPieces.get(0);
    }

    @Override
    public List<Piece> getAffectedPieces() {
        return returningPieces;
    }
}
