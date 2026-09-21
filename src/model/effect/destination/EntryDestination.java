package model.effect.destination;

import model.board.Board;
import model.effect.activation.EffectActivationRule;
import model.player.Player;

// T-11: the player's own Entry cell; arriving there has no further effect.
public final class EntryDestination extends TrackDestination {

    private static final String LABEL = "Entry";

    private final Board board;

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
