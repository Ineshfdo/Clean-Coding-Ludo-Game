package model.effect.rule;

import java.util.List;

import message.mystery.BlockEffectAssigned;
import message.mystery.IndividualEffectAssigned;
import model.piece.PieceLabels;
import utils.coin.CoinToss;
import config.constant.BlockadeConstants;
import config.constant.EffectConstants;
import config.enums.CoinTossResult;
import message.observer.GameMessagePublisher;
import config.enums.MovementEffectType;
import model.effect.movement.MovementEffect;
import model.piece.Piece;
import model.player.Player;

/**
 * Gives the pieces that were teleported to Alpha an Energized or Sick effect by coin toss (T-12).
 * Every piece gets its own effect, and a group of two or more pieces also gets one shared blockade
 * effect.
 */
public final class AlphaEffectRule {

    private final CoinToss coinToss;

    /**
     * Creates the rule.
     *
     * @param coinToss the coin that decides between Energized and Sick
     */
    public AlphaEffectRule(CoinToss coinToss) {
        this.coinToss = coinToss;
    }

    /**
     * Tosses the coin and gives the effects.
     *
     * @param player the owner of the pieces
     * @param teleportedPieces the pieces that arrived on Alpha
     * @param messagePublisher where the effects are announced
     */
    public void applyTo(Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        for (Piece piece : teleportedPieces) {
            MovementEffect individualEffect = rollEffect();
            player.applyIndividualEffect(piece, individualEffect);

            messagePublisher.publish(
                    new IndividualEffectAssigned(piece.toString(), individualEffect.getLabel()));
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
                new BlockEffectAssigned(PieceLabels.joinPieceLabels(teleportedPieces), blockEffect.getLabel()));
    }

    private MovementEffect rollEffect() {
        MovementEffectType type = coinToss.flip() == CoinTossResult.HEADS
                ? MovementEffectType.ENERGIZED
                : MovementEffectType.SICK;

        return MovementEffect.of(type, EffectConstants.EFFECT_DURATION_IN_ROUNDS);
    }
}
