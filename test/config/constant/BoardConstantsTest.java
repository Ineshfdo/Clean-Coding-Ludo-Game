package config.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Locks the board layout from the assignment: 52 track cells, four evenly spaced colours.
@DisplayName("BoardConstants")
class BoardConstantsTest {

    @Test
    void trackHasFiftyTwoStandardCells() {
        assertEquals(52, BoardConstants.STANDARD_CELL_COUNT);
    }

    @Test
    void everyPlayerHasFourPieces() {
        assertEquals(4, BoardConstants.PIECES_PER_PLAYER);
    }

    @Test
    void homeStraightHasFiveCells() {
        assertEquals(5, BoardConstants.CELLS_PER_HOME_STRAIGHT);
    }

    @Test
    void approachCellsAreSpacedAQuarterOfTheTrackApart() {
        int quarterOfTrack = BoardConstants.STANDARD_CELL_COUNT / 4;

        assertEquals(quarterOfTrack, BoardConstants.BLUE_APPROACH_POSITION - BoardConstants.YELLOW_APPROACH_POSITION);
        assertEquals(quarterOfTrack, BoardConstants.RED_APPROACH_POSITION - BoardConstants.BLUE_APPROACH_POSITION);
        assertEquals(quarterOfTrack, BoardConstants.GREEN_APPROACH_POSITION - BoardConstants.RED_APPROACH_POSITION);
    }

    @Test
    void everyEntryCellIsTwoCellsAfterItsApproachCell() {
        assertEquals(BoardConstants.YELLOW_APPROACH_POSITION + 2, BoardConstants.YELLOW_ENTRY_POSITION);
        assertEquals(BoardConstants.BLUE_APPROACH_POSITION + 2, BoardConstants.BLUE_ENTRY_POSITION);
        assertEquals(BoardConstants.RED_APPROACH_POSITION + 2, BoardConstants.RED_ENTRY_POSITION);
        assertEquals(BoardConstants.GREEN_APPROACH_POSITION + 2, BoardConstants.GREEN_ENTRY_POSITION);
    }
}
