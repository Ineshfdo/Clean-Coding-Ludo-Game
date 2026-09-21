package model.player.strategy.playstyle;

import config.constant.TurnConstants;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.piece.RemainingHomeDistance;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import model.player.strategy.helper.CaptureTargetFinder;
import model.player.strategy.helper.CommandFinder;

// Yellow is winning-focused: leaves Base, captures only when needed, else nears Home.
public final class YellowStrategy implements PlayerStrategy {

    @Override
    public MoveCommand choose(List<MoveCommand> legalCommands, StrategyContext context) {
        // Rule (1): keep Base empty; a six entering the board comes first.
        Optional<MoveCommand> enterBoardMove =
            CommandFinder.findFirst(legalCommands, command -> command.entersBoard());

        if (enterBoardMove.isPresent()) {
            return enterBoardMove.get();
        }

        // Rule (2): capture only for a piece that still needs one.
        Optional<MoveCommand> neededCapture = CommandFinder.findFirst(legalCommands, command -> capturesForAPieceThatNeedsOne(command, context));

        if (neededCapture.isPresent()) {
            return neededCapture.get();
        }

        // Rule (3): otherwise advance the piece closest to Home.
        return findClosestToHome(legalCommands, context).orElse(legalCommands.get(0));
    }

    private static boolean capturesForAPieceThatNeedsOne(MoveCommand command, StrategyContext context) {
        return pieceStillNeedsCapture(command.getAffectedPiece())
                && CaptureTargetFinder.findTarget(command, context).isPresent();
    }

    // A piece needs a capture before entering its HomeStraight.
    private static boolean pieceStillNeedsCapture(Piece piece) {
        return piece.getCaptureCount() < TurnConstants.REQUIRED_CAPTURES_TO_ENTER_HOME_STRAIGHT;
    }

    private static Optional<MoveCommand> findClosestToHome(List<MoveCommand> legalCommands, StrategyContext context) {
        return legalCommands.stream()
            .min(Comparator.comparingInt(
                command -> RemainingHomeDistance.forPiece(command.getAffectedPiece(), context.getBoard())));
    }
}
