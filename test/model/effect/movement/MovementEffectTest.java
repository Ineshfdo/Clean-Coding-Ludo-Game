package model.effect.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.MovementEffectType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MovementEffect")
class MovementEffectTest {

    private static final int DURATION = 4;

    @Test
    void noneIsASharedInstance() {
        assertSame(MovementEffect.none(), MovementEffect.none());
    }

    @Test
    void noneIsNotActive() {
        assertFalse(MovementEffect.none().isActive());
    }

    @Test
    void energizedAndSickAreActive() {
        assertTrue(MovementEffect.of(MovementEffectType.ENERGIZED, DURATION).isActive());
        assertTrue(MovementEffect.of(MovementEffectType.SICK, DURATION).isActive());
    }

    @Test
    void energizedDoublesTheSteps() {
        assertEquals(6, MovementEffect.of(MovementEffectType.ENERGIZED, DURATION).applyTo(3));
    }

    @Test
    void sickHalvesTheSteps() {
        assertEquals(3, MovementEffect.of(MovementEffectType.SICK, DURATION).applyTo(6));
    }

    @Test
    void sickRoundsDownOddSteps() {
        assertEquals(2, MovementEffect.of(MovementEffectType.SICK, DURATION).applyTo(5));
    }

    @Test
    void noEffectLeavesTheStepsAlone() {
        assertEquals(5, MovementEffect.none().applyTo(5));
    }

    @Test
    void labelsNameTheEffect() {
        assertEquals("Energized", MovementEffect.of(MovementEffectType.ENERGIZED, DURATION).getLabel());
        assertEquals("Sick", MovementEffect.of(MovementEffectType.SICK, DURATION).getLabel());
        assertEquals("None", MovementEffect.none().getLabel());
    }

    @Test
    void effectStaysActiveForFourFullRounds() {
        MovementEffect effect = MovementEffect.of(MovementEffectType.ENERGIZED, DURATION);

        for (int round = 1; round <= DURATION; round++) {
            effect = effect.afterRoundElapses();
            assertTrue(effect.isActive(), "still active after round " + round);
        }
    }

    @Test
    void effectExpiresOnTheRoundAfterItsLastFullRound() {
        MovementEffect effect = MovementEffect.of(MovementEffectType.ENERGIZED, DURATION);

        for (int round = 1; round <= DURATION + 1; round++) {
            effect = effect.afterRoundElapses();
        }

        assertFalse(effect.isActive());
    }

    @Test
    void anElapsedRoundKeepsTheEffectType() {
        MovementEffect after = MovementEffect.of(MovementEffectType.SICK, DURATION).afterRoundElapses();

        assertEquals("Sick", after.getLabel());
    }

    @Test
    void noEffectStaysNoEffectAsRoundsPass() {
        assertSame(MovementEffect.none(), MovementEffect.none().afterRoundElapses());
    }
}
