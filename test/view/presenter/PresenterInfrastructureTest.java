package view.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import exception.UnpresentableMessageException;
import java.util.List;
import message.GameMessage;
import message.game.GameStarting;
import message.toss.TossTied;
import message.turn.TurnStarted;
import model.board.LudoBoard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Presenter infrastructure")
class PresenterInfrastructureTest {

    // A presenter with fixed wording, so dispatch can be tested without the real texts.
    private static <M extends GameMessage> EventPresenter<M> presenterSaying(Class<M> type, String text) {
        return new EventPresenter<>(type) {
            @Override
            public String present(M message) {
                return text;
            }
        };
    }

    @Nested
    @DisplayName("MessagePresenter")
    class Dispatch {

        private final MessagePresenter messagePresenter = new MessagePresenter(List.of(
                presenterSaying(TurnStarted.class, "a turn started"),
                presenterSaying(TossTied.class, "a toss tied")));

        @Test
        void aMessageGoesToThePresenterRegisteredForItsKind() {
            assertEquals("a turn started", messagePresenter.present(new TurnStarted(PlayerColor.RED)));
        }

        @Test
        void eachKindOfMessageGetsItsOwnPresenter() {
            assertEquals("a toss tied", messagePresenter.present(new TossTied(6)));
        }

        @Test
        void aMessageNobodyRegisteredIsAnError() {
            assertThrows(UnpresentableMessageException.class, () -> messagePresenter.present(new GameStarting()));
        }

        @Test
        void theErrorNamesTheMessageThatHasNoPresenter() {
            UnpresentableMessageException exception =
                    assertThrows(UnpresentableMessageException.class, () -> messagePresenter.present(new GameStarting()));

            assertEquals("No presenter is registered for GameStarting", exception.getMessage());
        }

        @Test
        void anEmptyRegistryPresentsNothing() {
            MessagePresenter empty = new MessagePresenter(List.of());

            assertThrows(UnpresentableMessageException.class, () -> empty.present(new TossTied(1)));
        }
    }

    @Nested
    @DisplayName("EventPresenter")
    class Base {

        @Test
        void reportsTheKindOfEventItHandles() {
            assertEquals(TurnStarted.class, presenterSaying(TurnStarted.class, "x").getEventType());
        }
    }

    @Nested
    @DisplayName("CellNames")
    class Names {

        private final CellNames cellNames = new CellNames(LudoBoard.getInstance());

        @ParameterizedTest(name = "cell {0} is an Approach cell")
        @ValueSource(ints = {0, 13, 26, 39})
        void everyColorsApproachCellIsNamedApproach(int position) {
            assertEquals("Approach(" + position + ")", cellNames.labelOf(position));
        }

        @ParameterizedTest(name = "cell {0} is an ordinary cell")
        @ValueSource(ints = {1, 5, 25, 28, 51})
        void anyOtherCellIsNamedCell(int position) {
            assertEquals("Cell(" + position + ")", cellNames.labelOf(position));
        }

        @Test
        void anEntryCellIsNotAnApproachCell() {
            assertTrue(cellNames.labelOf(28).startsWith("Cell("));
        }
    }

    @Nested
    @DisplayName("PieceCountLine")
    class CountLine {

        @Test
        void describesHowManyPiecesAreOnTheBoardAndAtBase() {
            assertEquals("Red player now has 2/4 pieces on the board and 2/4 pieces on the base.",
                    PieceCountLine.describe("Red", 2, 2));
        }

        @Test
        void worksForZeroPieces() {
            assertEquals("Blue player now has 0/4 pieces on the board and 4/4 pieces on the base.",
                    PieceCountLine.describe("Blue", 0, 4));
        }
    }
}
