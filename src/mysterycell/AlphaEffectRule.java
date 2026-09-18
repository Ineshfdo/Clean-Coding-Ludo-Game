package mysterycell;

import java.util.List;
import java.util.stream.Collectors;

import coin.CoinToss;
import coin.CoinTossResult;
import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import player.MovementEffect;
import player.MovementEffectType;
import player.Piece;
import player.Player;

// T-12: a coin toss decides Energized or Sick for every piece teleported to Alpha,
// plus one more shared toss for the whole block when 2+ pieces teleported together.
public final class AlphaEffectRule {

    private static final int EFFECT_DURATION_IN_ROUNDS = 4;
    private static final int MINIMUM_BLOCK_SIZE = 2;

    private final CoinToss coinToss;

    public AlphaEffectRule(CoinToss coinToss) {
        this.coinToss = coinToss;
    }

    public void applyTo(Player player, List<Piece> teleportedPieces, GameMessagePublisher messages) {
        for (Piece piece : teleportedPieces) {
            MovementEffect individualEffect = rollEffect();
            player.applyIndividualEffect(piece, individualEffect);
            messages.publish(
                    GameMessage.individualEffectAssigned(piece.toString(), individualEffect.getLabel()));
        }

        if (teleportedPieces.size() < MINIMUM_BLOCK_SIZE) {
            return;
        }

        // T-12: as long as they stay grouped, this shared block effect overrides each piece's
        // own - recorded against this exact group's size so it stops applying the moment the
        // group's membership changes.
        MovementEffect blockEffect = rollEffect();
        for (Piece piece : teleportedPieces) {
            player.applyBlockEffect(piece, blockEffect, teleportedPieces.size());
        }
        messages.publish(
                GameMessage.blockEffectAssigned(describeBlock(teleportedPieces), blockEffect.getLabel()));
    }

    private MovementEffect rollEffect() {
        MovementEffectType type = coinToss.flip() == CoinTossResult.HEADS
                ? MovementEffectType.ENERGIZED
                : MovementEffectType.SICK;
        return MovementEffect.of(type, EFFECT_DURATION_IN_ROUNDS);
    }

    private static String describeBlock(List<Piece> pieces) {
        return pieces.stream().map(Piece::toString).collect(Collectors.joining("+"));
    }
}
