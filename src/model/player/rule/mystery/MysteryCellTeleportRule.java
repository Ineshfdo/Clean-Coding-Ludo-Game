package model.player.rule.mystery;

import java.util.List;
import java.util.Optional;
import model.effect.mysterycell.MysteryCellDestination;
import model.effect.mysterycell.MysteryCellLocation;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.mystery.MysteryCellTeleportCommand;
import utils.randomgenerator.RandomNumberGenerator;

// T-11: a piece landing on the active Mystery Cell teleports to a random destination.
public final class MysteryCellTeleportRule implements TeleportRule {

    private static final int FIRST_DESTINATION_INDEX = 0;

    private final MysteryCellLocation mysteryCellLocation;
    private final RandomNumberGenerator randomNumberGenerator;
    private final List<MysteryCellDestination> destinations;

    public MysteryCellTeleportRule(
            MysteryCellLocation mysteryCellLocation, RandomNumberGenerator randomNumberGenerator,
            List<MysteryCellDestination> destinations) {
        this.mysteryCellLocation = mysteryCellLocation;
        this.randomNumberGenerator = randomNumberGenerator;
        this.destinations = List.copyOf(destinations);
    }

    @Override
    public Optional<Command> findTeleport(Player mover, Piece landedPiece) {
        if (!landsOnMysteryCell(landedPiece)) {
            return Optional.empty();
        }

        List<Piece> teleportedGroup = mover.getPiecesAt(landedPiece.getTrackPosition());

        return Optional.of(new MysteryCellTeleportCommand(mover, teleportedGroup, chooseRandomDestination()));
    }

    private boolean landsOnMysteryCell(Piece piece) {
        return mysteryCellLocation.isActive()
                && piece.isOnTrack()
                && piece.getTrackPosition() == mysteryCellLocation.getCurrentCellPosition();
    }

    private MysteryCellDestination chooseRandomDestination() {
        int randomIndex =
                randomNumberGenerator.nextIntInRange(FIRST_DESTINATION_INDEX, destinations.size() - 1);

        return destinations.get(randomIndex);
    }
}
