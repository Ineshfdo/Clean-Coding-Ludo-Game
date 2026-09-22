package model.player.command.cannotmove;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import config.enums.PlayerColor;
import java.util.List;
import message.cannotmove.BlockRollTooSmall;
import message.cannotmove.EffectRollTooSmall;
import message.cannotmove.PieceBlocked;
import message.cannotmove.PieceNeedsExactRoll;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.command.MoveCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Cannot-move commands")
class CannotMoveCommandsTest {

    private final Piece first = new Piece(PlayerColor.RED, 1);
    private final Piece second = new Piece(PlayerColor.RED, 2);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);

    @Test
    void blockRollTooSmallAnnouncesTheBlockCannotMove() {
        new BlockRollTooSmallCommand(first).execute(publisher);

        verify(publisher).publish(new BlockRollTooSmall("R1"));
    }

    @Test
    void exactRollRequiredAnnouncesAnExactRollIsNeeded() {
        new ExactRollRequiredCommand(first).execute(publisher);

        verify(publisher).publish(new PieceNeedsExactRoll("R1"));
    }

    @Test
    void sickRollTooSmallAnnouncesTheEffectLeftNothingToMove() {
        new SickRollTooSmallCommand(first).execute(publisher);

        verify(publisher).publish(new EffectRollTooSmall("R1"));
    }

    @Test
    void moveBlockedNamesEveryPieceOfTheBlock() {
        new MoveBlockedByBlockadeCommand(List.of(first, second)).execute(publisher);

        verify(publisher).publish(new PieceBlocked("R1+R2"));
    }

    @Test
    void moveBlockedReportsTheFirstPieceAsTheAffectedOne() {
        MoveCommand command = new MoveBlockedByBlockadeCommand(List.of(first, second));

        assertSame(first, command.getAffectedPiece());
    }

    @Test
    void everyCannotMoveCommandMovesNothing() {
        List<MoveCommand> commands = List.of(
                new BlockRollTooSmallCommand(first),
                new ExactRollRequiredCommand(first),
                new SickRollTooSmallCommand(first),
                new MoveBlockedByBlockadeCommand(List.of(first)));

        for (MoveCommand command : commands) {
            assertTrue(command.movesNothing(), command.getClass().getSimpleName());
        }
    }

    @Test
    void aCannotMoveCommandReportsItsPieceAsTheOnlyAffectedPiece() {
        MoveCommand command = new ExactRollRequiredCommand(first);

        assertSame(first, command.getAffectedPiece());
        assertEquals(List.of(first), command.getAffectedPieces());
    }

    @Test
    void aCannotMoveCommandDoesNothingElseAStrategyCaresAbout() {
        MoveCommand command = new SickRollTooSmallCommand(first);

        assertFalse(command.entersBoard());
        assertFalse(command.reachesHome());
        assertFalse(command.movesExistingBlock());
        assertFalse(command.breaksExistingBlock());
        assertFalse(command.leavesStandardPath());
        assertTrue(command.previewLandingPosition().isEmpty());
    }

    @Test
    void executingChangesNothingOnThePiece() {
        new ExactRollRequiredCommand(first).execute(publisher);

        assertTrue(first.isAtBase());
        verify(publisher).publish(new PieceNeedsExactRoll("R1"));
        verifyNoMoreInteractions(publisher);
    }
}
