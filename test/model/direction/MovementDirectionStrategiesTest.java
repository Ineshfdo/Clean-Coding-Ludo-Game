package model.direction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import model.board.Board;
import model.board.LudoBoard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Movement direction strategies")
class MovementDirectionStrategiesTest {

    private final Board board = LudoBoard.getInstance();

    @Nested
    @DisplayName("Clockwise")
    class ClockwiseTests {

        private final MovementDirectionStrategy clockwise = ClockwiseMovementStrategy.getInstance();

        @Test
        void isASharedInstance() {
            assertSame(clockwise, ClockwiseMovementStrategy.getInstance());
        }

        @Test
        void nextPositionCountsUpTheTrack() {
            assertEquals(15, clockwise.nextPosition(10, 5, board));
        }

        @Test
        void nextPositionWrapsPastTheLastCell() {
            assertEquals(3, clockwise.nextPosition(50, 5, board));
        }

        @Test
        void stepsToApproachAreTheForwardDistance() {
            assertEquals(6, clockwise.countStepsToApproach(20, PlayerColor.RED, board));
        }

        @Test
        void stepsToApproachAreZeroStandingOnApproach() {
            assertEquals(0, clockwise.countStepsToApproach(26, PlayerColor.RED, board));
        }

        @Test
        void stepsToApproachWrapAroundWhenApproachIsBehind() {
            assertEquals(46, clockwise.countStepsToApproach(32, PlayerColor.RED, board));
        }

        @Test
        void needsOneApproachPass() {
            assertEquals(1, clockwise.getRequiredApproachPassCount());
        }

        @Test
        void isClockwise() {
            assertTrue(clockwise.isClockwise());
        }

        @Test
        void hasTheClockwiseLabel() {
            assertEquals("Clockwise", clockwise.getLabel());
        }

        @Test
        void reverseIsCounterClockwise() {
            assertSame(CounterClockwiseMovementStrategy.getInstance(), clockwise.reverse());
        }
    }

    @Nested
    @DisplayName("Counter-clockwise")
    class CounterClockwiseTests {

        private final MovementDirectionStrategy counterClockwise = CounterClockwiseMovementStrategy.getInstance();

        @Test
        void isASharedInstance() {
            assertSame(counterClockwise, CounterClockwiseMovementStrategy.getInstance());
        }

        @Test
        void nextPositionCountsDownTheTrack() {
            assertEquals(5, counterClockwise.nextPosition(10, 5, board));
        }

        @Test
        void nextPositionWrapsBelowCellZero() {
            assertEquals(49, counterClockwise.nextPosition(2, 5, board));
        }

        @Test
        void stepsToApproachAreTheDistanceBackToApproach() {
            assertEquals(4, counterClockwise.countStepsToApproach(30, PlayerColor.RED, board));
        }

        @Test
        void stepsToApproachAreZeroStandingOnApproach() {
            assertEquals(0, counterClockwise.countStepsToApproach(26, PlayerColor.RED, board));
        }

        @Test
        void stepsToApproachWrapAroundWhenApproachIsAhead() {
            assertEquals(46, counterClockwise.countStepsToApproach(20, PlayerColor.RED, board));
        }

        @Test
        void needsTwoApproachPasses() {
            assertEquals(2, counterClockwise.getRequiredApproachPassCount());
        }

        @Test
        void isNotClockwise() {
            assertFalse(counterClockwise.isClockwise());
        }

        @Test
        void hasTheCounterClockwiseLabel() {
            assertEquals("Counter-Clockwise", counterClockwise.getLabel());
        }

        @Test
        void reverseIsClockwise() {
            assertSame(ClockwiseMovementStrategy.getInstance(), counterClockwise.reverse());
        }
    }
}
