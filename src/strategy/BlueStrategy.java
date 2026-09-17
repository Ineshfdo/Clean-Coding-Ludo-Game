package strategy;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import command.Command;
import direction.MovementDirectionStrategy;
import mysterycell.MysteryCellLocation;
import player.Piece;
import player.Player;

// T-19: Blue is a mostly-random player. Behavior (1): by default it moves through its own 4
// pieces in a fixed rotation (B1 -> B2 -> B3 -> B4 -> B1 -> ...). Whichever piece actually
// moves this turn - through the rotation itself, or through a Behavior (2)/(3) Mystery Cell
// override - is what the rotation resumes from next turn, exactly one step later. A bonus
// roll from a 6 or a capture still belongs to the SAME turn, so it keeps considering the same
// piece rather than advancing again.
public final class BlueStrategy implements PlayerStrategy {

    private static final int FIRST_ROLL_OF_TURN = 1;

    private Piece lastMovedPiece;
    private Piece consideredPieceForCurrentTurn;

    @Override
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        if (context.getRollNumber() == FIRST_ROLL_OF_TURN) {
            consideredPieceForCurrentTurn = resolveConsideredPiece(context.getPlayer(), legalOptions);
        }

        Command chosenCommand = chooseCommand(legalOptions, context);
        lastMovedPiece = chosenCommand.getAffectedPiece();
        return chosenCommand;
    }

    private Command chooseCommand(List<Command> legalOptions, StrategyContext context) {
        // Rule (2): a counter-clockwise piece landing on the Mystery Cell always wins.
        Optional<Command> counterClockwiseMysteryLanding = findFirst(legalOptions,
                option -> isCounterClockwise(option) && landsOnMysteryCell(option, context));
        if (counterClockwiseMysteryLanding.isPresent()) {
            return counterClockwiseMysteryLanding.get();
        }

        // Rule (1): the piece the fixed rotation is considering this turn.
        Command cyclicChoice = findOptionFor(legalOptions, consideredPieceForCurrentTurn)
                .orElse(legalOptions.get(0));

        // Rule (3): a clockwise piece avoids the Mystery Cell when a legal alternative exists.
        return avoidMysteryCellIfClockwise(cyclicChoice, legalOptions, context);
    }

    // T-19/Iterator: resumes the rotation right after whichever piece last actually moved, then
    // advances through it until finding a piece this turn's legalOptions actually names - a
    // piece still at Base (or otherwise unable to move) cannot be "considered" this turn, so it
    // is skipped rather than forced into an arbitrary fallback.
    private Piece resolveConsideredPiece(Player player, List<Command> legalOptions) {
        List<Piece> pieces = player.getPieces();
        Iterator<Piece> rotation = new CyclicPieceIterator(pieces, lastMovedPiece);
        Piece firstInRotation = null;
        for (int attempt = 0; attempt < pieces.size(); attempt++) {
            Piece candidate = rotation.next();
            if (firstInRotation == null) {
                firstInRotation = candidate;
            }
            if (findOptionFor(legalOptions, candidate).isPresent()) {
                return candidate;
            }
        }
        // Every legal option always names one of this player's own 4 pieces, so this is
        // unreachable - kept only so the method has a well-typed result.
        return firstInRotation;
    }

    private static Command avoidMysteryCellIfClockwise(
            Command cyclicChoice, List<Command> legalOptions, StrategyContext context) {
        if (!isClockwise(cyclicChoice) || !landsOnMysteryCell(cyclicChoice, context)) {
            return cyclicChoice;
        }
        return findFirst(legalOptions, option -> !landsOnMysteryCell(option, context))
                .orElse(cyclicChoice);
    }

    // T-1: a Base piece has no assigned direction yet - direction is only set on Base exit -
    // so it is treated as neither clockwise nor counter-clockwise here. It can never preview a
    // landing position anyway, so this only affects which rule branch considers it.
    private static Optional<MovementDirectionStrategy> currentDirectionOf(Command option) {
        return Optional.ofNullable(option.getAffectedPiece().getMovementDirectionStrategy());
    }

    private static boolean isClockwise(Command option) {
        return currentDirectionOf(option).map(MovementDirectionStrategy::isClockwise).orElse(false);
    }

    private static boolean isCounterClockwise(Command option) {
        return currentDirectionOf(option).map(direction -> !direction.isClockwise()).orElse(false);
    }

    private static boolean landsOnMysteryCell(Command option, StrategyContext context) {
        MysteryCellLocation mysteryCellLocation = context.getMysteryCellLocation();
        if (!mysteryCellLocation.isActive()) {
            return false;
        }
        return option.previewLandingPosition()
                .map(landingPosition -> landingPosition == mysteryCellLocation.getCurrentCellPosition())
                .orElse(false);
    }

    private static Optional<Command> findOptionFor(List<Command> legalOptions, Piece piece) {
        return legalOptions.stream()
                .filter(option -> option.getAffectedPiece() == piece)
                .findFirst();
    }

    private static Optional<Command> findFirst(List<Command> legalOptions, Predicate<Command> condition) {
        return legalOptions.stream().filter(condition).findFirst();
    }
}
