package model.player.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.player.command.Command;
import model.player.command.mystery.MysteryCellTeleportCommand;
import model.board.Board;
import config.enums.MysteryCellDestinationType;
import model.effect.rule.MysteryCellEffects;
import model.effect.mysterycell.MysteryCellLocation;
import model.effect.mysterycell.MysteryCellManager;
import utils.random.RandomNumberGenerator;
import model.piece.Piece;
import model.player.Player;

// T-11: a piece landing exactly on the active Mystery Cell teleports to a randomly chosen destination.
public final class MysteryCellTeleportRule {

    private static final MysteryCellDestinationType[] DESTINATIONS = MysteryCellDestinationType.values();
    private static final int FIRST_DESTINATION_INDEX = 0;
    private static final int LAST_DESTINATION_INDEX = DESTINATIONS.length - 1;

    private final MysteryCellManager mysteryCellManager;
    private final RandomNumberGenerator randomNumberGenerator;
    private final Board board;
    private final MysteryCellEffects effects;

    public MysteryCellTeleportRule(
            MysteryCellManager mysteryCellManager, RandomNumberGenerator randomNumberGenerator,
            Board board, MysteryCellEffects effects) {
        this.mysteryCellManager = mysteryCellManager;
        this.randomNumberGenerator = randomNumberGenerator;
        this.board = board;
        this.effects = effects;
    }

    // T-19: lets a PlayerStrategy (e.g. BlueStrategy) preview the Mystery Cell's current
    // location without depending on this whole rule or the manager's full responsibilities.
    public MysteryCellLocation getMysteryCellLocation() {
        return mysteryCellManager;
    }

    public Optional<Command> resolve(Player mover, Piece landedPiece) {
        if (!landsOnMysteryCell(landedPiece)) {
            return Optional.empty();
        }

        List<Piece> teleportedGroup = findOwnPiecesAt(mover, landedPiece.getTrackPosition());
        MysteryCellDestinationType destinationType = chooseRandomDestination();
        return Optional.of(new MysteryCellTeleportCommand(mover, teleportedGroup, destinationType, board, effects));
    }

    private boolean landsOnMysteryCell(Piece piece) {
        return mysteryCellManager.isActive()
                && piece.isOnTrack()
                && piece.getTrackPosition() == mysteryCellManager.getCurrentCellPosition();
    }

    // T-3: every own piece sharing the landing cell teleports together, so a block never splits.
    private static List<Piece> findOwnPiecesAt(Player player, int trackPosition) {
        List<Piece> piecesAtPosition = new ArrayList<>();
        for (Piece piece : player.getPieces()) {
            if (piece.isOnTrack() && piece.getTrackPosition() == trackPosition) {
                piecesAtPosition.add(piece);
            }
        }
        return piecesAtPosition;
    }

    private MysteryCellDestinationType chooseRandomDestination() {
        int randomIndex =
                randomNumberGenerator.nextIntInRange(FIRST_DESTINATION_INDEX, LAST_DESTINATION_INDEX);
        return DESTINATIONS[randomIndex];
    }
}
