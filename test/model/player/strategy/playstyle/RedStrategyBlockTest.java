package model.player.strategy.playstyle;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import config.enums.PlayerColor;
import java.util.List;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Commands;
import support.Fixtures;

// A block moves as one, so a block leaving the path takes every block member off it, not just the first.
@DisplayName("RedStrategy with blocks")
class RedStrategyBlockTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final StrategyContext context = Commands.contextFor(red, List.of(red), 1);
    private final PlayerStrategy strategy = new RedStrategy();

    private final Piece first = Fixtures.placeOnTrackClockwise(red, 0, 10);
    private final Piece second = Fixtures.placeOnTrackClockwise(red, 1, 10);

    private MoveCommand blockLeavingThePath() {
        MoveCommand command = Commands.movingTo(first, null);
        when(command.movesExistingBlock()).thenReturn(true);
        when(command.leavesStandardPath()).thenReturn(true);
        return command;
    }

    @Test
    void aBlockLeavingThePathIsAvoidedWhenItWouldTakeEveryRedPieceOffTheTrack() {
        MoveCommand leaving = blockLeavingThePath();
        MoveCommand staying = Commands.movingTo(first, 14);

        assertSame(staying, strategy.choose(List.of(leaving, staying), context));
    }

    @Test
    void aBlockLeavingThePathIsFineWhenAnotherRedPieceStaysOnTheTrack() {
        Fixtures.placeOnTrackClockwise(red, 2, 30);
        MoveCommand leaving = blockLeavingThePath();
        MoveCommand staying = Commands.movingTo(first, 14);

        assertSame(leaving, strategy.choose(List.of(leaving, staying), context));
    }

    @Test
    void theBlockIsCountedAsMovingEvenThoughItsCommandNamesOnlyTheFirstMember() {
        // If only the first member were counted as moving, the second would look like a piece left behind.
        MoveCommand leaving = blockLeavingThePath();
        MoveCommand other = Commands.movingTo(second, 16);

        assertSame(other, strategy.choose(List.of(leaving, other), context));
    }
}
