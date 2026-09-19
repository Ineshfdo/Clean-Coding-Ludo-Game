package model.effect.mysterycell;

import config.constant.BoardConstants;
import config.constant.MysteryCellConstants;
import java.util.ArrayList;
import java.util.List;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import service.result.GameMessage;
import utils.random.RandomNumberGenerator;
import view.observer.GameMessagePublisher;

// T-10: tracks Mystery Cell spawn, lifespan and relocation.
// Publishes messages only; never prints directly.
public final class MysteryCellManager implements MysteryCellLocation {

    private static final int NO_ROUND_RECORDED = -1;

    private final Board board;
    private final RandomNumberGenerator randomNumberGenerator;

    private int firstStandardPathEntryRound = NO_ROUND_RECORDED;
    private boolean isActive;
    private int currentCellPosition = BoardConstants.NO_TRACK_POSITION;
    private int roundsRemainingAtCurrentCell;

    public MysteryCellManager(Board board, RandomNumberGenerator randomNumberGenerator) {
        this.board = board;
        this.randomNumberGenerator = randomNumberGenerator;
    }

    @Override
    public int getCurrentCellPosition() {
        return currentCellPosition;
    }

    // T-11/T-19: lets rules and strategies check for an active cell.
    @Override
    public boolean isActive() {
        return isActive;
    }

    // Runs before turns, so changes last the whole round.
    public void onRoundStarted(int roundNumber, List<Player> players, GameMessagePublisher messages) {
        if (isActive) {
            relocateIfDue(players, messages);
            return;
        }

        spawnIfDue(roundNumber, players, messages);
    }

    // Runs after turns, so mid-round entries count this round.
    public void onRoundCompleted(int roundNumber, List<Player> players) {
        if (firstStandardPathEntryRound != NO_ROUND_RECORDED) {
            return;
        }

        if (isAnyPieceOnStandardPath(players)) {
            firstStandardPathEntryRound = roundNumber;
        }
    }

    private void spawnIfDue(int roundNumber, List<Player> players, GameMessagePublisher messages) {
        if (firstStandardPathEntryRound == NO_ROUND_RECORDED) {
            return;
        }

        int roundsSinceEntry = roundNumber - firstStandardPathEntryRound;

        if (roundsSinceEntry < MysteryCellConstants.REQUIRED_ROUNDS_BEFORE_SPAWN
                || !isAnyPieceOnStandardPath(players)) {
            return;
        }

        currentCellPosition = selectRandomEmptyCell(players, BoardConstants.NO_TRACK_POSITION);
        roundsRemainingAtCurrentCell = MysteryCellConstants.ROUNDS_PER_LOCATION;
        isActive = true;

        messages.publish(GameMessage.mysteryCellAppeared(currentCellPosition));
    }

    private void relocateIfDue(List<Player> players, GameMessagePublisher messages) {
        roundsRemainingAtCurrentCell--;

        if (roundsRemainingAtCurrentCell > 0) {
            return;
        }

        int previousCellPosition = currentCellPosition;
        currentCellPosition = selectRandomEmptyCell(players, previousCellPosition);
        roundsRemainingAtCurrentCell = MysteryCellConstants.ROUNDS_PER_LOCATION;

        messages.publish(GameMessage.mysteryCellRelocated(currentCellPosition));
    }

    // T-10: excludes the previous cell so it never repeats.
    private int selectRandomEmptyCell(List<Player> players, int excludedCellPosition) {
        List<Integer> emptyCellPositions = findEmptyCellPositions(players, excludedCellPosition);
        int randomIndex = randomNumberGenerator.nextIntInRange(0, emptyCellPositions.size() - 1);

        return emptyCellPositions.get(randomIndex);
    }

    private List<Integer> findEmptyCellPositions(List<Player> players, int excludedCellPosition) {
        List<Integer> emptyCellPositions = new ArrayList<>();

        for (int cellPosition = 0; cellPosition < board.getStandardCellCount(); cellPosition++) {
            if (cellPosition == excludedCellPosition) {
                continue;
            }

            if (!isCellOccupied(cellPosition, players)) {
                emptyCellPositions.add(cellPosition);
            }
        }

        return emptyCellPositions;
    }

    private static boolean isCellOccupied(int cellPosition, List<Player> players) {
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (piece.isOnTrack() && piece.getTrackPosition() == cellPosition) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isAnyPieceOnStandardPath(List<Player> players) {
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (piece.isOnTrack()) {
                    return true;
                }
            }
        }

        return false;
    }
}
