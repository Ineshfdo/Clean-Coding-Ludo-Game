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
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        // Rule (2): moving toward Home outranks breaking a block.
        Optional<Command> movingHome = findFirst(legalOptions, Command::reachesHome);

        if (movingHome.isPresent()) {
            return movingHome.get();
        }

        // Rule (1): forming a new block outranks emptying Base.
        Optional<Command> formingBlock = findFirst(legalOptions, option -> formsNewBlock(option, context));

        if (formingBlock.isPresent()) {
            return formingBlock.get();
        }

        // Rule (2): keep moving an existing block 
        Optional<Command> continuingBlock = findFirst(legalOptions, Command::movesExistingBlock);

        if (continuingBlock.isPresent()) {
            return continuingBlock.get();
        }

        // Rule (1): otherwise, keep Base empty.
        Optional<Command> enteringFromBase =
                findFirst(legalOptions, option -> option.getType() == CommandType.ENTER_BOARD);

        if (enteringFromBase.isPresent()) {
            return enteringFromBase.get();
        }

        // Rule (3): break a block only when nothing else is legal.
        Optional<Command> avoidingBreak = findFirst(legalOptions, option -> !option.breaksExistingBlock());

        if (avoidingBreak.isPresent()) {
            return avoidingBreak.get();
        }

        return legalOptions.get(0);
    }

    // Rule (1): landing on an own piece's cell forms a new block.
    private static boolean formsNewBlock(Command option, StrategyContext context) {
        return option.previewLandingPosition()
                .map(landingPosition -> countOwnPiecesAt(context.getPlayer(), landingPosition) > 0)
                .orElse(false);
    }

    private static int countOwnPiecesAt(Player player, int trackPosition) {
        return (int) player.getPieces().stream()
                .filter(Piece::isOnTrack)
                .filter(piece -> piece.getTrackPosition() == trackPosition)
                .count();
    }

    private static Optional<Command> findFirst(List<Command> legalOptions, Predicate<Command> condition) {
        return legalOptions.stream().filter(condition).findFirst();
    }
}
