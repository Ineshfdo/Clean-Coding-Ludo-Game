package model.player.action;
import config.enums.CommandType;

import java.util.List;
import java.util.stream.Collectors;

import config.constant.BoardConstants;
import service.result.GameMessage;
import view.observer.GameMessagePublisher;
import model.board.Board;
import model.effect.MysteryCellArrival;
import config.enums.MysteryCellDestinationType;
import model.effect.MysteryCellDestinationLabels;
import model.effect.MysteryCellEffects;
import model.piece.BetaRestrictedState;
import model.piece.Piece;
import model.player.Player;

// T-11: teleports every piece that landed on the Mystery Cell to the randomly chosen destination.
public final class TeleportCommand implements Command {

    private final Player player;
    private final List<Piece> teleportedPieces;
    private final MysteryCellDestinationType destinationType;
    private final Board board;
    private final MysteryCellEffects effects;

    public TeleportCommand(
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
        messages.publish(GameMessage.pieceTeleported(
                describeLabel(), MysteryCellDestinationLabels.labelOf(destinationType), targetPosition));

        // T-15: Alpha/Beta/Gamma effects activate only after this validated, genuine teleport.
        boolean effectPermitted = effects.getEffectActivationRule()
                .permitsActivation(new MysteryCellArrival(destinationType));
        if (!effectPermitted) {
            return;
        }

        // T-12: only Alpha assigns an Energized/Sick effect.
        if (destinationType == MysteryCellDestinationType.ALPHA) {
            effects.getAlphaEffectRule().applyTo(player, teleportedPieces, messages);
        }

        // T-13: only Beta restricts movement and starts the consecutive-3 tracking.
        if (destinationType == MysteryCellDestinationType.BETA) {
            BetaRestrictedState restriction = new BetaRestrictedState();
            for (Piece piece : teleportedPieces) {
                player.applyRestriction(piece, restriction);
            }
            messages.publish(GameMessage.betaRestrictionApplied(describeLabel()));
        }

        // T-14: only Gamma reverses direction, or forwards on to Beta if already reversed.
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
