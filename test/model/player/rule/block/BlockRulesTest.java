package model.player.rule.block;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.Fixtures;

@DisplayName("Block rules")
class BlockRulesTest {

    private static final MovementDirectionStrategy CLOCKWISE = ClockwiseMovementStrategy.getInstance();
    private static final MovementDirectionStrategy COUNTER_CLOCKWISE = CounterClockwiseMovementStrategy.getInstance();

    private final Board board = LudoBoard.getInstance();

    @Nested
    @DisplayName("DivideByBlockSizeRule")
    class DivideByBlockSize {

        private final BlockStepsRule rule = new DivideByBlockSizeRule();

        private List<Piece> blockOf(int size) {
            Player red = Fixtures.playerOf(PlayerColor.RED);
            for (int index = 0; index < size; index++) {
                Fixtures.placeOnTrackClockwise(red, index, 10);
            }
            return red.getPiecesAt(10);
        }

        @ParameterizedTest(name = "a block of {0} rolling {1} moves {2}")
        @CsvSource({"1,6,6", "2,6,3", "2,5,2", "2,1,0", "3,6,2", "4,6,1", "4,3,0"})
        void theRollIsSharedEvenlyRoundingDown(int blockSize, int roll, int expectedSteps) {
            assertEquals(expectedSteps, rule.limitSteps(blockOf(blockSize), roll));
        }

        @Test
        void theNextRuleWorksOnTheAlreadyDividedSteps() {
            rule.setNext(new DivideByBlockSizeRule());

            // 12 / 2 = 6, then 6 / 2 = 3.
            assertEquals(3, rule.limitSteps(blockOf(2), 12));
        }
    }

    @Nested
    @DisplayName("PassingBlockadeRule")
    class PassingBlockade {

        private final BlockadeLimitRule rule = new PassingBlockadeRule();
        private final Player red = Fixtures.playerOf(PlayerColor.RED);
        private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
        private final List<Player> everyone = List.of(red, green);

        private void opponentBlockadeAt(int position) {
            Fixtures.placeOnTrackClockwise(green, 0, position);
            Fixtures.placeOnTrackClockwise(green, 1, position);
        }

        private int limit(int from, int steps, int moverBlockSize, MovementDirectionStrategy direction) {
            return rule.limitSteps(PlayerColor.RED, from, steps, board, everyone, direction, moverBlockSize);
        }

        @Test
        void anEmptyRoadDoesNotLimitTheMove() {
            assertEquals(5, limit(10, 5, 1, CLOCKWISE));
        }

        @Test
        void aBlockadeInTheWayStopsThePieceJustBeforeIt() {
            opponentBlockadeAt(13);

            assertEquals(2, limit(10, 5, 1, CLOCKWISE));
        }

        @Test
        void aBlockadeRightNextToThePieceStopsItCompletely() {
            opponentBlockadeAt(11);

            assertEquals(0, limit(10, 5, 1, CLOCKWISE));
        }

        @Test
        void aBlockadeOnTheLandingCellStillBlocksAPieceOfADifferentSize() {
            opponentBlockadeAt(15);

            assertEquals(4, limit(10, 5, 1, CLOCKWISE));
        }

        @Test
        void aBlockOfTheSameSizeMayLandOnTheBlockadeToCaptureIt() {
            opponentBlockadeAt(15);

            assertEquals(5, limit(10, 5, 2, CLOCKWISE));
        }

        @Test
        void aBlockOfTheSameSizeCannotPassThroughTheBlockadeBeforeTheLandingCell() {
            opponentBlockadeAt(13);

            assertEquals(2, limit(10, 5, 2, CLOCKWISE));
        }

        @Test
        void aSingleOpponentPieceIsNotABlockade() {
            Fixtures.placeOnTrackClockwise(green, 0, 13);

            assertEquals(5, limit(10, 5, 1, CLOCKWISE));
        }

        @Test
        void aBlockadeBehindThePieceDoesNotMatter() {
            opponentBlockadeAt(8);

            assertEquals(5, limit(10, 5, 1, CLOCKWISE));
        }

        @Test
        void counterClockwiseMovesAreStoppedByBlockadesInTheirOwnDirection() {
            opponentBlockadeAt(7);

            assertEquals(2, limit(10, 5, 1, COUNTER_CLOCKWISE));
        }

        @Test
        void theMoversOwnBlockadeDoesNotBlockIt() {
            Fixtures.placeOnTrackClockwise(red, 1, 13);
            Fixtures.placeOnTrackClockwise(red, 2, 13);

            assertEquals(5, limit(10, 5, 1, CLOCKWISE));
        }

        @Test
        void zeroStepsStayZero() {
            opponentBlockadeAt(11);

            assertEquals(0, limit(10, 0, 1, CLOCKWISE));
        }

        @Test
        void theNextRuleWorksOnTheAlreadyLimitedSteps() {
            opponentBlockadeAt(13);
            rule.setNext(new BlockadeLimitRule() {
                @Override
                protected int restrict(
                        PlayerColor moverColor, int fromPosition, int requestedSteps, Board board,
                        List<Player> allPlayers, MovementDirectionStrategy direction, int moverBlockSize) {
                    return requestedSteps - 1;
                }
            });

            // The blockade limits 5 to 2, then the next rule takes one more away.
            assertEquals(1, limit(10, 5, 1, CLOCKWISE));
        }
    }
}
