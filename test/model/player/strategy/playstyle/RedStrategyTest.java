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

// Red is capture-focused: capture first, then leave Base, keep a piece on the path and avoid forming blocks.
// Red enters at cell 28 and its Approach is cell 26. Green's Approach is cell 39.
@DisplayName("RedStrategy")
class RedStrategyTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final StrategyContext context = Commands.contextFor(red, List.of(red, green), 1);
    private final PlayerStrategy strategy = new RedStrategy();

    private final Piece first = Fixtures.placeOnTrackClockwise(red, 0, 10);
    private final Piece second = Fixtures.placeOnTrackClockwise(red, 1, 12);
    private final Piece atBase = red.getPieces().get(2);

    private static MoveCommand leavingThePath(MoveCommand command) {
        when(command.leavesStandardPath()).thenReturn(true);
        return command;
    }

    private static MoveCommand movingNothing(MoveCommand command) {
        when(command.movesNothing()).thenReturn(true);
        return command;
    }

    @Test
    void aCaptureBeatsLeavingBase() {
        Fixtures.placeOnTrackClockwise(green, 0, 30);
        MoveCommand enter = Commands.entering(atBase);
        MoveCommand capture = Commands.movingTo(first, 30);

        assertSame(capture, strategy.choose(List.of(enter, capture), context));
    }

    @Test
    void amongCapturesTheOpponentClosestToItsHomeIsTargeted() {
        // Green's Approach is cell 39: a Green piece on 36 is nearer Home than one on 30.
        Fixtures.placeOnTrackClockwise(green, 0, 30);
        Fixtures.placeOnTrackClockwise(green, 1, 36);
        MoveCommand farTarget = Commands.movingTo(first, 30);
        MoveCommand nearTarget = Commands.movingTo(second, 36);

        assertSame(nearTarget, strategy.choose(List.of(farTarget, nearTarget), context));
    }

    @Test
    void withNoCaptureLeavingBaseComesNext() {
        MoveCommand plain = Commands.movingTo(first, 14);
        MoveCommand enter = Commands.entering(atBase);

        assertSame(enter, strategy.choose(List.of(plain, enter), context));
    }

    @Test
    void leavingBaseIsSkippedWhenItWouldFormABlockOnTheEntryCell() {
        Fixtures.placeOnTrackClockwise(red, 3, 28);
        MoveCommand plain = Commands.movingTo(first, 14);
        MoveCommand enter = Commands.entering(atBase);

        assertSame(plain, strategy.choose(List.of(enter, plain), context));
    }

    @Test
    void aMoveThatFormsABlockIsAvoidedWhenAnotherMoveDoesNot() {
        MoveCommand formsBlock = Commands.movingTo(first, 12);
        MoveCommand free = Commands.movingTo(first, 14);

        assertSame(free, strategy.choose(List.of(formsBlock, free), context));
    }

    @Test
    void aMoveThatWouldEmptyThePathIsAvoided() {
        red.returnToBase(second);
        MoveCommand leaving = leavingThePath(Commands.movingTo(first, 14));
        MoveCommand staying = Commands.movingTo(first, 16);

        assertSame(staying, strategy.choose(List.of(leaving, staying), context));
    }

    @Test
    void leavingThePathIsFineWhileAnotherRedPieceStaysOnIt() {
        MoveCommand leaving = leavingThePath(Commands.movingTo(first, 14));
        MoveCommand staying = Commands.movingTo(second, 16);

        assertSame(leaving, strategy.choose(List.of(leaving, staying), context));
    }

    @Test
    void whenEveryMoveStrandsThePiecesTheOneThatFormsNoBlockIsUsed() {
        red.returnToBase(second);
        MoveCommand firstLeaving = leavingThePath(Commands.movingTo(first, 14));
        MoveCommand secondLeaving = leavingThePath(Commands.movingTo(first, 16));

        assertSame(firstLeaving, strategy.choose(List.of(firstLeaving, secondLeaving), context));
    }

    @Test
    void whenEveryMoveStrandsThePiecesABlockFormingOneIsSkippedForOneThatDoesNot() {
        // Both moves strand every on-track piece, so priorities (1)/(2) both fail. Unlike the
        // block-forming candidate, the plain one is evaluated second, so this also confirms the
        // fallback keeps looking past a rejected candidate instead of stopping at it.
        MoveCommand blockForming = leavingThePath(Commands.movingTo(first, 12));
        when(blockForming.getAffectedPieces()).thenReturn(List.of(first, second));
        MoveCommand free = leavingThePath(Commands.movingTo(first, 40));
        when(free.getAffectedPieces()).thenReturn(List.of(first, second));

        assertSame(free, strategy.choose(List.of(blockForming, free), context));
    }

    @Test
    void whenNothingCanMoveTheFirstCommandIsReturned() {
        MoveCommand firstMessage = movingNothing(Commands.forPiece(first));
        MoveCommand secondMessage = movingNothing(Commands.forPiece(second));

        assertSame(firstMessage, strategy.choose(List.of(firstMessage, secondMessage), context));
    }

    @Test
    void aRealMoveBeatsAMessageThatNothingCanMove() {
        MoveCommand message = movingNothing(Commands.forPiece(first));
        MoveCommand move = Commands.movingTo(second, 16);

        assertSame(move, strategy.choose(List.of(message, move), context));
    }
}
