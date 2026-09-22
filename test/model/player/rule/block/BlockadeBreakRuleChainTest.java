package model.player.rule.block;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.board.LudoBoard;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.MoveCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Commands;
import support.Fixtures;

// The chain hands a roll to the next rule only when the current rule has nothing to force.
@DisplayName("BlockadeBreakRule chain")
class BlockadeBreakRuleChainTest {

    private final Board board = LudoBoard.getInstance();
    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 10);
    private final Command forced = Commands.forPiece(piece);

    private static BlockadeBreakRule ruleForcing(Command command) {
        return new BlockadeBreakRule() {
            @Override
            protected Optional<Command> identify(
                    Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers) {
                return Optional.ofNullable(command);
            }
        };
    }

    private Optional<Command> ask(BlockadeBreakRule rule) {
        return rule.findForcedBreak(red, 3, 6, board, List.of(red));
    }

    @Test
    void aRuleThatForcesSomethingAnswersWithoutAskingTheNextRule() {
        MoveCommand fromNextRule = Commands.forPiece(piece);
        BlockadeBreakRule first = ruleForcing(forced);
        first.setNext(ruleForcing(fromNextRule));

        assertSame(forced, ask(first).orElseThrow());
    }

    @Test
    void aRuleWithNothingToForceAsksTheNextRule() {
        BlockadeBreakRule first = ruleForcing(null);
        first.setNext(ruleForcing(forced));

        assertSame(forced, ask(first).orElseThrow());
    }

    @Test
    void whenNoRuleForcesAnythingThereIsNoBreak() {
        BlockadeBreakRule first = ruleForcing(null);
        first.setNext(ruleForcing(null));

        assertTrue(ask(first).isEmpty());
    }
}
