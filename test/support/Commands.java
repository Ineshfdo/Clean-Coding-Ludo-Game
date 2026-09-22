package support;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import model.board.LudoBoard;
import model.effect.mysterycell.MysteryCellLocation;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.StrategyContext;

// Stubbed commands and contexts for strategy tests: each strategy only reads what a command reports.
public final class Commands {

    private Commands() {
    }

    // A command that moves the piece and reports the given landing cell (or none).
    public static MoveCommand movingTo(Piece piece, Integer landingCell) {
        MoveCommand command = forPiece(piece);

        when(command.previewLandingPosition()).thenReturn(Optional.ofNullable(landingCell));

        return command;
    }

    public static MoveCommand forPiece(Piece piece) {
        MoveCommand command = mock(MoveCommand.class);

        when(command.getAffectedPiece()).thenReturn(piece);
        when(command.getAffectedPieces()).thenReturn(List.of(piece));

        return command;
    }

    public static MoveCommand entering(Piece piece) {
        MoveCommand command = forPiece(piece);

        when(command.entersBoard()).thenReturn(true);

        return command;
    }

    public static MysteryCellLocation mysteryCellAt(int position) {
        MysteryCellLocation location = mock(MysteryCellLocation.class);

        when(location.isActive()).thenReturn(true);
        when(location.getCurrentCellPosition()).thenReturn(position);

        return location;
    }

    public static MysteryCellLocation noMysteryCell() {
        MysteryCellLocation location = mock(MysteryCellLocation.class);

        when(location.isActive()).thenReturn(false);

        return location;
    }

    public static StrategyContext contextFor(Player player, List<Player> everyone, int rollNumber) {
        return new StrategyContext(player, everyone, LudoBoard.getInstance(), noMysteryCell(), rollNumber);
    }

    public static StrategyContext contextFor(
            Player player, List<Player> everyone, MysteryCellLocation location, int rollNumber) {
        return new StrategyContext(player, everyone, LudoBoard.getInstance(), location, rollNumber);
    }
}
