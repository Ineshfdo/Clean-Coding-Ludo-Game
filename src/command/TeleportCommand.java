package command;

import java.util.List;
import java.util.stream.Collectors;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import mysterycell.AlphaEffectRule;
import mysterycell.MysteryCellDestinationType;
import player.Piece;
import player.Player;

// T-11: teleports every piece that landed on the Mystery Cell to the randomly chosen destination.
public final class TeleportCommand implements Command {

    private static final int NO_TRACK_POSITION = -1;

    private final Player player;
    private final List<Piece> teleportedPieces;
    private final MysteryCellDestinationType destinationType;
    private final Board board;
    private final AlphaEffectRule alphaEffectRule;

    public TeleportCommand(
            Player player, List<Piece> teleportedPieces, MysteryCellDestinationType destinationType,
            Board board, AlphaEffectRule alphaEffectRule) {
        this.player = player;
        this.teleportedPieces = teleportedPieces;
        this.destinationType = destinationType;
        this.board = board;
        this.alphaEffectRule = alphaEffectRule;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        if (destinationType == MysteryCellDestinationType.BASE) {
            for (Piece piece : teleportedPieces) {
                player.returnToBase(piece);
            }
            messages.publish(GameMessage.pieceTeleported(
                    describeLabel(), destinationType.getLabel(), NO_TRACK_POSITION));
            return;
        }

        int targetPosition = resolveTrackPosition();
        for (Piece piece : teleportedPieces) {
            player.teleportTo(piece, targetPosition);
        }
        messages.publish(GameMessage.pieceTeleported(
                describeLabel(), destinationType.getLabel(), targetPosition));

        // T-12: only Alpha assigns an Energized/Sick effect.
        if (destinationType == MysteryCellDestinationType.ALPHA) {
            alphaEffectRule.applyTo(player, teleportedPieces, messages);
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
