package model.player.strategy.playstyle;

import java.util.List;
import java.util.Optional;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import model.player.strategy.helper.CommandFinder;

/**
 Green is blockade-focused.
 It moves toward Home first, then forms a new blockade, keeps moving an existing blockade, and leaves Base.
 It breaks a blockade only when nothing else is legal.
 */
public final class GreenStrategy implements PlayerStrategy {

    @Override
    public MoveCommand choose(List<MoveCommand> legalCommands, StrategyContext context) {
        // Rule (2): moving toward Home outranks breaking a block.
        Optional<MoveCommand> homeMove = CommandFinder.findFirst(legalCommands, MoveCommand::reachesHome);

        if (homeMove.isPresent()) {
            return homeMove.get();
        }

        // Rule (1): forming a new block outranks emptying Base.
        Optional<MoveCommand> blockFormingMove = CommandFinder.findFirst(legalCommands, command -> formsNewBlock(command, context));

        if (blockFormingMove.isPresent()) {
            return blockFormingMove.get();
        }

        // Rule (2): keep moving an existing block 
        Optional<MoveCommand> blockContinuingMove = CommandFinder.findFirst(legalCommands, MoveCommand::movesExistingBlock);

        if (blockContinuingMove.isPresent()) {
            return blockContinuingMove.get();
        }

        // Rule (1): otherwise, keep Base empty.
        Optional<MoveCommand> enterBoardMove =
                CommandFinder.findFirst(legalCommands, command -> command.entersBoard());

        if (enterBoardMove.isPresent()) {
            return enterBoardMove.get();
        }

        // Rule (3): break a block only when nothing else is legal.
        Optional<MoveCommand> nonBreakingMove = CommandFinder.findFirst(legalCommands, command -> !command.breaksExistingBlock());

        if (nonBreakingMove.isPresent()) {
            return nonBreakingMove.get();
        }

        return legalCommands.get(0);
    }

    // Rule (1): landing on an own piece's cell forms a new block.
    private static boolean formsNewBlock(MoveCommand command, StrategyContext context) {
        return command.previewLandingPosition()
                .map(landingPosition -> !context.getPlayer().getPiecesAt(landingPosition).isEmpty())
                .orElse(false);
    }
}
