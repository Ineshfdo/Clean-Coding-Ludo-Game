package model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import model.board.cell.HomeStraightCell;
import model.board.cell.StandardCell;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("LudoBoard")
class LudoBoardTest {

    private final LudoBoard board = LudoBoard.getInstance();

    @Test
    void getInstanceAlwaysReturnsTheSameBoard() {
        assertSame(board, LudoBoard.getInstance());
    }

    @Test
    void trackHasFiftyTwoCells() {
        assertEquals(52, board.getStandardCellCount());
    }

    @Test
    void homeStraightHasFiveCells() {
        assertEquals(5, board.getHomeStraightLength());
    }

    @ParameterizedTest(name = "{0} approaches at cell {1}")
    @CsvSource({"YELLOW,0", "BLUE,13", "RED,26", "GREEN,39"})
    void approachCellPositionMatchesEachColor(PlayerColor color, int expectedPosition) {
        assertEquals(expectedPosition, board.getApproachCellPosition(color));
    }

    @ParameterizedTest(name = "{0} enters at cell {1}")
    @CsvSource({"YELLOW,2", "BLUE,15", "RED,28", "GREEN,41"})
    void entryCellPositionMatchesEachColor(PlayerColor color, int expectedPosition) {
        assertEquals(expectedPosition, board.getEntryCellPosition(color));
    }

    @Test
    void mysteryCellPositionsAreAlphaBetaGamma() {
        assertEquals(9, board.getAlphaCellPosition());
        assertEquals(27, board.getBetaCellPosition());
        assertEquals(46, board.getGammaCellPosition());
    }

    @ParameterizedTest(name = "cell {0} moved {1} steps forward lands on {2}")
    @CsvSource({"0,5,5", "10,6,16", "50,5,3", "51,1,0", "0,52,0"})
    void positionAfterMovingWrapsAroundTheTrack(int from, int steps, int expected) {
        assertEquals(expected, board.getPositionAfterMoving(from, steps));
    }

    @ParameterizedTest(name = "cell {0} moved {1} steps backward lands on {2}")
    @CsvSource({"10,5,5", "2,5,49", "0,1,51", "0,52,0", "5,0,5"})
    void positionAfterMovingBackwardWrapsAroundTheTrack(int from, int steps, int expected) {
        assertEquals(expected, board.getPositionAfterMovingBackward(from, steps));
    }

    @ParameterizedTest(name = "forward distance {0} -> {1} is {2}")
    @CsvSource({"5,10,5", "10,5,47", "7,7,0", "51,0,1", "0,51,51"})
    void forwardDistanceCountsStepsAheadAroundTheTrack(int from, int to, int expected) {
        assertEquals(expected, board.getForwardDistance(from, to));
    }

    @ParameterizedTest(name = "after {0} comes {1}")
    @CsvSource({"YELLOW,BLUE", "BLUE,RED", "RED,GREEN", "GREEN,YELLOW"})
    void nextColorClockwiseFollowsTheBoardLayout(PlayerColor color, PlayerColor expectedNext) {
        assertEquals(expectedNext, board.getNextColorClockwise(color));
    }

    @Test
    void standardCellRemembersItsPosition() {
        assertEquals(17, board.getStandardCell(17).getPosition());
    }

    @Test
    void approachCellIsMarkedForItsOwner() {
        StandardCell redApproach = board.getStandardCell(board.getApproachCellPosition(PlayerColor.RED));

        assertTrue(redApproach.isApproachPointFor(PlayerColor.RED));
        assertFalse(redApproach.isApproachPointFor(PlayerColor.BLUE));
    }

    @Test
    void entryCellIsMarkedForItsOwner() {
        StandardCell greenEntry = board.getStandardCell(board.getEntryCellPosition(PlayerColor.GREEN));

        assertTrue(greenEntry.isEntryPointFor(PlayerColor.GREEN));
        assertFalse(greenEntry.isEntryPointFor(PlayerColor.RED));
    }

    @Test
    void ordinaryCellIsNeitherApproachNorEntryForAnyone() {
        StandardCell ordinaryCell = board.getStandardCell(5);

        for (PlayerColor color : PlayerColor.values()) {
            assertFalse(ordinaryCell.isApproachPointFor(color));
            assertFalse(ordinaryCell.isEntryPointFor(color));
        }
    }

    @Test
    void homeStraightCellBelongsToTheAskedColorAndIndex() {
        HomeStraightCell cell = board.getHomeStraightCell(PlayerColor.BLUE, 3);

        assertEquals(PlayerColor.BLUE, cell.getColor());
        assertEquals(3, cell.getIndexFromApproach());
    }
}
