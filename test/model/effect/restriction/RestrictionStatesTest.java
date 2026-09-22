package model.effect.restriction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Restriction states")
class RestrictionStatesTest {

    @Nested
    @DisplayName("NoRestrictionState")
    class NoRestriction {

        private final PieceRestrictionState free = NoRestrictionState.getInstance();

        @Test
        void isASharedInstance() {
            assertSame(free, NoRestrictionState.getInstance());
        }

        @Test
        void doesNotForbidMovement() {
            assertFalse(free.forbidsMovement());
        }

        @Test
        void hasNoRoundsRemaining() {
            assertEquals(0, free.getRoundsRemaining());
        }

        @Test
        void neverTriggersAReturnToBase() {
            assertFalse(free.afterRollRecorded(3).afterRollRecorded(3).hasTriggeredReturnToBase());
        }

        @Test
        void staysUnrestrictedWhenARoundElapses() {
            assertSame(free, free.afterRoundElapses());
        }

        @Test
        void staysUnrestrictedWhenARollIsRecorded() {
            assertSame(free, free.afterRollRecorded(3));
        }
    }

    @Nested
    @DisplayName("BetaRestrictedState")
    class BetaRestricted {

        private final PieceRestrictionState beta = new BetaRestrictedState();

        @Test
        void forbidsMovement() {
            assertTrue(beta.forbidsMovement());
        }

        @Test
        void startsWithFourRoundsRemaining() {
            assertEquals(4, beta.getRoundsRemaining());
        }

        @Test
        void aRoundElapsingUsesUpOneRound() {
            assertEquals(3, beta.afterRoundElapses().getRoundsRemaining());
        }

        @Test
        void stillForbidsMovementAfterFourRounds() {
            PieceRestrictionState state = beta;

            for (int round = 1; round <= 4; round++) {
                state = state.afterRoundElapses();
            }

            assertTrue(state.forbidsMovement());
        }

        @Test
        void expiresToNoRestrictionAfterTheFifthRound() {
            PieceRestrictionState state = beta;

            for (int round = 1; round <= 5; round++) {
                state = state.afterRoundElapses();
            }

            assertSame(NoRestrictionState.getInstance(), state);
        }

        @Test
        void hasNotTriggeredBeforeAnyRoll() {
            assertFalse(beta.hasTriggeredReturnToBase());
        }

        @Test
        void oneTriggerRollIsNotEnough() {
            assertFalse(beta.afterRollRecorded(3).hasTriggeredReturnToBase());
        }

        @Test
        void twoConsecutiveTriggerRollsSendThePieceBackToBase() {
            assertTrue(beta.afterRollRecorded(3).afterRollRecorded(3).hasTriggeredReturnToBase());
        }

        @Test
        void aNonTriggerRollInBetweenResetsTheCount() {
            PieceRestrictionState state = beta.afterRollRecorded(3).afterRollRecorded(5).afterRollRecorded(3);

            assertFalse(state.hasTriggeredReturnToBase());
        }

        @Test
        void recordingARollKeepsTheRoundsRemaining() {
            assertEquals(4, beta.afterRollRecorded(3).getRoundsRemaining());
        }

        @Test
        void aRoundElapsingKeepsTheRollCount() {
            PieceRestrictionState state = beta.afterRollRecorded(3).afterRoundElapses().afterRollRecorded(3);

            assertTrue(state.hasTriggeredReturnToBase());
        }

        @Test
        void statesAreImmutable() {
            beta.afterRoundElapses();
            beta.afterRollRecorded(3);

            assertEquals(4, beta.getRoundsRemaining());
            assertFalse(beta.hasTriggeredReturnToBase());
        }
    }
}
