package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import config.enums.PlayerColor;
import java.util.List;
import java.util.Optional;
import message.turn.NoPieceMovable;
import message.turn.ThirdSixVoided;
import message.turn.TurnRolled;
import message.turn.TurnStarted;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.board.LudoBoard;
import model.effect.mysterycell.MysteryCellLocation;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.MoveCommand;
import model.player.rule.block.BlockadeBreakRule;
import model.player.rule.capture.CaptureCheckRule;
import model.player.rule.mystery.TeleportRule;
import model.player.rule.roll.ConsecutiveSixVoidRule;
import model.player.rule.roll.RollEvent;
import model.player.rule.roll.RollHook;
import model.player.rule.turn.TurnRule;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.PlayerStrategyLookup;
import model.player.strategy.StrategyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import support.Commands;
import support.Fixtures;
import utils.dice.Dice;

// Every collaborator of the engine is a mock, so each test shows one rule of a turn in isolation.
@DisplayName("GameEngine")
class GameEngineTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final List<Player> everyone = List.of(red, green);
    private final Board board = LudoBoard.getInstance();

    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);
    private final Dice dice = mock(Dice.class);
    private final TurnRule turnRule = mock(TurnRule.class);
    private final PlayerStrategy strategy = mock(PlayerStrategy.class);
    private final PlayerStrategyLookup strategyLookup = mock(PlayerStrategyLookup.class);
    private final CaptureCheckRule captureRule = mock(CaptureCheckRule.class);
    private final BlockadeBreakRule breakRule = mock(BlockadeBreakRule.class);
    private final TeleportRule teleportRule = mock(TeleportRule.class);
    private final MysteryCellLocation mysteryCell = mock(MysteryCellLocation.class);
    private final RollHook rollHook = mock(RollHook.class);

    private final GameEngine engine = new GameEngine(
            List.of(turnRule), strategyLookup, new ConsecutiveSixVoidRule(), captureRule, breakRule,
            teleportRule, mysteryCell, List.of(rollHook));

    private final Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 10);
    private final MoveCommand move = Commands.movingTo(piece, 14);

    GameEngineTest() {
        when(strategyLookup.getStrategyFor(PlayerColor.RED)).thenReturn(strategy);
        when(turnRule.findLegalCommands(eq(red), anyInt(), any(), any())).thenReturn(List.of(move));
        when(strategy.choose(any(), any())).thenReturn(move);
    }

    private void playTurn() {
        engine.playTurn(red, everyone, dice, board, publisher);
    }

    @Test
    void theTurnIsAnnouncedThenTheRollIsReported() {
        when(dice.roll()).thenReturn(4);

        playTurn();

        InOrder order = inOrder(publisher);
        order.verify(publisher).publish(new TurnStarted(PlayerColor.RED));
        order.verify(publisher).publish(new TurnRolled(PlayerColor.RED, 4));
    }

    @Test
    void aRollOtherThanSixEndsTheTurnAfterOneRoll() {
        when(dice.roll()).thenReturn(4);

        playTurn();

        verify(dice, times(1)).roll();
        verify(move, times(1)).execute(publisher);
    }

    @Test
    void aSixEarnsAnotherRoll() {
        when(dice.roll()).thenReturn(6, 4);

        playTurn();

        verify(dice, times(2)).roll();
        verify(move, times(2)).execute(publisher);
    }

    @Test
    void aThirdSixInARowIsVoidedAndEndsTheTurn() {
        when(dice.roll()).thenReturn(6, 6, 6);

        playTurn();

        verify(dice, times(3)).roll();
        verify(move, times(2)).execute(publisher);
        verify(publisher).publish(new ThirdSixVoided());
    }

    @Test
    void twoSixesFollowedByAnotherNumberAreNotVoided() {
        when(dice.roll()).thenReturn(6, 6, 4);

        playTurn();

        verify(move, times(3)).execute(publisher);
        verify(publisher, never()).publish(new ThirdSixVoided());
    }

    @Test
    void aTurnWithNothingToMoveAnnouncesItAndEnds() {
        when(dice.roll()).thenReturn(4);
        when(turnRule.findLegalCommands(eq(red), anyInt(), any(), any())).thenReturn(List.of());

        playTurn();

        verify(publisher).publish(new NoPieceMovable());
        verifyNoInteractions(strategy);
    }

    @Test
    void theStrategyChoosesFromTheLegalCommandsWithTheTurnsContext() {
        when(dice.roll()).thenReturn(4);
        ArgumentCaptor<StrategyContext> context = ArgumentCaptor.forClass(StrategyContext.class);

        playTurn();

        verify(strategy).choose(eq(List.of(move)), context.capture());
        assertSame(red, context.getValue().getPlayer());
        assertSame(mysteryCell, context.getValue().getMysteryCellLocation());
        assertEquals(1, context.getValue().getRollNumber());
    }

    @Test
    void theRollNumberInTheContextCountsBonusRolls() {
        when(dice.roll()).thenReturn(6, 4);
        ArgumentCaptor<StrategyContext> context = ArgumentCaptor.forClass(StrategyContext.class);

        playTurn();

        verify(strategy, times(2)).choose(any(), context.capture());
        assertEquals(2, context.getAllValues().get(1).getRollNumber());
    }

    @Test
    void legalCommandsFromEveryTurnRuleAreOfferedTogether() {
        TurnRule secondRule = mock(TurnRule.class);
        MoveCommand other = Commands.movingTo(piece, 15);
        when(secondRule.findLegalCommands(eq(red), anyInt(), any(), any())).thenReturn(List.of(other));
        GameEngine twoRules = new GameEngine(
                List.of(turnRule, secondRule), strategyLookup, new ConsecutiveSixVoidRule(), captureRule,
                breakRule, teleportRule, mysteryCell, List.of());
        when(dice.roll()).thenReturn(4);

        twoRules.playTurn(red, everyone, dice, board, publisher);

        verify(strategy).choose(eq(List.of(move, other)), any());
    }

    @Test
    void rollHooksAreToldAboutEachAcceptedRoll() {
        when(dice.roll()).thenReturn(4);
        ArgumentCaptor<RollEvent> event = ArgumentCaptor.forClass(RollEvent.class);

        playTurn();

        verify(rollHook).onRollAccepted(event.capture(), eq(publisher));
        assertSame(red, event.getValue().getPlayer());
        assertEquals(1, event.getValue().getRollNumber());
        assertEquals(4, event.getValue().getRollValue());
    }

    @Test
    void rollHooksRunBeforeTheStrategyChooses() {
        when(dice.roll()).thenReturn(4);

        playTurn();

        InOrder order = inOrder(rollHook, strategy);
        order.verify(rollHook).onRollAccepted(any(), any());
        order.verify(strategy).choose(any(), any());
    }

    @Test
    void aVoidedRollIsNotReportedToTheRollHooks() {
        when(dice.roll()).thenReturn(6, 6, 6);

        playTurn();

        verify(rollHook, times(2)).onRollAccepted(any(), any());
    }

    @Test
    void aForcedBlockadeBreakReplacesTheNormalTurnAndEndsIt() {
        MoveCommand forcedBreak = Commands.forPiece(piece);
        when(breakRule.findForcedBreak(eq(red), anyInt(), anyInt(), any(), any()))
                .thenReturn(Optional.<Command>of(forcedBreak));
        when(dice.roll()).thenReturn(6);

        playTurn();

        verify(forcedBreak).execute(publisher);
        verify(dice, times(1)).roll();
        verifyNoInteractions(strategy);
    }

    @Test
    void aCaptureEarnsAnotherRoll() {
        Command capture = Commands.forPiece(new Piece(PlayerColor.GREEN, 1));
        when(captureRule.findCapture(red, piece, everyone))
                .thenReturn(Optional.of(capture), Optional.empty());
        when(dice.roll()).thenReturn(4, 3);

        playTurn();

        verify(capture).execute(publisher);
        verify(dice, times(2)).roll();
    }

    @Test
    void aMoveThatCapturesNothingEarnsNoExtraRoll() {
        when(dice.roll()).thenReturn(4);

        playTurn();

        verify(dice, times(1)).roll();
    }

    @Test
    void aMessageThatNothingCanMoveIsNeverCheckedForCaptures() {
        MoveCommand nothingMoves = Commands.forPiece(piece);
        when(nothingMoves.movesNothing()).thenReturn(true);
        when(strategy.choose(any(), any())).thenReturn(nothingMoves);
        when(dice.roll()).thenReturn(4);

        playTurn();

        verifyNoInteractions(captureRule);
    }

    @Test
    void aPieceLandingOnTheTrackIsCheckedForAMysteryCellTeleport() {
        when(dice.roll()).thenReturn(4);

        playTurn();

        verify(teleportRule).findTeleport(red, piece);
    }

    @Test
    void aTeleportIsExecutedAfterTheMove() {
        MoveCommand teleport = Commands.forPiece(piece);
        when(teleportRule.findTeleport(red, piece)).thenReturn(Optional.<Command>of(teleport));
        when(dice.roll()).thenReturn(4);

        playTurn();

        InOrder order = inOrder(move, teleport);
        order.verify(move).execute(publisher);
        order.verify(teleport).execute(publisher);
    }

    @Test
    void aTeleportedPieceCanCaptureAndEarnAnotherRoll() {
        Piece teleported = Fixtures.placeOnTrackClockwise(red, 1, 46);
        MoveCommand teleport = Commands.forPiece(teleported);
        Command capture = Commands.forPiece(new Piece(PlayerColor.GREEN, 1));
        // The teleport happens on the first roll only; otherwise every bonus roll would teleport and capture again.
        when(teleportRule.findTeleport(red, piece))
                .thenReturn(Optional.<Command>of(teleport), Optional.<Command>empty());
        when(captureRule.findCapture(red, teleported, everyone)).thenReturn(Optional.of(capture));
        when(dice.roll()).thenReturn(4, 3);

        playTurn();

        verify(capture).execute(publisher);
        verify(dice, times(2)).roll();
    }

    @Test
    void piecesLandingOnTheSameCellAreCheckedForTeleportOnlyOnce() {
        Piece partner = Fixtures.placeOnTrackClockwise(red, 1, 10);
        MoveCommand block = Commands.forPiece(piece);
        when(block.getAffectedPieces()).thenReturn(List.of(piece, partner));
        when(strategy.choose(any(), any())).thenReturn(block);
        when(dice.roll()).thenReturn(4);

        playTurn();

        verify(teleportRule, times(1)).findTeleport(eq(red), any());
    }

    @Test
    void aPieceNoLongerOnTheTrackIsNotCheckedForTeleport() {
        Piece finished = Fixtures.placeHome(red, 1);
        MoveCommand homeMove = Commands.forPiece(finished);
        when(strategy.choose(any(), any())).thenReturn(homeMove);
        when(dice.roll()).thenReturn(4);

        playTurn();

        verify(teleportRule, never()).findTeleport(any(), any());
    }
}
