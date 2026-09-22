package view.presenter.move;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.PlayerColor;
import message.move.BlockMoved;
import message.move.PieceDirectionAssigned;
import message.move.PieceEnteredBoard;
import message.move.PieceEnteredHomeStraight;
import message.move.PieceLeftBlock;
import message.move.PieceMoved;
import message.move.PieceReachedHome;
import model.board.LudoBoard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Move presenters")
class MovePresentersTest {

    private final PieceMovedPresenter pieceMoved = new PieceMovedPresenter(LudoBoard.getInstance());

    @Test
    void pieceMovedNamesBothCellsTheDiceValueAndTheDirection() {
        String text = pieceMoved.present(new PieceMoved(PlayerColor.RED, "R1", 20, 24, 4, "Clockwise"));

        assertEquals("  -> Red moves piece R1 from location Cell(20) to Cell(24) by 4 units in Clockwise direction.", text);
    }

    @Test
    void pieceMovedCallsAnApproachCellByItsRole() {
        String text = pieceMoved.present(new PieceMoved(PlayerColor.RED, "R1", 20, 26, 6, "Clockwise"));

        assertEquals("  -> Red moves piece R1 from location Cell(20) to Approach(26) by 6 units in Clockwise direction.", text);
    }

    @Test
    void blockMovedNamesTheBlockItsCellsItsTypeAndItsDirection() {
        String text = new BlockMovedPresenter().present(new BlockMoved("R1+R2", 8, 10, "Same-Direction", "Clockwise"));

        assertEquals("  -> Moved R1+R2 from cell 8 to cell 10. [BlockType:Same-Direction BlockDirection:Clockwise]", text);
    }

    @Test
    void pieceEnteredBoardReportsTheEntryAndTheNewPieceCounts() {
        String text = new PieceEnteredBoardPresenter().present(new PieceEnteredBoard(PlayerColor.GREEN, "G3", 41, 1, 3));

        assertEquals("  -> Green player moves piece G3 to the starting point.\n"
                + "  -> Green player now has 1/4 pieces on the board and 3/4 pieces on the base.", text);
    }

    @Test
    void pieceDirectionAssignedReportsTheCoinTossAndTheResultingDirection() {
        String text = new PieceDirectionAssignedPresenter()
                .present(new PieceDirectionAssigned("G3", "Heads", "Clockwise"));

        assertEquals("  -> Coin toss for G3: Heads - it will move Clockwise.", text);
    }

    @Test
    void pieceEnteredHomeStraightNamesTheCell() {
        String text = new PieceEnteredHomeStraightPresenter()
                .present(new PieceEnteredHomeStraight("R1", "RedHomePath2"));

        assertEquals("  -> R1 entered its HomeStraight at RedHomePath2.", text);
    }

    @Test
    void pieceReachedHomeSaysThePieceIsRemovedFromPlay() {
        assertEquals("  -> R1+R2 reached Home and is removed from play!",
                new PieceReachedHomePresenter().present(new PieceReachedHome("R1+R2")));
    }

    @Test
    void pieceLeftBlockSaysItResumesItsOwnDirection() {
        assertEquals("  -> R2 leaves the block and resumes its own direction.",
                new PieceLeftBlockPresenter().present(new PieceLeftBlock("R2")));
    }
}
