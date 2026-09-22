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

// Green is block-focused: Home first, then forming or moving blocks, then entering, and breaking a block last.
@DisplayName("GreenStrategy")
class GreenStrategyTest {

    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final StrategyContext context = Commands.contextFor(green, List.of(green), 1);
    private final PlayerStrategy strategy = new GreenStrategy();

    private final Piece mover = Fixtures.placeOnTrackClockwise(green, 0, 10);
    private final Piece partner = Fixtures.placeOnTrackClockwise(green, 1, 20);
    private final Piece atBase = green.getPieces().get(2);

    private MoveCommand plainMove() {
        return Commands.movingTo(mover, 14);
    }

    private MoveCommand homeMove() {
        MoveCommand command = Commands.forPiece(mover);
        when(command.reachesHome()).thenReturn(true);
        return command;
    }

    // Lands on the cell where another Green piece already stands.
    private MoveCommand blockFormingMove() {
        return Commands.movingTo(mover, 20);
    }

    private MoveCommand blockContinuingMove() {
        MoveCommand command = Commands.forPiece(partner);
        when(command.movesExistingBlock()).thenReturn(true);
        return command;
    }

    private MoveCommand blockBreakingMove() {
        MoveCommand command = Commands.forPiece(partner);
        when(command.breaksExistingBlock()).thenReturn(true);
        return command;
    }

    @Test
    void movingTowardsHomeBeatsFormingABlock() {
        MoveCommand home = homeMove();

        assertSame(home, strategy.choose(List.of(blockFormingMove(), home), context));
    }

    @Test
    void formingANewBlockBeatsMovingAnExistingOne() {
        MoveCommand forming = blockFormingMove();

        assertSame(forming, strategy.choose(List.of(blockContinuingMove(), forming), context));
    }

    @Test
    void movingAnExistingBlockBeatsEnteringTheBoard() {
        MoveCommand continuing = blockContinuingMove();

        assertSame(continuing, strategy.choose(List.of(Commands.entering(atBase), continuing), context));
    }

    @Test
    void enteringTheBoardBeatsAPlainMove() {
        MoveCommand enter = Commands.entering(atBase);

        assertSame(enter, strategy.choose(List.of(plainMove(), enter), context));
    }

    @Test
    void aPlainMoveBeatsBreakingABlock() {
        MoveCommand plain = plainMove();

        assertSame(plain, strategy.choose(List.of(blockBreakingMove(), plain), context));
    }

    @Test
    void breakingABlockIsTheLastResort() {
        MoveCommand firstBreak = blockBreakingMove();
        MoveCommand secondBreak = blockBreakingMove();

        assertSame(firstBreak, strategy.choose(List.of(firstBreak, secondBreak), context));
    }

    @Test
    void withNoPreferenceTheFirstCommandWins() {
        MoveCommand first = plainMove();
        MoveCommand second = Commands.movingTo(mover, 16);

        assertSame(first, strategy.choose(List.of(first, second), context));
    }
}
