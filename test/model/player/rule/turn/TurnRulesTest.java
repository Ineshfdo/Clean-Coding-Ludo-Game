package model.player.rule.turn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import config.enums.MovementEffectType;
import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.direction.EntryDirectionAssigner;
import model.effect.movement.MovementEffect;
import model.effect.restriction.BetaRestrictedState;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.command.cannotmove.BlockRollTooSmallCommand;
import model.player.command.cannotmove.ExactRollRequiredCommand;
import model.player.command.cannotmove.MoveBlockedByBlockadeCommand;
import model.player.command.cannotmove.SickRollTooSmallCommand;
import model.player.command.move.BreakBlockCommand;
import model.player.command.move.EnterBoardCommand;
import model.player.command.move.MoveBlockCommand;
import model.player.command.move.MovePieceCommand;
import model.player.rule.block.DivideByBlockSizeRule;
import model.player.rule.block.PassingBlockadeRule;
import model.player.rule.home.ApproachPassCountRule;
import model.player.rule.home.HomeStraightEligibilityRule;
import model.player.rule.home.HomeStraightEntryRule;
import model.player.rule.home.OvershootHomeRule;
import model.player.strategy.blockdirection.LongestDistanceDirectionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.Fixtures;

@DisplayName("Turn rules")
class TurnRulesTest {

    private final Board board = LudoBoard.getInstance();
    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final List<Player> everyone = List.of(red, green);

    @Nested
    @DisplayName("EnterBoardRule")
    class EnterBoard {

        private final EntryDirectionAssigner assigner = mock(EntryDirectionAssigner.class);
        private final TurnRule rule = new EnterBoardRule(assigner);

        @ParameterizedTest(name = "a roll of {0} cannot leave Base")
        @ValueSource(ints = {1, 2, 3, 4, 5})
        void onlyASixLeavesBase(int rollValue) {
            assertTrue(rule.findLegalCommands(red, rollValue, board, everyone).isEmpty());
        }

        @Test
        void aSixOffersOneCommandToEnterTheBoard() {
            List<MoveCommand> commands = rule.findLegalCommands(red, 6, board, everyone);

            assertEquals(1, commands.size());
            assertInstanceOf(EnterBoardCommand.class, commands.get(0));
            assertTrue(commands.get(0).entersBoard());
        }

        @Test
        void theFirstPieceAtBaseIsTheOneThatEnters() {
            List<MoveCommand> commands = rule.findLegalCommands(red, 6, board, everyone);

            assertSame(red.getPieces().get(0), commands.get(0).getAffectedPiece());
        }

        @Test
        void aPieceAlreadyOnTheTrackIsSkipped() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);

            List<MoveCommand> commands = rule.findLegalCommands(red, 6, board, everyone);

            assertSame(red.getPieces().get(1), commands.get(0).getAffectedPiece());
        }

        @Test
        void nothingIsOfferedWhenNoPieceIsAtBase() {
            for (int index = 0; index < 4; index++) {
                Fixtures.placeOnTrackClockwise(red, index, 10 + index);
            }

            assertTrue(rule.findLegalCommands(red, 6, board, everyone).isEmpty());
        }
    }

    @Nested
    @DisplayName("MovePiecesRule")
    class MovePieces {

        private final HomeStraightEntryRule entryRule = buildEntryRule();
        private final TurnRule rule = new MovePiecesRule(
                new PassingBlockadeRule(), entryRule, new OvershootHomeRule(board),
                new DivideByBlockSizeRule(), new LongestDistanceDirectionStrategy());

        private HomeStraightEntryRule buildEntryRule() {
            HomeStraightEntryRule passCount = new ApproachPassCountRule();
            passCount.setNext(new HomeStraightEligibilityRule(color -> false));
            return passCount;
        }

        private List<MoveCommand> commandsFor(int rollValue) {
            return rule.findLegalCommands(red, rollValue, board, everyone);
        }

        private void blockadeOf(Player player, int firstIndex, int position) {
            Fixtures.placeOnTrackClockwise(player, firstIndex, position);
            Fixtures.placeOnTrackClockwise(player, firstIndex + 1, position);
        }

        @Test
        void noCommandsWhenNoPieceIsOnTheBoard() {
            assertTrue(commandsFor(4).isEmpty());
        }

        @Test
        void aPieceOnTheTrackGetsAMoveCommand() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);

            List<MoveCommand> commands = commandsFor(4);

            assertEquals(1, commands.size());
            assertInstanceOf(MovePieceCommand.class, commands.get(0));
        }

        @Test
        void theMoveCommandLandsTheRollAheadOfThePiece() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);

            assertEquals(14, commandsFor(4).get(0).previewLandingPosition().orElseThrow());
        }

        @Test
        void everyPieceOnADifferentCellGetsItsOwnCommand() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);
            Fixtures.placeOnTrackClockwise(red, 1, 20);

            assertEquals(2, commandsFor(4).size());
        }

        @Test
        void piecesOnTheSameCellMoveAsOneBlockCommand() {
            blockadeOf(red, 0, 10);

            List<MoveCommand> commands = commandsFor(6);

            assertEquals(1, commands.size());
            assertInstanceOf(MoveBlockCommand.class, commands.get(0));
            assertTrue(commands.get(0).movesExistingBlock());
        }

        @Test
        void aBlockSharesTheRollBetweenItsPieces() {
            blockadeOf(red, 0, 10);

            assertEquals(13, commandsFor(6).get(0).previewLandingPosition().orElseThrow());
        }

        @Test
        void aBlockWhoseRollDividesToZeroCannotMove() {
            blockadeOf(red, 0, 10);

            List<MoveCommand> commands = commandsFor(1);

            assertInstanceOf(BlockRollTooSmallCommand.class, commands.get(0));
            assertTrue(commands.get(0).movesNothing());
        }

        @Test
        void aBetaRestrictedPieceIsLeftOut() {
            Piece restricted = Fixtures.placeOnTrackClockwise(red, 0, 10);
            Piece free = Fixtures.placeOnTrackClockwise(red, 1, 20);
            red.applyRestriction(restricted, new BetaRestrictedState());

            List<MoveCommand> commands = commandsFor(4);

            assertEquals(1, commands.size());
            assertSame(free, commands.get(0).getAffectedPiece());
        }

        @Test
        void noCommandsWhenTheOnlyPieceIsRestricted() {
            Piece restricted = Fixtures.placeOnTrackClockwise(red, 0, 10);
            red.applyRestriction(restricted, new BetaRestrictedState());

            assertTrue(commandsFor(4).isEmpty());
        }

        @Test
        void aRollThatOvershootsHomeGivesAnExactRollMessage() {
            Fixtures.placeOnHomeStraight(red, 0, 3, ClockwiseMovementStrategy.getInstance());

            List<MoveCommand> commands = commandsFor(4);

            assertInstanceOf(ExactRollRequiredCommand.class, commands.get(0));
            assertTrue(commands.get(0).movesNothing());
        }

        @Test
        void anExactRollOnTheHomeStraightIsALegalMove() {
            Fixtures.placeOnHomeStraight(red, 0, 3, ClockwiseMovementStrategy.getInstance());

            assertInstanceOf(MovePieceCommand.class, commandsFor(2).get(0));
        }

        @Test
        void anOpponentBlockadeRightAheadBlocksTheMove() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);
            blockadeOf(green, 0, 11);

            List<MoveCommand> commands = commandsFor(3);

            assertInstanceOf(MoveBlockedByBlockadeCommand.class, commands.get(0));
        }

        @Test
        void aSickPieceWhoseRollHalvesToZeroCannotMove() {
            Piece sick = Fixtures.placeOnTrackClockwise(red, 0, 10);
            red.applyIndividualEffect(sick, MovementEffect.of(MovementEffectType.SICK, 4));

            List<MoveCommand> commands = commandsFor(1);

            assertInstanceOf(SickRollTooSmallCommand.class, commands.get(0));
        }

        @Test
        void anEnergizedPieceMovesDoubleTheRoll() {
            Piece energized = Fixtures.placeOnTrackClockwise(red, 0, 10);
            red.applyIndividualEffect(energized, MovementEffect.of(MovementEffectType.ENERGIZED, 4));

            assertEquals(16, commandsFor(3).get(0).previewLandingPosition().orElseThrow());
        }

        @Test
        void aSickPieceMovesHalfTheRoll() {
            Piece sick = Fixtures.placeOnTrackClockwise(red, 0, 10);
            red.applyIndividualEffect(sick, MovementEffect.of(MovementEffectType.SICK, 4));

            assertEquals(13, commandsFor(6).get(0).previewLandingPosition().orElseThrow());
        }

        @Test
        void aBlockUsesItsBlockEffectInsteadOfThePiecesOwn() {
            blockadeOf(red, 0, 10);
            red.getPieces().get(0).applyIndividualEffect(MovementEffect.of(MovementEffectType.SICK, 4));
            red.applyBlockEffect(red.getPieces().get(0), MovementEffect.of(MovementEffectType.ENERGIZED, 4), 2);
            red.applyBlockEffect(red.getPieces().get(1), MovementEffect.of(MovementEffectType.ENERGIZED, 4), 2);

            // 6 shared by 2 = 3, doubled by the block's Energized effect = 6.
            assertEquals(16, commandsFor(6).get(0).previewLandingPosition().orElseThrow());
        }

        @Test
        void aPieceStillHoldingABlocksDirectionBreaksAwayFromTheBlock() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 10);
            piece.adoptBlockDirection(CounterClockwiseMovementStrategy.getInstance(), 2);

            List<MoveCommand> commands = commandsFor(3);

            assertInstanceOf(BreakBlockCommand.class, commands.get(0));
            assertTrue(commands.get(0).breaksExistingBlock());
        }

        @Test
        void aPieceAtBaseNeverGetsAMoveCommand() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);

            for (MoveCommand command : commandsFor(4)) {
                assertFalse(command.getAffectedPiece().isAtBase());
            }
        }
    }
}
