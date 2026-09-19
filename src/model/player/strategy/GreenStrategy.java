package model.player.strategy;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import model.player.action.Command;
import config.enums.CommandType;
import model.piece.Piece;
import model.player.Player;

// T-17: Green is a block-focused, winning-oriented player. It keeps an empty Base whenever
// possible, prefers forming or continuing a block (T-4) over most other actions, always
// advances a piece toward or into Home before resorting to a move that breaks a block
// apart, and only ever breaks a block when nothing else is legal this turn.
public final class GreenStrategy implements PlayerStrategy {

    @Override
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        // Rule (2): moving any piece toward/into Home always outranks breaking a block.
        Optional<Command> movingHome = findFirst(legalOptions, Command::reachesHome);
        if (movingHome.isPresent()) {
            return movingHome.get();
        }

        // Rule (1): forming a new block outranks keeping Base empty.
        Optional<Command> formingBlock = findFirst(legalOptions, option -> formsNewBlock(option, context));
        if (formingBlock.isPresent()) {
            return formingBlock.get();
        }

        // Rule (2): Green always attempts to move forward using T-4's block movement.
        Optional<Command> continuingBlock = findFirst(legalOptions, Command::movesExistingBlock);
        if (continuingBlock.isPresent()) {
            return continuingBlock.get();
        }

        // Rule (1): otherwise, Green likes to keep an empty Base.
        Optional<Command> enteringFromBase =
                findFirst(legalOptions, option -> option.getType() == CommandType.ENTER_BOARD);
        if (enteringFromBase.isPresent()) {
            return enteringFromBase.get();
        }

        // Rule (3): only break a block once no other legal option remains this turn.
        Optional<Command> avoidingBreak = findFirst(legalOptions, option -> !option.breaksExistingBlock());
        if (avoidingBreak.isPresent()) {
            return avoidingBreak.get();
        }

        return legalOptions.get(0);
    }

    // Rule (1): landing on a cell another Green piece already holds forms a new block.
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
