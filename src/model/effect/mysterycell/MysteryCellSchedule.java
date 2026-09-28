package model.effect.mysterycell;

import config.constant.BoardConstants;
import config.constant.MysteryCellConstants;
import java.util.ArrayList;
import java.util.List;
import message.mystery.MysteryCellAppeared;
import message.mystery.MysteryCellRelocated;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.round.RoundListener;
import utils.randomgenerator.RandomNumberGenerator;

/**
 Controls when the Mystery Cell appears and where it stands (T-10).
 It appears two rounds after the first piece has entered the track, stays four rounds on one empty cell, and then moves to another empty cell.
 It only publishes messages and never prints.
 */
public final class MysteryCellSchedule implements MysteryCellLocation, RoundListener {

    private static final int NO_ROUND_RECORDED = -1;

    private final Board board;
    private final RandomNumberGenerator randomNumberGenerator;

    private int firstStandardPathEntryRound = NO_ROUND_RECORDED;
    private boolean isActive;
    private int currentCellPosition = BoardConstants.NO_TRACK_POSITION;
    private int roundsRemainingAtCurrentCell;

    /**
     Creates the schedule of one game.
     @param board gives the size of the track
     @param randomNumberGenerator draws the random cell
     */
    public MysteryCellSchedule(Board board, RandomNumberGenerator randomNumberGenerator) {
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
    @Override
    public void onRoundStarted(int roundNumber, List<Player> allPlayers, GameMessagePublisher messagePublisher) {
        if (isActive) {
            relocateIfDue(allPlayers, messagePublisher);
            return;
        }

        spawnIfDue(roundNumber, allPlayers, messagePublisher);
    }

    // Runs after turns, so mid-round entries count this round.
    @Override
    public void onRoundCompleted(int roundNumber, List<Player> allPlayers) {
        if (firstStandardPathEntryRound != NO_ROUND_RECORDED) {
            return;
        }

        if (isAnyPieceOnStandardPath(allPlayers)) {
            firstStandardPathEntryRound = roundNumber;
        }
    }

    private void spawnIfDue(int roundNumber, List<Player> allPlayers, GameMessagePublisher messagePublisher) {
        if (firstStandardPathEntryRound == NO_ROUND_RECORDED) {
            return;
        }

        int roundsSinceEntry = roundNumber - firstStandardPathEntryRound;

        if (roundsSinceEntry < MysteryCellConstants.REQUIRED_ROUNDS_BEFORE_SPAWN
                || !isAnyPieceOnStandardPath(allPlayers)) {
            return;
        }

        currentCellPosition = chooseRandomEmptyCell(allPlayers, BoardConstants.NO_TRACK_POSITION);
        roundsRemainingAtCurrentCell = MysteryCellConstants.ROUNDS_PER_LOCATION;
        isActive = true;

        messagePublisher.publish(new MysteryCellAppeared(currentCellPosition));
    }

    private void relocateIfDue(List<Player> allPlayers, GameMessagePublisher messagePublisher) {
        roundsRemainingAtCurrentCell--;

        if (roundsRemainingAtCurrentCell > 0) {
            return;
        }

        int previousCellPosition = currentCellPosition;
        currentCellPosition = chooseRandomEmptyCell(allPlayers, previousCellPosition);
        roundsRemainingAtCurrentCell = MysteryCellConstants.ROUNDS_PER_LOCATION;

        messagePublisher.publish(new MysteryCellRelocated(currentCellPosition));
    }

    // T-10: excludes the previous cell so it never repeats.
    private int chooseRandomEmptyCell(List<Player> allPlayers, int excludedCellPosition) {
        List<Integer> emptyCellPositions = findEmptyCellPositions(allPlayers, excludedCellPosition);
        int randomIndex = randomNumberGenerator.nextIntInRange(0, emptyCellPositions.size() - 1);

        return emptyCellPositions.get(randomIndex);
    }

    private List<Integer> findEmptyCellPositions(List<Player> allPlayers, int excludedCellPosition) {
        List<Integer> emptyCellPositions = new ArrayList<>();

        for (int cellPosition = 0; cellPosition < board.getStandardCellCount(); cellPosition++) {
            if (cellPosition == excludedCellPosition) {
                continue;
            }

            if (!isCellOccupied(cellPosition, allPlayers)) {
                emptyCellPositions.add(cellPosition);
            }
        }

        return emptyCellPositions;
    }

    private static boolean isCellOccupied(int cellPosition, List<Player> allPlayers) {
        for (Player player : allPlayers) {
            if (!player.getPiecesAt(cellPosition).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAnyPieceOnStandardPath(List<Player> allPlayers) {
        for (Player player : allPlayers) {
            for (Piece piece : player.getPieces()) {
                if (piece.isOnTrack()) {
                    return true;
                }
            }
        }

        return false;
    }
}
