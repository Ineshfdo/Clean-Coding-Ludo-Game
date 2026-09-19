package model.player.command.mysterycell;
import model.player.command.Command;
import config.enums.CommandType;

import java.util.List;
import java.util.stream.Collectors;

import service.result.GameMessage;
import view.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;

// T-13: sends a Beta-restricted piece/block to Base after two consecutive rounds of rolling a 3.
public final class ReturnToBaseCommand implements Command {

    private final Player player;
    private final List<Piece> returningPieces;

    public ReturnToBaseCommand(Player player, List<Piece> returningPieces) {
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
