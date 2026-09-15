package mysterycell;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import java.util.ArrayList;
import java.util.List;
import ludoboard.Board;
import numbergenerator.RandomNumberGenerator;
import player.Piece;
import player.Player;

// T-10: tracks the mystery cell's spawn timing, lifespan, and relocation.
// Publishes through GameMessagePublisher only - never prints directly (Observer's job).
public final class MysteryCellManager {

    private static final int REQUIRED_ROUNDS_BEFORE_SPAWN = 2;
    private static final int ROUNDS_PER_LOCATION = 4;
    private static final int NO_ROUND_RECORDED = -1;
    private static final int NO_CELL_SELECTED = -1;

    private final Board board;
    private final RandomNumberGenerator randomNumberGenerator;

    private int firstStandardPathEntryRound = NO_ROUND_RECORDED;
    private boolean isActive;
    private int currentCellPosition = NO_CELL_SELECTED;
    private int roundsRemainingAtCurrentCell;

    public MysteryCellManager(Board board, RandomNumberGenerator randomNumberGenerator) {
        this.board = board;
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public int getCurrentCellPosition() {
        return currentCellPosition;
    }

    // T-11: lets other rules check whether a piece can land on an active Mystery Cell.
    public boolean isActive() {
        return isActive;
    }

    // Called once per round, before turns are played, so a spawn/relocation
    // is visible for the whole round it takes effect in.
    public void onRoundStarted(int roundNumber, List<Player> players, GameMessagePublisher messages) {
        if (isActive) {
            relocateIfDue(players, messages);
            return;
        }
        spawnIfDue(roundNumber, players, messages);
    }

    // Called once per round, after turns are played, so a piece that only
    // reaches the standard path mid-round is still credited to this round.
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
        if (roundsSinceEntry < REQUIRED_ROUNDS_BEFORE_SPAWN || !isAnyPieceOnStandardPath(players)) {
            return;
        }

        currentCellPosition = selectRandomEmptyCell(players, NO_CELL_SELECTED);
        roundsRemainingAtCurrentCell = ROUNDS_PER_LOCATION;
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
        roundsRemainingAtCurrentCell = ROUNDS_PER_LOCATION;
        messages.publish(GameMessage.mysteryCellRelocated(currentCellPosition));
    }

    // T-10: excludes the previous cell so it never reappears in the same place consecutively.
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
