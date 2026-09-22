package model.player.strategy.blockdirection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import config.enums.BlockDirectionType;
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
import support.Fixtures;

@DisplayName("Block direction")
class BlockDirectionTest {

    private static final MovementDirectionStrategy CLOCKWISE = ClockwiseMovementStrategy.getInstance();
    private static final MovementDirectionStrategy COUNTER_CLOCKWISE = CounterClockwiseMovementStrategy.getInstance();

    private final Board board = LudoBoard.getInstance();
    private final Player red = Fixtures.playerOf(PlayerColor.RED);

    private Piece pieceAt20(int index, MovementDirectionStrategy direction) {
        return Fixtures.placeOnTrack(red, index, 20, direction);
    }

    @Nested
    @DisplayName("BlockDirectionClassifier")
    class Classifier {

        @Test
        void aSinglePieceIsSameDirection() {
            assertEquals(BlockDirectionType.SAME_DIRECTION, BlockDirectionClassifier.classify(List.of(pieceAt20(0, CLOCKWISE))));
        }

        @Test
        void noPiecesIsSameDirection() {
            assertEquals(BlockDirectionType.SAME_DIRECTION, BlockDirectionClassifier.classify(List.of()));
        }

        @Test
        void twoPiecesGoingTheSameWayAreSameDirection() {
            List<Piece> block = List.of(pieceAt20(0, CLOCKWISE), pieceAt20(1, CLOCKWISE));

            assertEquals(BlockDirectionType.SAME_DIRECTION, BlockDirectionClassifier.classify(block));
        }

        @Test
        void twoPiecesGoingOppositeWaysAreOppositeDirection() {
            List<Piece> block = List.of(pieceAt20(0, CLOCKWISE), pieceAt20(1, COUNTER_CLOCKWISE));

            assertEquals(BlockDirectionType.OPPOSITE_DIRECTION, BlockDirectionClassifier.classify(block));
        }

        @Test
        void theClassificationUsesOriginalDirectionsNotTheAdoptedOne() {
            Piece first = pieceAt20(0, CLOCKWISE);
            Piece second = pieceAt20(1, COUNTER_CLOCKWISE);
            second.adoptBlockDirection(CLOCKWISE, 2);

            assertEquals(BlockDirectionType.OPPOSITE_DIRECTION, BlockDirectionClassifier.classify(List.of(first, second)));
        }

        @Test
        void labelsNameTheBlockType() {
            assertEquals("Same-Direction", BlockDirectionClassifier.labelOf(BlockDirectionType.SAME_DIRECTION));
            assertEquals("Opposite-Direction", BlockDirectionClassifier.labelOf(BlockDirectionType.OPPOSITE_DIRECTION));
        }
    }

    @Nested
    @DisplayName("LongestDistanceDirectionStrategy")
    class LongestDistance {

        private final BlockTravelDirectionStrategy strategy = new LongestDistanceDirectionStrategy();

        @Test
        void aSinglePieceTravelsInItsOwnDirection() {
            Piece piece = pieceAt20(0, COUNTER_CLOCKWISE);

            assertSame(COUNTER_CLOCKWISE, strategy.resolveTravelDirection(List.of(piece), board));
        }

        @Test
        void aSameDirectionBlockTravelsThatWay() {
            List<Piece> block = List.of(pieceAt20(0, CLOCKWISE), pieceAt20(1, CLOCKWISE));

            assertSame(CLOCKWISE, strategy.resolveTravelDirection(block, board));
        }

        @Test
        void aMixedBlockFollowsThePieceFarthestFromHome() {
            // At cell 20, a clockwise Red piece is 11 from Home; a counter-clockwise one is 103.
            List<Piece> block = List.of(pieceAt20(0, CLOCKWISE), pieceAt20(1, COUNTER_CLOCKWISE));

            assertSame(COUNTER_CLOCKWISE, strategy.resolveTravelDirection(block, board));
        }

        @Test
        void theFarthestPieceWinsWhateverItsPositionInTheBlock() {
            List<Piece> block = List.of(pieceAt20(0, COUNTER_CLOCKWISE), pieceAt20(1, CLOCKWISE));

            assertSame(COUNTER_CLOCKWISE, strategy.resolveTravelDirection(block, board));
        }

        @Test
        void aTieKeepsTheFirstPiecesDirection() {
            List<Piece> block = List.of(pieceAt20(0, CLOCKWISE), pieceAt20(1, CLOCKWISE));

            assertSame(block.get(0).getOriginalMovementDirection(), strategy.resolveTravelDirection(block, board));
        }

        @Test
        void aDirectionAlreadyAdoptedForThisBlockSizeIsReused() {
            Piece first = pieceAt20(0, CLOCKWISE);
            Piece second = pieceAt20(1, COUNTER_CLOCKWISE);
            second.adoptBlockDirection(CLOCKWISE, 2);

            // By distance the counter-clockwise piece would win, but the block already agreed on clockwise.
            assertSame(CLOCKWISE, strategy.resolveTravelDirection(List.of(first, second), board));
        }

        @Test
        void aDirectionAdoptedForAnotherBlockSizeIsIgnored() {
            Piece first = pieceAt20(0, CLOCKWISE);
            Piece second = pieceAt20(1, COUNTER_CLOCKWISE);
            second.adoptBlockDirection(CLOCKWISE, 3);

            assertSame(COUNTER_CLOCKWISE, strategy.resolveTravelDirection(List.of(first, second), board));
        }
    }
}
