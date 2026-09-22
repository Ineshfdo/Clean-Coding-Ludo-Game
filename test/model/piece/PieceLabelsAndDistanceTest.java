package model.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.CounterClockwiseMovementStrategy;
import model.direction.ClockwiseMovementStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;

@DisplayName("Piece helpers")
class PieceLabelsAndDistanceTest {

    @Nested
    @DisplayName("PieceLabels")
    class Labels {

        @Test
        void joinsPieceNamesWithAPlus() {
            List<Piece> block = List.of(new Piece(PlayerColor.RED, 1), new Piece(PlayerColor.RED, 2));

            assertEquals("R1+R2", PieceLabels.joinPieceLabels(block));
        }

        @Test
        void aSinglePieceKeepsItsPlainName() {
            assertEquals("B4", PieceLabels.joinPieceLabels(List.of(new Piece(PlayerColor.BLUE, 4))));
        }

        @Test
        void noPiecesGiveAnEmptyLabel() {
            assertEquals("", PieceLabels.joinPieceLabels(List.of()));
        }
    }

    @Nested
    @DisplayName("RemainingHomeDistance")
    class Distance {

        private final Board board = LudoBoard.getInstance();

        @Test
        void aPieceAlreadyHomeHasNothingLeft() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 10);
            piece.moveHome();

            assertEquals(0, RemainingHomeDistance.forPiece(piece, board));
        }

        @Test
        void aPieceOnTheHomeStraightCountsTheCellsLeftOnIt() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 10);
            piece.moveToHomeStraight(2);

            assertEquals(3, RemainingHomeDistance.forPiece(piece, board));
        }

        @Test
        void clockwisePieceCountsStepsToApproachPlusTheHomeStraight() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 20);

            // 6 steps to Approach(26) + 5 HomeStraight cells; the first pass is enough clockwise.
            assertEquals(11, RemainingHomeDistance.forPiece(piece, board));
        }

        @Test
        void counterClockwisePieceNeedsAnExtraLapBeforeHomeStraight() {
            Piece piece = Fixtures.pieceOnTrack(
                    PlayerColor.RED, 1, 30, CounterClockwiseMovementStrategy.getInstance());

            // 4 steps + one extra 52-cell lap (two passes needed) + 5 HomeStraight cells.
            assertEquals(61, RemainingHomeDistance.forPiece(piece, board));
        }

        @Test
        void clockwisePieceStandingOnApproachWithItsPassOnlyHasTheHomeStraightLeft() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 26);
            piece.recordApproachPass();

            assertEquals(5, RemainingHomeDistance.forPiece(piece, board));
        }

        @Test
        void counterClockwisePieceOnApproachWithOnePassStillNeedsAFullLap() {
            Piece piece = Fixtures.pieceOnTrack(
                    PlayerColor.RED, 1, 26, CounterClockwiseMovementStrategy.getInstance());
            piece.recordApproachPass();

            assertEquals(57, RemainingHomeDistance.forPiece(piece, board));
        }

        @Test
        void usesTheOriginalDirectionNotABlocksDirection() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 20);
            piece.adoptBlockDirection(CounterClockwiseMovementStrategy.getInstance(), 2);

            assertEquals(11, RemainingHomeDistance.forPiece(piece, board));
            assertEquals(ClockwiseMovementStrategy.getInstance(), piece.getOriginalMovementDirection());
        }
    }
}
