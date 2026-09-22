package model.player.rule.turn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import config.enums.MovementEffectType;
import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.effect.movement.MovementEffect;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.command.move.MoveBlockCommand;
import model.player.command.move.MovePieceCommand;
import model.player.rule.block.DivideByBlockSizeRule;
import model.player.rule.block.PassingBlockadeRule;
import model.player.rule.home.ApproachPassCountRule;
import model.player.rule.home.OvershootHomeRule;
import model.player.strategy.blockdirection.LongestDistanceDirectionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Fixtures;

@DisplayName("MovePiecesRule on the HomeStraight and with block effects")
class MovePiecesRuleHomeStraightTest {

    private final Board board = LudoBoard.getInstance();
    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final TurnRule rule = new MovePiecesRule(
            new PassingBlockadeRule(), new ApproachPassCountRule(), new OvershootHomeRule(board),
            new DivideByBlockSizeRule(), new LongestDistanceDirectionStrategy());

    private List<MoveCommand> commandsFor(int rollValue) {
        return rule.findLegalCommands(red, rollValue, board, List.of(red));
    }

    @Test
    void piecesOnDifferentHomeStraightCellsMoveSeparately() {
        Fixtures.placeOnHomeStraight(red, 0, 1, ClockwiseMovementStrategy.getInstance());
        Fixtures.placeOnHomeStraight(red, 1, 2, ClockwiseMovementStrategy.getInstance());

        List<MoveCommand> commands = commandsFor(1);

        assertEquals(2, commands.size());
        assertInstanceOf(MovePieceCommand.class, commands.get(0));
        assertInstanceOf(MovePieceCommand.class, commands.get(1));
    }

    @Test
    void piecesOnTheSameHomeStraightCellMoveTogetherAsABlock() {
        Fixtures.placeOnHomeStraight(red, 0, 1, ClockwiseMovementStrategy.getInstance());
        Fixtures.placeOnHomeStraight(red, 1, 1, ClockwiseMovementStrategy.getInstance());

        List<MoveCommand> commands = commandsFor(2);

        assertEquals(1, commands.size());
        assertInstanceOf(MoveBlockCommand.class, commands.get(0));
    }

    @Test
    void aBlockWithoutABlockEffectIgnoresTheIndividualEffectsOfItsPieces() {
        Piece first = Fixtures.placeOnTrackClockwise(red, 0, 10);
        Fixtures.placeOnTrackClockwise(red, 1, 10);
        red.applyIndividualEffect(first, MovementEffect.of(MovementEffectType.ENERGIZED, 4));

        // 6 shared by 2 = 3 cells; the piece's own Energized effect would make it 6 if it were used.
        assertEquals(13, commandsFor(6).get(0).previewLandingPosition().orElseThrow());
    }

    @Test
    void aBlockEffectAssignedForAnotherBlockSizeIsIgnored() {
        Piece first = Fixtures.placeOnTrackClockwise(red, 0, 10);
        Fixtures.placeOnTrackClockwise(red, 1, 10);
        red.applyBlockEffect(first, MovementEffect.of(MovementEffectType.ENERGIZED, 4), 3);

        assertEquals(13, commandsFor(6).get(0).previewLandingPosition().orElseThrow());
    }
}
