package model.effect.destination;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.board.MysteryCellPositions;
import model.effect.activation.EffectActivationRule;
import model.effect.rule.GammaDirectionRule;
import model.piece.Piece;
import model.player.Player;

// T-14: Gamma reverses a Clockwise group's direction, or sends a Counter-Clockwise group on to Beta.
public final class GammaDestination extends TrackDestination {

    private static final String LABEL = "Gamma";

    private final MysteryCellPositions positions;
    private final GammaDirectionRule gammaDirectionRule;

    public GammaDestination(
            MysteryCellPositions positions, EffectActivationRule effectActivationRule,
            GammaDirectionRule gammaDirectionRule) {
        super(effectActivationRule);
        this.positions = positions;
        this.gammaDirectionRule = gammaDirectionRule;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    protected int findCellPosition(Player player) {
        return positions.getGammaCellPosition();
    }

    @Override
    protected void applyEffect(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        gammaDirectionRule.applyTo(player, teleportedPieces, messagePublisher);
    }
}
