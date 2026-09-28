package model.effect.destination;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.board.MysteryCellPositions;
import model.effect.activation.EffectActivationRule;
import model.effect.rule.GammaDirectionRule;
import model.piece.Piece;
import model.player.Player;

/**
 The Gamma destination (T-14).
 A clockwise group reverses its direction, and a counter-clockwise group is sent on to Beta.
 */
public final class GammaDestination extends TrackDestination {

    private static final String LABEL = "Gamma";

    private final MysteryCellPositions positions;
    private final GammaDirectionRule gammaDirectionRule;

    /**
     Creates the Gamma destination.
     @param positions gives the Gamma cell
     @param effectActivationRule decides whether the effect may start
     @param gammaDirectionRule applies the direction rule of Gamma
     */
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
