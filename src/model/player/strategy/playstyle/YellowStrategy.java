package model.player.strategy.playstyle;

import config.enums.CommandType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import model.piece.Piece;
import model.player.command.Command;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import model.player.strategy.helper.CaptureTargetFinder;
import model.position.RemainingHomeDistance;

// Yellow is winning-focused: leaves Base, captures only when needed, else nears Home.
public final class YellowStrategy implements PlayerStrategy {

    private static final int REQUIRED_CAPTURES_FOR_HOME_STRAIGHT = 1;

    @Override
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        // Rule (1): keep Base empty; a six entering the board comes first.
        Optional<Command> enteringFromBase =
            findFirst(legalOptions, option -> option.getType() == CommandType.ENTER_BOARD);

        if (enteringFromBase.isPresent()) {
            return enteringFromBase.get();
        }

        // Rule (2): capture only for a piece that still needs one.
        Optional<Command> neededCapture = findFirst(legalOptions, option -> capturesForAPieceThatNeedsOne(option, context));

        if (neededCapture.isPresent()) {
            return neededCapture.get();
        }

        // Rule (3): otherwise advance the piece closest to Home.
        return findClosestToHome(legalOptions, context).orElse(legalOptions.get(0));
    }

    private static boolean capturesForAPieceThatNeedsOne(Command option, StrategyContext context) {
        return pieceStillNeedsCapture(option.getAffectedPiece())
                && CaptureTargetFinder.findTarget(option, context).isPresent();
    }

    // A piece needs a capture before entering its HomeStraight.
    private static boolean pieceStillNeedsCapture(Piece piece) {
        return piece.getCaptureCount() < REQUIRED_CAPTURES_FOR_HOME_STRAIGHT;
    }

    private static Optional<Command> findClosestToHome(List<Command> legalOptions, StrategyContext context) {
        return legalOptions.stream()
            .min(Comparator.comparingInt(
                option -> RemainingHomeDistance.forPiece(option.getAffectedPiece(), context.getBoard())));
    }

    private static Optional<Command> findFirst(List<Command> legalOptions, Predicate<Command> condition) {
        return legalOptions.stream().filter(condition).findFirst();
    }
}
