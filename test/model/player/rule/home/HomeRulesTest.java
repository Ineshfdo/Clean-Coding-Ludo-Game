package model.player.rule.home;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import model.board.LudoBoard;
import model.direction.CounterClockwiseMovementStrategy;
import model.piece.Piece;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.Fixtures;

@DisplayName("Home rules")
class HomeRulesTest {

    private static final HomeGateStatus GATE_CLOSED = color -> false;
    private static final HomeGateStatus GATE_OPEN = color -> true;

    private static Piece clockwisePassed(int passes) {
        Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 26);
        for (int pass = 0; pass < passes; pass++) {
            piece.recordApproachPass();
        }
        return piece;
    }

    private static Piece counterClockwisePassed(int passes) {
        Piece piece = Fixtures.pieceOnTrack(PlayerColor.RED, 1, 26, CounterClockwiseMovementStrategy.getInstance());
        for (int pass = 0; pass < passes; pass++) {
            piece.recordApproachPass();
        }
        return piece;
    }

    @Nested
    @DisplayName("ApproachPassCountRule")
    class ApproachPassCount {

        private final HomeStraightEntryRule rule = new ApproachPassCountRule();

        @Test
        void aClockwisePieceThatHasNotPassedApproachMayNotEnter() {
            assertTrue(rule.forbidsEntry(clockwisePassed(0)));
        }

        @Test
        void aClockwisePieceNeedsOnlyOnePass() {
            assertFalse(rule.forbidsEntry(clockwisePassed(1)));
        }

        @Test
        void aCounterClockwisePieceWithOnePassMayNotEnter() {
            assertTrue(rule.forbidsEntry(counterClockwisePassed(1)));
        }

        @Test
        void aCounterClockwisePieceNeedsTwoPasses() {
            assertFalse(rule.forbidsEntry(counterClockwisePassed(2)));
        }

        @Test
        void theRuleUsesTheOriginalDirectionNotABlocksDirection() {
            Piece piece = clockwisePassed(1);
            piece.adoptBlockDirection(CounterClockwiseMovementStrategy.getInstance(), 2);

            assertFalse(rule.forbidsEntry(piece));
        }
    }

    @Nested
    @DisplayName("HomeStraightEligibilityRule")
    class HomeStraightEligibility {

        @Test
        void aPieceWithoutACaptureMayNotEnterWhileTheGateIsClosed() {
            HomeStraightEntryRule rule = new HomeStraightEligibilityRule(GATE_CLOSED);

            assertTrue(rule.forbidsEntry(clockwisePassed(1)));
        }

        @Test
        void aPieceThatHasCapturedMayEnter() {
            HomeStraightEntryRule rule = new HomeStraightEligibilityRule(GATE_CLOSED);
            Piece piece = clockwisePassed(1);
            piece.recordCapture();

            assertFalse(rule.forbidsEntry(piece));
        }

        @Test
        void anOpenGateLetsAPieceEnterWithoutACapture() {
            HomeStraightEntryRule rule = new HomeStraightEligibilityRule(GATE_OPEN);

            assertFalse(rule.forbidsEntry(clockwisePassed(1)));
        }

        @Test
        void theGateIsAskedAboutThePiecesOwnColor() {
            HomeStraightEntryRule rule = new HomeStraightEligibilityRule(color -> color == PlayerColor.GREEN);

            assertTrue(rule.forbidsEntry(clockwisePassed(1)));
        }
    }

    @Nested
    @DisplayName("the entry rule chain")
    class EntryChain {

        private HomeStraightEntryRule chain() {
            HomeStraightEntryRule passCount = new ApproachPassCountRule();
            passCount.setNext(new HomeStraightEligibilityRule(GATE_CLOSED));
            return passCount;
        }

        @Test
        void aPieceMustSatisfyEveryRule() {
            Piece piece = clockwisePassed(1);
            piece.recordCapture();

            assertFalse(chain().forbidsEntry(piece));
        }

        @Test
        void missingThePassStopsThePieceEvenWithACapture() {
            Piece piece = clockwisePassed(0);
            piece.recordCapture();

            assertTrue(chain().forbidsEntry(piece));
        }

        @Test
        void missingTheCaptureStopsThePieceEvenAfterThePass() {
            assertTrue(chain().forbidsEntry(clockwisePassed(1)));
        }
    }

    @Nested
    @DisplayName("OvershootHomeRule")
    class OvershootHome {

        private final ExactRollRule rule = new OvershootHomeRule(LudoBoard.getInstance());

        private Piece onHomeStraightAt(int index) {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 26);
            piece.moveToHomeStraight(index);
            return piece;
        }

        @ParameterizedTest(name = "from index {0} a roll of {1} is forbidden: {2}")
        @CsvSource({"0,5,false", "0,6,true", "3,2,false", "3,3,true", "4,1,false", "4,2,true"})
        void aMoveMayNotGoBeyondHome(int index, int steps, boolean forbidden) {
            assertEquals(forbidden, rule.forbidsMove(onHomeStraightAt(index), steps));
        }

        @Test
        void aLaterRuleInTheChainCanStillForbidTheMove() {
            rule.setNext(new ExactRollRule() {
                @Override
                protected boolean appliesTo(Piece piece, int steps) {
                    return true;
                }
            });

            assertTrue(rule.forbidsMove(onHomeStraightAt(0), 1));
        }
    }
}
