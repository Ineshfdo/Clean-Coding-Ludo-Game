package model.player.strategy.playstyle;

import config.enums.CommandType;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;

// Green is block-focused: forms and keeps blocks, heads Home, breaks a block last.
public final class GreenStrategy implements PlayerStrategy {

    @Override
    public Command choose(List<Command> legalCommands, StrategyContext context) {
        // Rule (2): moving toward Home outranks breaking a block.
        Optional<Command> homeMove = findFirst(legalCommands, Command::reachesHome);

        if (homeMove.isPresent()) {
            return homeMove.get();
        }

        // Rule (1): forming a new block outranks emptying Base.
        Optional<Command> blockFormingMove = findFirst(legalCommands, command -> formsNewBlock(command, context));

        if (blockFormingMove.isPresent()) {
            return blockFormingMove.get();
        }

        // Rule (2): keep moving an existing block 
        Optional<Command> blockContinuingMove = findFirst(legalCommands, Command::movesExistingBlock);

        if (blockContinuingMove.isPresent()) {
            return blockContinuingMove.get();
        }

        // Rule (1): otherwise, keep Base empty.
        Optional<Command> enterBoardMove =
                findFirst(legalCommands, command -> command.getType() == CommandType.ENTER_BOARD);

        if (enterBoardMove.isPresent()) {
            return enterBoardMove.get();
        }

        // Rule (3): break a block only when nothing else is legal.
        Optional<Command> nonBreakingMove = findFirst(legalCommands, command -> !command.breaksExistingBlock());

        if (nonBreakingMove.isPresent()) {
            return nonBreakingMove.get();
        }

        return legalCommands.get(0);
    }

    // Rule (1): landing on an own piece's cell forms a new block.
    private static boolean formsNewBlock(Command command, StrategyContext context) {
        return command.previewLandingPosition()
                .map(landingPosition -> countOwnPiecesAt(context.getPlayer(), landingPosition) > 0)
                .orElse(false);
    }

    private static int countOwnPiecesAt(Player player, int trackPosition) {
        return (int) player.getPieces().stream()
                .filter(Piece::isOnTrack)
                .filter(piece -> piece.getTrackPosition() == trackPosition)
                .count();
    }

    private static Optional<Command> findFirst(List<Command> legalCommands, Predicate<Command> condition) {
        return legalCommands.stream().filter(condition).findFirst();
    }
}
