package model.effect.rule;

import java.util.List;

import model.piece.PieceLabels;
import utils.coin.CoinToss;
import config.constant.BlockadeConstants;
import config.constant.EffectConstants;
import config.enums.CoinTossResult;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import config.enums.MovementEffectType;
import model.effect.movement.MovementEffect;
import model.piece.Piece;
import model.player.Player;

// T-12: coin toss picks Energized or Sick for pieces teleported to Alpha.
public final class AlphaEffectRule {

    private final CoinToss coinToss;

    public AlphaEffectRule(CoinToss coinToss) {
        this.coinToss = coinToss;
    }

    public void applyTo(Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        for (Piece piece : teleportedPieces) {
            MovementEffect individualEffect = rollEffect();
            player.applyIndividualEffect(piece, individualEffect);

            messagePublisher.publish(
                    GameMessage.individualEffectAssigned(piece.toString(), individualEffect.getLabel()));
        }

        if (teleportedPieces.size() < BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
            return;
        }

        // T-12: block effect overrides each piece's own while grouped.
        MovementEffect blockEffect = rollEffect();

        for (Piece piece : teleportedPieces) {
            player.applyBlockEffect(piece, blockEffect, teleportedPieces.size());
        }

        messagePublisher.publish(
                GameMessage.blockEffectAssigned(PieceLabels.joinPieceLabels(teleportedPieces), blockEffect.getLabel()));
    }

    private MovementEffect rollEffect() {
        MovementEffectType type = coinToss.flip() == CoinTossResult.HEADS
                ? MovementEffectType.ENERGIZED
                : MovementEffectType.SICK;

        return MovementEffect.of(type, EffectConstants.EFFECT_DURATION_IN_ROUNDS);
    }
}
