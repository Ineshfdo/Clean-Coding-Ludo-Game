package model.player.command.mystery;

import config.constant.BoardConstants;
import config.enums.CommandType;
import config.enums.MysteryCellDestinationType;
import java.util.List;
import java.util.stream.Collectors;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.effect.activation.MysteryCellArrival;
import model.effect.mysterycell.MysteryCellDestinationLabels;
import model.effect.restriction.BetaRestrictedState;
import model.effect.rule.MysteryCellEffects;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

// T-11: teleports pieces from the Mystery Cell to a random destination.
public final class MysteryCellTeleportCommand implements Command {

    private final Player player;
    private final List<Piece> teleportedPieces;

    private final MysteryCellDestinationType destinationType;
    private final Board board;
    private final MysteryCellEffects effects;

    public MysteryCellTeleportCommand(
        Player player, List<Piece> teleportedPieces, MysteryCellDestinationType destinationType,
        Board board, MysteryCellEffects effects) {
        this.player = player;
        this.teleportedPieces = teleportedPieces;
        this.destinationType = destinationType;
        this.board = board;
        this.effects = effects;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        if (destinationType == MysteryCellDestinationType.BASE) {
            for (Piece piece : teleportedPieces) {
                player.returnToBase(piece);
            }

            messages.publish(GameMessage.pieceTeleported(
                describeLabel(), MysteryCellDestinationLabels.labelOf(destinationType),
                BoardConstants.NO_TRACK_POSITION));
            return;
        }

        int targetPosition = resolveTrackPosition();

        for (Piece piece : teleportedPieces) {
            player.teleportTo(piece, targetPosition);
        }

        // T-1: arriving on Approach counts as a pass, so the next move can enter HomeStraight.
        if (destinationType == MysteryCellDestinationType.APPROACH) {
            for (Piece piece : teleportedPieces) {
                player.recordApproachPass(piece);
            }
        }

        messages.publish(GameMessage.pieceTeleported(
            describeLabel(), MysteryCellDestinationLabels.labelOf(destinationType), targetPosition
        ));

        // T-15: effects activate only after a genuine teleport.
        boolean effectPermitted = effects.getEffectActivationRule()
            .permitsActivation(new MysteryCellArrival(destinationType));

        if (!effectPermitted) {
            return;
        }

        // T-12: only Alpha assigns Energized/Sick.
        if (destinationType == MysteryCellDestinationType.ALPHA) {
            effects.getAlphaEffectRule().applyTo(player, teleportedPieces, messages);
        }

        // T-13: only Beta restricts movement.
        if (destinationType == MysteryCellDestinationType.BETA) {
            BetaRestrictedState restriction = new BetaRestrictedState();

            for (Piece piece : teleportedPieces) {
                player.applyRestriction(piece, restriction);
            }

            messages.publish(GameMessage.betaRestrictionApplied(describeLabel()));
        }

        // T-14: only Gamma reverses direction (or forwards to Beta).
        if (destinationType == MysteryCellDestinationType.GAMMA) {
            effects.getGammaDirectionRule().applyTo(player, teleportedPieces, messages, effects);
        }
    }

    private int resolveTrackPosition() {
        return switch (destinationType) {
            case ALPHA -> board.getAlphaCellPosition();
            case BETA -> board.getBetaCellPosition();
            case GAMMA -> board.getGammaCellPosition();
            case ENTRY -> board.getEntryCellPosition(player.getColor());
            case APPROACH -> board.getApproachCellPosition(player.getColor());
            case BASE -> throw new IllegalStateException("Base has no track position");
        };
    }

    private String describeLabel() {
        return teleportedPieces.stream().map(Piece::toString).collect(Collectors.joining("+"));
    }

    @Override
    public CommandType getType() {
        return CommandType.MOVE_FORWARD;
    }

    @Override
    public Piece getAffectedPiece() {
        return teleportedPieces.get(0);
    }

    @Override
    public List<Piece> getAffectedPieces() {
        return teleportedPieces;
    }
}
