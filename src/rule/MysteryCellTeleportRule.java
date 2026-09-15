package rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import command.Command;
import command.TeleportCommand;
import ludoboard.Board;
import mysterycell.AlphaEffectRule;
import mysterycell.MysteryCellDestinationType;
import mysterycell.MysteryCellManager;
import numbergenerator.RandomNumberGenerator;
import player.Piece;
import player.Player;

// T-11: a piece landing exactly on the active Mystery Cell teleports to a randomly chosen destination.
public final class MysteryCellTeleportRule {

    private static final MysteryCellDestinationType[] DESTINATIONS = MysteryCellDestinationType.values();
    private static final int FIRST_DESTINATION_INDEX = 0;
    private static final int LAST_DESTINATION_INDEX = DESTINATIONS.length - 1;

    private final MysteryCellManager mysteryCellManager;
    private final RandomNumberGenerator randomNumberGenerator;
    private final Board board;
    private final AlphaEffectRule alphaEffectRule;

    public MysteryCellTeleportRule(
            MysteryCellManager mysteryCellManager, RandomNumberGenerator randomNumberGenerator,
            Board board, AlphaEffectRule alphaEffectRule) {
        this.mysteryCellManager = mysteryCellManager;
        this.randomNumberGenerator = randomNumberGenerator;
        this.board = board;
        this.alphaEffectRule = alphaEffectRule;
    }

    public Optional<Command> resolve(Player mover, Piece landedPiece) {
        if (!landsOnMysteryCell(landedPiece)) {
            return Optional.empty();
        }

        List<Piece> teleportedGroup = findOwnPiecesAt(mover, landedPiece.getTrackPosition());
        MysteryCellDestinationType destinationType = chooseRandomDestination();
        return Optional.of(
                new TeleportCommand(mover, teleportedGroup, destinationType, board, alphaEffectRule));
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
