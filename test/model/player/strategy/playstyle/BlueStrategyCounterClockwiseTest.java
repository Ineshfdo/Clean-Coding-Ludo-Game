package model.player.strategy.playstyle;

import static org.junit.jupiter.api.Assertions.assertSame;

import config.enums.PlayerColor;
import java.util.List;
import model.direction.CounterClockwiseMovementStrategy;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Commands;
import support.Fixtures;

@DisplayName("BlueStrategy with a counter-clockwise rotation choice")
class BlueStrategyCounterClockwiseTest {

    private static final int MYSTERY_CELL = 30;

    private final Player blue = Fixtures.playerOf(PlayerColor.BLUE);
    private final PlayerStrategy strategy = new BlueStrategy();
    private final Piece b1 = Fixtures.placeOnTrack(blue, 0, 10, CounterClockwiseMovementStrategy.getInstance());
    private final Piece b2 = Fixtures.placeOnTrack(blue, 1, 11, CounterClockwiseMovementStrategy.getInstance());
    private final StrategyContext context =
            Commands.contextFor(blue, List.of(blue), Commands.mysteryCellAt(MYSTERY_CELL), 1);

    @Test
    void aCounterClockwiseRotationChoiceNotOnTheMysteryCellStillYieldsToOneThatLandsOnIt() {
        MoveCommand rotationChoice = Commands.movingTo(b1, 20);
        MoveCommand ontoMysteryCell = Commands.movingTo(b2, MYSTERY_CELL);

        assertSame(ontoMysteryCell, strategy.choose(List.of(rotationChoice, ontoMysteryCell), context));
    }

    @Test
    void aCounterClockwiseRotationChoiceIsKeptWhenNothingLandsOnTheMysteryCell() {
        MoveCommand rotationChoice = Commands.movingTo(b1, 20);
        MoveCommand other = Commands.movingTo(b2, 21);

        assertSame(rotationChoice, strategy.choose(List.of(rotationChoice, other), context));
    }
}
