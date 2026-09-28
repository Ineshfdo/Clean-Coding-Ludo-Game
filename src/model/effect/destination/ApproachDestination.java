package model.effect.destination;

import java.util.List;
import model.board.Board;
import model.effect.activation.EffectActivationRule;
import model.piece.Piece;
import model.player.Player;

/**
 The Approach destination (T-11).
 The pieces go to the Approach cell of their own colour, and arriving there counts as an Approach pass (T-1).
 */
public final class ApproachDestination extends TrackDestination {

    private static final String LABEL = "Approach";

    private final Board board;

    /**
     Creates the Approach destination.
     @param board gives the Approach cell of each colour
     @param effectActivationRule decides whether an effect may start
     */
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
