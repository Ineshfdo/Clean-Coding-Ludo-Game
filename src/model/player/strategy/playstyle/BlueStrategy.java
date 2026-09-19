package model.player.strategy.playstyle;

import config.constant.TurnConstants;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import model.direction.MovementDirectionStrategy;
import model.effect.mysterycell.MysteryCellLocation;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import model.player.strategy.helper.BluePieceRotationIterator;

// Blue rotates through its pieces (B1 -> B2 -> B3 -> B4), with Mystery Cell overrides.
// A bonus roll in the same turn keeps considering the same piece.
public final class BlueStrategy implements PlayerStrategy {

    private Piece lastMovedPiece;
    private Piece consideredPieceForCurrentTurn;

    @Override
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        if (context.getRollNumber() == TurnConstants.FIRST_ROLL_OF_TURN) {
            consideredPieceForCurrentTurn = resolveConsideredPiece(context.getPlayer(), legalOptions);
        }

        Command chosenCommand = chooseCommand(legalOptions, context);
        lastMovedPiece = chosenCommand.getAffectedPiece();

        return chosenCommand;
    }

    // Mystery Cell rules (2)/(3) only override the rotation's own choice when needed.
    private Command chooseCommand(List<Command> legalOptions, StrategyContext context) {
        // Rule (1): the piece the rotation is considering this turn.
        Command cyclicChoice = findOptionFor(legalOptions, consideredPieceForCurrentTurn)
                .orElse(legalOptions.get(0));
        boolean cyclicChoiceLandsOnMysteryCell = landsOnMysteryCell(cyclicChoice, context);

        if (isCounterClockwise(cyclicChoice) && cyclicChoiceLandsOnMysteryCell) {
            // Rule (2): counter-clockwise onto the Mystery Cell is preferred, so keep it.
            return cyclicChoice;
        }

        if (isClockwise(cyclicChoice) && cyclicChoiceLandsOnMysteryCell) {
            // Rule (3): clockwise onto the Mystery Cell: switch to an option that avoids it.
            return findFirst(legalOptions, option -> !landsOnMysteryCell(option, context))
                    .orElse(cyclicChoice);
        }

        // Rule (2): otherwise, look for a counter-clockwise option that lands on it.
        return findFirst(legalOptions,
                option -> isCounterClockwise(option) && landsOnMysteryCell(option, context))
                .orElse(cyclicChoice);
    }

    // Resumes after the last moved piece, skipping pieces with no legal option.
    private Piece resolveConsideredPiece(Player player, List<Command> legalOptions) {
        List<Piece> pieces = player.getPieces();
        Iterator<Piece> rotation = new BluePieceRotationIterator(pieces, lastMovedPiece);
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

        // Unreachable: every legal option names one of the player's pieces.
        return firstInRotation;
    }

    // Base piece has no direction yet, so it is neither clockwise nor counter-clockwise.
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
