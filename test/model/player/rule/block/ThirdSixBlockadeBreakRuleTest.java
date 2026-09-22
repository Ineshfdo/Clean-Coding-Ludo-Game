package model.player.rule.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import config.enums.PlayerColor;
import java.util.List;
import java.util.Optional;
import message.move.PieceLeftBlock;
import message.move.PieceMoved;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.board.LudoBoard;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.move.BreakBlockCommand;
import model.player.rule.home.ApproachPassCountRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Fixtures;

// A third six normally voids the turn; with a blockade on the board it breaks the blockade instead.
@DisplayName("ThirdSixBlockadeBreakRule")
class ThirdSixBlockadeBreakRuleTest {

    private final Board board = LudoBoard.getInstance();
    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final List<Player> everyone = List.of(red, green);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);
    private final BlockadeBreakRule rule =
            new ThirdSixBlockadeBreakRule(new ApproachPassCountRule(), new PassingBlockadeRule());

    private Optional<Command> findBreak(int consecutiveSixCount, int rollValue) {
        return rule.findForcedBreak(red, consecutiveSixCount, rollValue, board, everyone);
    }

    private void redBlockadeOf(int size, int position) {
        for (int index = 0; index < size; index++) {
            Fixtures.placeOnTrackClockwise(red, index, position);
        }
    }

    @Test
    void doesNothingBeforeTheThirdSix() {
        redBlockadeOf(2, 10);

        assertTrue(findBreak(2, 6).isEmpty());
    }

    @Test
    void doesNothingWhenTheThirdRollIsNotASix() {
        redBlockadeOf(2, 10);

        assertTrue(findBreak(3, 5).isEmpty());
    }

    @Test
    void doesNothingWhenThePlayerHasNoBlockade() {
        Fixtures.placeOnTrackClockwise(red, 0, 10);
        Fixtures.placeOnTrackClockwise(red, 1, 20);

        assertTrue(findBreak(3, 6).isEmpty());
    }

    @Test
    void aThirdSixWithABlockadeGivesABreakCommand() {
        redBlockadeOf(2, 10);

        Command command = findBreak(3, 6).orElseThrow();

        assertInstanceOf(BreakBlockCommand.class, command);
    }

    @Test
    void theLowestNumberedPieceStaysAndTheOtherMovesTheFullSix() {
        redBlockadeOf(2, 10);
        Piece stays = red.getPieces().get(0);
        Piece released = red.getPieces().get(1);

        findBreak(3, 6).orElseThrow().execute(publisher);

        assertEquals(10, stays.getTrackPosition());
        assertEquals(16, released.getTrackPosition());
    }

    @Test
    void everyBlockadeMemberIsAnnouncedAsLeavingTheBlock() {
        redBlockadeOf(2, 10);

        findBreak(3, 6).orElseThrow().execute(publisher);

        verify(publisher).publish(new PieceLeftBlock("R1"));
        verify(publisher).publish(new PieceLeftBlock("R2"));
    }

    @Test
    void theReleasedPieceMoveIsAnnounced() {
        redBlockadeOf(2, 10);

        findBreak(3, 6).orElseThrow().execute(publisher);

        verify(publisher).publish(new PieceMoved(PlayerColor.RED, "R2", 10, 16, 6, "Clockwise"));
    }

    @Test
    void theAffectedPiecesAreTheOnesThatMoved() {
        redBlockadeOf(2, 10);
        Piece released = red.getPieces().get(1);

        List<Piece> affected = findBreak(3, 6).orElseThrow().getAffectedPieces();

        assertEquals(List.of(released), affected);
    }

    @Test
    void theSixIsSharedBetweenSeveralReleasedPieces() {
        redBlockadeOf(3, 10);

        findBreak(3, 6).orElseThrow().execute(publisher);

        // Two released pieces, so 6 / 2 = 3 cells each.
        assertEquals(13, red.getPieces().get(1).getTrackPosition());
        assertEquals(13, red.getPieces().get(2).getTrackPosition());
        assertEquals(10, red.getPieces().get(0).getTrackPosition());
    }

    @Test
    void aReleasedPieceStopsBeforeAnOpponentBlockade() {
        redBlockadeOf(2, 10);
        Fixtures.placeOnTrackClockwise(green, 0, 14);
        Fixtures.placeOnTrackClockwise(green, 1, 14);

        findBreak(3, 6).orElseThrow().execute(publisher);

        assertEquals(13, red.getPieces().get(1).getTrackPosition());
    }

    @Test
    void aReleasedPieceThatCannotMoveStaysPutButStillLeavesTheBlock() {
        redBlockadeOf(2, 10);
        Fixtures.placeOnTrackClockwise(green, 0, 11);
        Fixtures.placeOnTrackClockwise(green, 1, 11);
        Command command = findBreak(3, 6).orElseThrow();

        command.execute(publisher);

        assertEquals(10, red.getPieces().get(1).getTrackPosition());
        verify(publisher).publish(new PieceLeftBlock("R2"));
        assertEquals(List.of(red.getPieces().get(0)), command.getAffectedPieces());
    }
}
