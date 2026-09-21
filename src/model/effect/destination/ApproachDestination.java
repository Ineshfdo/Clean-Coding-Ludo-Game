package model.effect.destination;

import java.util.List;
import model.board.Board;
import model.effect.activation.EffectActivationRule;
import model.piece.Piece;
import model.player.Player;

// T-11: the player's own Approach cell.
public final class ApproachDestination extends TrackDestination {

    private static final String LABEL = "Approach";

    private final Board board;

    public ApproachDestination(Board board, EffectActivationRule effectActivationRule) {
        super(effectActivationRule);
        this.board = board;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    protected int findCellPosition(Player player) {
        return board.getApproachCellPosition(player.getColor());
    }

    // T-1: arriving on Approach counts as a pass, so the next move can enter HomeStraight.
    @Override
    protected void recordArrival(Player player, List<Piece> teleportedPieces) {
        for (Piece piece : teleportedPieces) {
            player.recordApproachPass(piece);
        }
    }
}
