package model.player.strategy.playstyle;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import config.enums.PlayerColor;
import exception.IllegalMoveException;
import java.util.List;
import model.direction.CounterClockwiseMovementStrategy;
import model.effect.mysterycell.MysteryCellLocation;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Commands;
import support.Fixtures;

// Blue rotates through its pieces B1 -> B2 -> B3 -> B4, with two Mystery Cell overrides.
@DisplayName("BlueStrategy")
class BlueStrategyTest {

    private static final int MYSTERY_CELL = 30;

    private final Player blue = Fixtures.playerOf(PlayerColor.BLUE);
    private final PlayerStrategy strategy = new BlueStrategy();

    private final Piece b1 = Fixtures.placeOnTrackClockwise(blue, 0, 10);
    private final Piece b2 = Fixtures.placeOnTrackClockwise(blue, 1, 11);
    private final Piece b3 = Fixtures.placeOnTrackClockwise(blue, 2, 12);
    private final Piece b4 = Fixtures.placeOnTrackClockwise(blue, 3, 13);

    private final MoveCommand forB1 = Commands.movingTo(b1, 14);
    private final MoveCommand forB2 = Commands.movingTo(b2, 15);
    private final MoveCommand forB3 = Commands.movingTo(b3, 16);
    private final MoveCommand forB4 = Commands.movingTo(b4, 17);
    private final List<MoveCommand> everyPiece = List.of(forB1, forB2, forB3, forB4);

    private StrategyContext context(int rollNumber) {
        return Commands.contextFor(blue, List.of(blue), rollNumber);
    }

    @Nested
    @DisplayName("the rotation")
    class Rotation {

        @Test
        void theFirstTurnConsidersTheFirstPiece() {
            assertSame(forB1, strategy.choose(everyPiece, context(1)));
        }

        @Test
        void eachNewTurnMovesOnToTheNextPiece() {
            strategy.choose(everyPiece, context(1));

            assertSame(forB2, strategy.choose(everyPiece, context(1)));
        }

        @Test
        void afterTheFourthPieceItWrapsBackToTheFirst() {
            for (int turn = 0; turn < 4; turn++) {
                strategy.choose(everyPiece, context(1));
            }

            assertSame(forB1, strategy.choose(everyPiece, context(1)));
        }

        @Test
        void aPieceWithNoLegalCommandIsSkipped() {
            List<MoveCommand> onlyLastTwo = List.of(forB3, forB4);

            assertSame(forB3, strategy.choose(onlyLastTwo, context(1)));
        }

        @Test
        void aBonusRollKeepsConsideringTheSamePiece() {
            strategy.choose(everyPiece, context(1));

            assertSame(forB1, strategy.choose(everyPiece, context(2)));
        }

        @Test
        void theTurnAfterABonusRollContinuesTheRotation() {
            strategy.choose(everyPiece, context(1));
            strategy.choose(everyPiece, context(2));

            assertSame(forB2, strategy.choose(everyPiece, context(1)));
        }

        @Test
        void noLegalCommandForAnyBluePieceIsAnError() {
            Player red = Fixtures.playerOf(PlayerColor.RED);
            MoveCommand redsCommand = Commands.forPiece(Fixtures.placeOnTrackClockwise(red, 0, 5));

            assertThrows(IllegalMoveException.class, () -> strategy.choose(List.of(redsCommand), context(1)));
        }
    }

    @Nested
    @DisplayName("the Mystery Cell overrides")
    class MysteryCellOverrides {

        private final MysteryCellLocation active = Commands.mysteryCellAt(MYSTERY_CELL);

        private StrategyContext withMysteryCell(MysteryCellLocation location) {
            return Commands.contextFor(blue, List.of(blue), location, 1);
        }

        private Piece counterClockwisePiece(Piece piece) {
            piece.assignMovementDirection(CounterClockwiseMovementStrategy.getInstance());
            return piece;
        }

        @Test
        void aCounterClockwiseMoveOntoTheMysteryCellIsPreferredToTheRotationsChoice() {
            counterClockwisePiece(b2);
            MoveCommand rotationChoice = forB1;
            MoveCommand ontoMysteryCell = Commands.movingTo(b2, MYSTERY_CELL);

            assertSame(ontoMysteryCell, strategy.choose(List.of(rotationChoice, ontoMysteryCell), withMysteryCell(active)));
        }

        @Test
        void aCounterClockwiseRotationChoiceOntoTheMysteryCellIsKept() {
            counterClockwisePiece(b1);
            counterClockwisePiece(b2);
            MoveCommand rotationChoice = Commands.movingTo(b1, MYSTERY_CELL);
            MoveCommand alternative = Commands.movingTo(b2, MYSTERY_CELL);

            assertSame(rotationChoice, strategy.choose(List.of(rotationChoice, alternative), withMysteryCell(active)));
        }

        @Test
        void aClockwiseRotationChoiceOntoTheMysteryCellIsSwappedForOneThatAvoidsIt() {
            MoveCommand rotationChoice = Commands.movingTo(b1, MYSTERY_CELL);
            MoveCommand avoids = Commands.movingTo(b2, 15);

            assertSame(avoids, strategy.choose(List.of(rotationChoice, avoids), withMysteryCell(active)));
        }

        @Test
        void aClockwiseChoiceOntoTheMysteryCellStaysWhenNothingAvoidsIt() {
            MoveCommand rotationChoice = Commands.movingTo(b1, MYSTERY_CELL);
            MoveCommand alsoOnto = Commands.movingTo(b2, MYSTERY_CELL);

            assertSame(rotationChoice, strategy.choose(List.of(rotationChoice, alsoOnto), withMysteryCell(active)));
        }

        @Test
        void withNoActiveMysteryCellTheRotationDecidesAlone() {
            counterClockwisePiece(b2);
            MoveCommand rotationChoice = Commands.movingTo(b1, MYSTERY_CELL);
            MoveCommand ontoMysteryCell = Commands.movingTo(b2, MYSTERY_CELL);

            assertSame(rotationChoice,
                    strategy.choose(List.of(rotationChoice, ontoMysteryCell), withMysteryCell(Commands.noMysteryCell())));
        }

        @Test
        void aPieceAtBaseHasNoDirectionSoItsChoiceIsKeptWhenNothingOverridesIt() {
            b1.returnToBase();
            MoveCommand enter = Commands.entering(b1);
            MoveCommand other = Commands.movingTo(b2, 15);

            assertSame(enter, strategy.choose(List.of(enter, other), withMysteryCell(active)));
        }

        @Test
        void aCounterClockwiseMoveOntoTheMysteryCellStillOverridesAPieceAtBase() {
            b1.returnToBase();
            counterClockwisePiece(b2);
            MoveCommand enter = Commands.entering(b1);
            MoveCommand ontoMysteryCell = Commands.movingTo(b2, MYSTERY_CELL);

            assertSame(ontoMysteryCell, strategy.choose(List.of(enter, ontoMysteryCell), withMysteryCell(active)));
        }
    }
}
