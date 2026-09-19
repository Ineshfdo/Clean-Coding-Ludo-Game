package model.player.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import model.player.command.Command;
import config.enums.CommandType;
import model.piece.Piece;
import model.position.RemainingHomeDistance;

// T-18: Yellow is a winning-focused player. It keeps Base empty whenever a six allows it;
// otherwise, it prioritizes a capture only for a piece that still needs one to become
// HomeStraight-eligible (T-7); if no such capture is available, it advances whichever piece
// is closest to Home.
public final class YellowStrategy implements PlayerStrategy {

    private static final int REQUIRED_CAPTURES_FOR_HOME_STRAIGHT = 1;

    @Override
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        // Rule (1): keep Base empty - a six bringing a new piece to X always comes first.
        Optional<Command> enteringFromBase =
                findFirst(legalOptions, option -> option.getType() == CommandType.ENTER_BOARD);
        if (enteringFromBase.isPresent()) {
            return enteringFromBase.get();
        }

        // Rule (2): otherwise, take a capture - but only for a piece that still needs one.
        Optional<Command> neededCapture = findFirst(legalOptions, option -> capturesForAPieceThatNeedsOne(option, context));
        if (neededCapture.isPresent()) {
            return neededCapture.get();
        }

        // Rule (3): no such capture available - advance whichever piece is closest to Home.
        return findClosestToHome(legalOptions, context).orElse(legalOptions.get(0));
    }

    private static boolean capturesForAPieceThatNeedsOne(Command option, StrategyContext context) {
        return pieceStillNeedsCapture(option.getAffectedPiece())
                && CaptureOpportunityFinder.findCapturedOpponent(option, context).isPresent();
    }

    // T-7: a piece needs at least one capture before it may enter its HomeStraight.
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
