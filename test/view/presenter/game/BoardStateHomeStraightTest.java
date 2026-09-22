package view.presenter.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import java.util.List;
import message.game.BoardStateReported;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.player.Player;
import model.player.strategy.blockdirection.LongestDistanceDirectionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Fixtures;

@DisplayName("BoardStateReportedPresenter on the HomeStraight")
class BoardStateHomeStraightTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final BoardStateReportedPresenter presenter = new BoardStateReportedPresenter(
            List.of(red), LudoBoard.getInstance(), new LongestDistanceDirectionStrategy());

    @Test
    void piecesOnDifferentHomeStraightCellsAreNotShownAsABlock() {
        Fixtures.placeOnHomeStraight(red, 0, 1, ClockwiseMovementStrategy.getInstance());
        Fixtures.placeOnHomeStraight(red, 1, 2, ClockwiseMovementStrategy.getInstance());

        String text = presenter.present(new BoardStateReported(1));

        assertFalse(text.contains("[Block:"));
        assertTrue(text.contains("R1(HomeStraight(1), "));
        assertTrue(text.contains("R2(HomeStraight(2), "));
    }
}
