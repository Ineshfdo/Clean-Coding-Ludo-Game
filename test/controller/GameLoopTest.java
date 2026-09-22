package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import config.enums.PlayerColor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import message.game.BoardStateReported;
import message.game.GameOver;
import message.game.RoundStarted;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.board.LudoBoard;
import model.effect.restriction.BetaRestrictedState;
import model.piece.Piece;
import model.player.Player;
import model.round.RoundListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import support.Fixtures;
import utils.dice.Dice;

// The engine is a mock that plays no real turns; each test says which players finish on which turn.
@DisplayName("GameLoop")
class GameLoopTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final List<Player> everyone = List.of(red, green);
    private final Board board = LudoBoard.getInstance();

    private final GameEngine engine = mock(GameEngine.class);
    private final RoundListener roundListener = mock(RoundListener.class);
    private final Dice dice = mock(Dice.class);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);
    private final GameLoop loop = new GameLoop(engine, roundListener, dice, board, publisher);

    // When the engine is asked to play this player's turn, that player's pieces all reach Home.
    private void finishesOnTurnNumber(Player player, int turnNumber) {
        AtomicInteger turnsTaken = new AtomicInteger();

        doAnswer(invocation -> {
            if (turnsTaken.incrementAndGet() == turnNumber) {
                Fixtures.sendEveryPieceHome(player);
            }
            return null;
        }).when(engine).playTurn(eq(player), any(), any(), any(), any());
    }

    @Test
    void noRoundIsPlayedWhenEveryoneHasAlreadyFinished() {
        Fixtures.sendEveryPieceHome(red);
        Fixtures.sendEveryPieceHome(green);

        loop.play(everyone, everyone);

        verifyNoInteractions(engine, roundListener);
        verify(publisher).publish(new GameOver(List.of()));
    }

    @Test
    void aRoundRunsInOrderAnnounceListenTurnListenReport() {
        finishesOnTurnNumber(red, 1);

        loop.play(List.of(red), List.of(red));

        InOrder order = inOrder(publisher, roundListener, engine);
        order.verify(publisher).publish(new RoundStarted(1));
        order.verify(roundListener).onRoundStarted(1, List.of(red), publisher);
        order.verify(engine).playTurn(red, List.of(red), dice, board, publisher);
        order.verify(roundListener).onRoundCompleted(1, List.of(red));
        order.verify(publisher).publish(new BoardStateReported(1));
        order.verify(publisher).publish(new GameOver(List.of(PlayerColor.RED)));
    }

    @Test
    void roundsAreNumberedFromOneUpwards() {
        finishesOnTurnNumber(red, 3);

        loop.play(List.of(red), List.of(red));

        verify(publisher).publish(new RoundStarted(1));
        verify(publisher).publish(new RoundStarted(2));
        verify(publisher).publish(new RoundStarted(3));
        verify(publisher, never()).publish(new RoundStarted(4));
    }

    @Test
    void theRoundListenerIsToldEachRoundStartAndEnd() {
        finishesOnTurnNumber(red, 2);

        loop.play(List.of(red), List.of(red));

        verify(roundListener).onRoundStarted(1, List.of(red), publisher);
        verify(roundListener).onRoundStarted(2, List.of(red), publisher);
        verify(roundListener).onRoundCompleted(1, List.of(red));
        verify(roundListener).onRoundCompleted(2, List.of(red));
    }

    @Test
    void aPlayerWhoHasFinishedTakesNoTurn() {
        Fixtures.sendEveryPieceHome(red);
        finishesOnTurnNumber(green, 1);

        loop.play(everyone, everyone);

        verify(engine, never()).playTurn(eq(red), any(), any(), any(), any());
        verify(engine).playTurn(green, everyone, dice, board, publisher);
    }

    @Test
    void playersTakeTurnsInTheGivenTurnOrder() {
        finishesOnTurnNumber(red, 1);
        finishesOnTurnNumber(green, 1);

        loop.play(everyone, List.of(green, red));

        InOrder order = inOrder(engine);
        order.verify(engine).playTurn(eq(green), any(), any(), any(), any());
        order.verify(engine).playTurn(eq(red), any(), any(), any(), any());
    }

    @Test
    void standingsFollowTheOrderPlayersFinishedIn() {
        finishesOnTurnNumber(red, 1);
        finishesOnTurnNumber(green, 2);

        loop.play(everyone, everyone);

        verify(publisher).publish(new GameOver(List.of(PlayerColor.RED, PlayerColor.GREEN)));
    }

    @Test
    void twoPlayersFinishingInTheSameRoundAreRankedByTurnOrder() {
        finishesOnTurnNumber(red, 1);
        finishesOnTurnNumber(green, 1);

        loop.play(everyone, List.of(green, red));

        verify(publisher).publish(new GameOver(List.of(PlayerColor.GREEN, PlayerColor.RED)));
    }

    @Test
    void aFinishedPlayerIsRecordedOnlyOnce() {
        finishesOnTurnNumber(red, 1);
        finishesOnTurnNumber(green, 3);

        loop.play(everyone, everyone);

        verify(publisher).publish(new GameOver(List.of(PlayerColor.RED, PlayerColor.GREEN)));
    }

    @Test
    void everyPlayersRestrictionsAreUsedUpAtTheStartOfEachRound() {
        Piece restricted = Fixtures.placeOnTrackClockwise(red, 0, 27);
        red.applyRestriction(restricted, new BetaRestrictedState());
        finishesOnTurnNumber(red, 2);

        loop.play(List.of(red), List.of(red));

        // Two rounds started, so two rounds of the four were used up.
        assertEquals(2, restricted.getRestrictionState().getRoundsRemaining());
    }

    @Test
    void recordingAFinisherAlreadyInTheStandingsLeavesThemUnchanged() throws Exception {
        // The public loop never calls this twice for the same player: once recorded, a player is
        // skipped by the "already finished" guard before their next round. Reached directly here
        // to prove the private guard still holds on its own, not just because of that skip.
        Method recordFinisherIfNewlyDone =
                GameLoop.class.getDeclaredMethod("recordFinisherIfNewlyDone", Player.class, List.class);
        recordFinisherIfNewlyDone.setAccessible(true);
        Fixtures.sendEveryPieceHome(red);
        List<PlayerColor> standings = new ArrayList<>(List.of(PlayerColor.RED));

        recordFinisherIfNewlyDone.invoke(null, red, standings);

        assertEquals(List.of(PlayerColor.RED), standings);
    }
}
