package model.effect.destination;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.board.MysteryCellPositions;
import model.effect.activation.EffectActivationRule;
import model.effect.rule.AlphaEffectRule;
import model.piece.Piece;
import model.player.Player;

// T-12: Alpha gives the teleported pieces an Energized or Sick status.
public final class AlphaDestination extends TrackDestination {

    private static final String LABEL = "Alpha";

    private final MysteryCellPositions positions;
    private final AlphaEffectRule alphaEffectRule;

    public AlphaDestination(
            MysteryCellPositions positions, EffectActivationRule effectActivationRule,
            AlphaEffectRule alphaEffectRule) {
        super(effectActivationRule);
        this.positions = positions;
        this.alphaEffectRule = alphaEffectRule;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    protected int findCellPosition(Player player) {
        return positions.getAlphaCellPosition();
    }

    @Override
    protected void applyEffect(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        alphaEffectRule.applyTo(player, teleportedPieces, messagePublisher);
    }
}
