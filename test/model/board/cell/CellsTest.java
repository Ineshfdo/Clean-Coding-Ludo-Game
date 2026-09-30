package model.board.cell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Board cells")
class CellsTest {

    @Test
    void standardCellReportsItsPosition() {
        assertEquals(12, new StandardCell(12, null, null).getPosition());
    }

    @Test
    void standardCellIsApproachPointOnlyForItsApproachOwner() {
        StandardCell cell = new StandardCell(26, PlayerColor.RED, null);

        assertTrue(cell.isApproachPointFor(PlayerColor.RED));
        assertFalse(cell.isApproachPointFor(PlayerColor.GREEN));
    }

    @Test
    void standardCellIsEntryPointOnlyForItsEntryOwner() {
        StandardCell cell = new StandardCell(28, null, PlayerColor.RED);

        assertTrue(cell.isEntryPointFor(PlayerColor.RED));
        assertFalse(cell.isEntryPointFor(PlayerColor.GREEN));
    }

    @Test
    void standardCellWithNoOwnersIsNeitherApproachNorEntry() {
        StandardCell cell = new StandardCell(5, null, null);

        assertFalse(cell.isApproachPointFor(PlayerColor.RED));
        assertFalse(cell.isEntryPointFor(PlayerColor.RED));
    }

    @Test
    void homeStraightCellReportsColorAndIndex() {
        HomeStraightCell cell = new HomeStraightCell(PlayerColor.GREEN, 2);

        assertEquals(PlayerColor.GREEN, cell.getColor());
        assertEquals(2, cell.getIndexFromApproach());
    }

    @ParameterizedTest(name = "index {0} is the last cell before Home: {1}")
    @CsvSource({"0,false", "3,false", "4,true"})
    void onlyTheFifthHomeStraightCellIsLastBeforeHome(int index, boolean expected) {
        assertEquals(expected, new HomeStraightCell(PlayerColor.RED, index).isLastCellBeforeHome());
    }

    @Test
    void homeStraightCellToStringNamesColorAndIndex() {
        assertEquals("RedHomePath2", new HomeStraightCell(PlayerColor.RED, 2).toString());
    }
}
