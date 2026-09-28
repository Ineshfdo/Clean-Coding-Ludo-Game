package model.player.strategy.playstyle;

import config.constant.TurnConstants;
import exception.IllegalMoveException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import model.direction.MovementDirectionStrategy;
import model.effect.mysterycell.MysteryCellLocation;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import model.player.strategy.helper.BluePieceRotationIterator;
import model.player.strategy.helper.CommandFinder;

/**
 Blue moves its pieces in rotation: B1, B2, B3, B4 and then B1 again.
 A bonus roll in the same turn keeps considering the same piece.
 Two Mystery Cell rules can override the rotation: a counter-clockwise move onto the Mystery Cell is preferred, and a clockwise move onto it is avoided.
 */
public final class BlueStrategy implements PlayerStrategy {

    private Piece lastMovedPiece;
    private Piece consideredPieceForCurrentTurn;

    @Override
    public MoveCommand choose(List<MoveCommand> legalCommands, StrategyContext context) {
        if (context.getRollNumber() == TurnConstants.FIRST_ROLL_OF_TURN) {
            consideredPieceForCurrentTurn = resolveConsideredPiece(context.getPlayer(), legalCommands);
        }

        MoveCommand chosenCommand = chooseCommand(legalCommands, context);
        lastMovedPiece = chosenCommand.getAffectedPiece();

        return chosenCommand;
    }

    // Mystery Cell rules (2)/(3) only override the rotation's own choice when needed.
    private MoveCommand chooseCommand(List<MoveCommand> legalCommands, StrategyContext context) {
        // Rule (1): the piece the rotation is considering this turn.
        MoveCommand cyclicChoice = findCommandFor(legalCommands, consideredPieceForCurrentTurn)
                .orElse(legalCommands.get(0));
        boolean cyclicChoiceLandsOnMysteryCell = landsOnMysteryCell(cyclicChoice, context);

        if (isCounterClockwise(cyclicChoice) && cyclicChoiceLandsOnMysteryCell) {
            // Rule (2): counter-clockwise onto the Mystery Cell is preferred, so keep it.
            return cyclicChoice;
        }

        if (isClockwise(cyclicChoice) && cyclicChoiceLandsOnMysteryCell) {
            // Rule (3): clockwise onto the Mystery Cell: switch to a command that avoids it.
            return CommandFinder.findFirst(legalCommands, command -> !landsOnMysteryCell(command, context))
                    .orElse(cyclicChoice);
        }

        // Rule (2): otherwise, look for a counter-clockwise command that lands on it.
        return CommandFinder.findFirst(legalCommands,
                command -> isCounterClockwise(command) && landsOnMysteryCell(command, context))
                .orElse(cyclicChoice);
    }

    // Resumes after the last moved piece, skipping pieces with no legal command.
    private Piece resolveConsideredPiece(Player player, List<MoveCommand> legalCommands) {
        List<Piece> pieces = player.getPieces();
        Iterator<Piece> rotation = new BluePieceRotationIterator(pieces, lastMovedPiece);

        for (int attempt = 0; attempt < pieces.size(); attempt++) {
            Piece candidate = rotation.next();

            if (findCommandFor(legalCommands, candidate).isPresent()) {
                return candidate;
            }
        }

        throw new IllegalMoveException(
                "No legal option belongs to any " + player.getColor() + " piece");
    }

    // Base piece has no direction yet, so it is neither clockwise nor counter-clockwise.
    private static Optional<MovementDirectionStrategy> findCurrentDirection(MoveCommand command) {
        Piece piece = command.getAffectedPiece();

        if (!piece.hasMovementDirection()) {
            return Optional.empty();
        }

        return Optional.of(piece.getMovementDirection());
    }

    private static boolean isClockwise(MoveCommand command) {
        return findCurrentDirection(command).map(MovementDirectionStrategy::isClockwise).orElse(false);
    }

    private static boolean isCounterClockwise(MoveCommand command) {
        return findCurrentDirection(command).map(direction -> !direction.isClockwise()).orElse(false);
    }

    private static boolean landsOnMysteryCell(MoveCommand command, StrategyContext context) {
        MysteryCellLocation mysteryCellLocation = context.getMysteryCellLocation();

        if (!mysteryCellLocation.isActive()) {
            return false;
        }

        return command.previewLandingPosition()
                .map(landingPosition -> landingPosition == mysteryCellLocation.getCurrentCellPosition())
                .orElse(false);
    }

    private static Optional<MoveCommand> findCommandFor(List<MoveCommand> legalCommands, Piece piece) {
        return legalCommands.stream()
                .filter(command -> command.getAffectedPiece() == piece)
                .findFirst();
    }
}
