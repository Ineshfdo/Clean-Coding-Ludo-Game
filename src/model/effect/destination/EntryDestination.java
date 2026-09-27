package model.effect.destination;

import model.board.Board;
import model.effect.activation.EffectActivationRule;
import model.player.Player;

/**
 * The Entry destination (T-11). The pieces go to the Entry cell of their own colour, and arriving
 * there has no further effect.
 */
public final class EntryDestination extends TrackDestination {

    private static final String LABEL = "Entry";

    private final Board board;

    /**
     * Creates the Entry destination.
     *
     * @param board gives the Entry cell of each colour
     * @param effectActivationRule decides whether an effect may start
     */
    public EntryDestination(Board board, EffectActivationRule effectActivationRule) {
        super(effectActivationRule);
        this.board = board;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    protected int findCellPosition(Player player) {
        return board.getEntryCellPosition(player.getColor());
    }
}
