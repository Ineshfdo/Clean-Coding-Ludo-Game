package view.presenter.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.MovementEffectType;
import config.enums.PlayerColor;
import java.util.List;
import message.game.BoardStateReported;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.effect.movement.MovementEffect;
import model.effect.restriction.BetaRestrictedState;
import model.piece.Piece;
import model.player.Player;
import model.player.strategy.blockdirection.LongestDistanceDirectionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Fixtures;

// This report reads the live players, so each test builds the situation it wants to see described.
@DisplayName("BoardStateReportedPresenter")
class BoardStateReportedPresenterTest {

    private static final String SHORT_BORDER = "-".repeat(31);
    private static final String BANNER = "=".repeat(37);

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final BoardStateReportedPresenter presenter = new BoardStateReportedPresenter(
            List.of(red), LudoBoard.getInstance(), new LongestDistanceDirectionStrategy());

    private String report() {
        return presenter.present(new BoardStateReported(3));
    }

    @Test
    void aPlayerWithEverythingAtBaseGetsTheFullReport() {
        String expectedRow = "RED: R1(BASE, IndividualCaptureCount:0)  R2(BASE, IndividualCaptureCount:0)"
                + "  R3(BASE, IndividualCaptureCount:0)  R4(BASE, IndividualCaptureCount:0)";

        assertEquals("\n" + SHORT_BORDER + "\n"
                + "Red player now has 0/4 pieces on the board and 4/4 pieces on the base.\n"
                + SHORT_BORDER + "\n"
                + "\nRound 3 Current Board State\n"
                + BANNER + "\n"
                + expectedRow + "\n"
                + BANNER, report());
    }

    @Test
    void theSummaryCountsPiecesOnTheBoardAndAtBase() {
        Fixtures.placeOnTrackClockwise(red, 0, 10);
        Fixtures.placeOnTrackClockwise(red, 1, 20);

        assertTrue(report().contains("Red player now has 2/4 pieces on the board and 2/4 pieces on the base."));
    }

    @Test
    void aTrackPieceShowsItsCellDirectionsAndPasses() {
        Fixtures.placeOnTrackClockwise(red, 0, 12);

        assertTrue(report().contains("R1(Cell(12), CurrentDirection:Clockwise, OriginalDirection:Clockwise, "
                + "ApproachCellPasses:0, IndividualCaptureCount:0)"));
    }

    @Test
    void anApproachCellIsNamedAsSuch() {
        Fixtures.placeOnTrackClockwise(red, 0, 26);

        assertTrue(report().contains("R1(Approach(26), "));
    }

    @Test
    void aHomeStraightPieceShowsItsIndex() {
        Fixtures.placeOnHomeStraight(red, 0, 2, ClockwiseMovementStrategy.getInstance());

        assertTrue(report().contains("R1(HomeStraight(2), CurrentDirection:Clockwise"));
    }

    @Test
    void aFinishedPieceShowsHomeAndNoDirection() {
        Fixtures.placeHome(red, 0);

        assertTrue(report().contains("R1(HOME, IndividualCaptureCount:0)"));
    }

    @Test
    void aPieceShowsItsCaptureCount() {
        Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 12);
        piece.recordCapture();
        piece.recordCapture();

        assertTrue(report().contains("IndividualCaptureCount:2)"));
    }

    @Test
    void anActiveIndividualEffectIsShown() {
        Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 12);
        piece.applyIndividualEffect(MovementEffect.of(MovementEffectType.SICK, 4));

        assertTrue(report().contains("IndividualCaptureCount:0, IndividualEffect:Sick)"));
    }

    @Test
    void aBetaRestrictionShowsTheRoundsLeft() {
        Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 27);
        piece.applyRestriction(new BetaRestrictedState());

        assertTrue(report().contains("IndividualCaptureCount:0, BetaRestrictionRoundsLeft:4)"));
    }

    @Test
    void piecesOnTheSameCellAreShownAsOneBlock() {
        Fixtures.placeOnTrackClockwise(red, 0, 10);
        Fixtures.placeOnTrackClockwise(red, 1, 10);

        String text = report();

        assertTrue(text.contains("[Block: R1(Cell(10), "));
        assertTrue(text.contains(" R2(Cell(10), "));
        assertTrue(text.contains(" BlockType:Same-Direction BlockDirection:Clockwise BlockApproachCellPasses:0]"));
    }

    @Test
    void aBlockOfMixedDirectionsIsLabelledOppositeDirection() {
        Fixtures.placeOnTrackClockwise(red, 0, 20);
        Fixtures.placeOnTrack(red, 1, 20, CounterClockwiseMovementStrategy.getInstance());

        assertTrue(report().contains("BlockType:Opposite-Direction BlockDirection:Counter-Clockwise"));
    }

    @Test
    void aBlockEffectIsShownOnceForTheWholeBlock() {
        Piece first = Fixtures.placeOnTrackClockwise(red, 0, 10);
        Piece second = Fixtures.placeOnTrackClockwise(red, 1, 10);
        red.applyBlockEffect(first, MovementEffect.of(MovementEffectType.ENERGIZED, 4), 2);
        red.applyBlockEffect(second, MovementEffect.of(MovementEffectType.ENERGIZED, 4), 2);

        assertTrue(report().contains("BlockApproachCellPasses:0 BlockEffect:Energized]"));
    }

    @Test
    void aHomeStraightBlockHasNoDirectionInformation() {
        Fixtures.placeOnHomeStraight(red, 0, 2, ClockwiseMovementStrategy.getInstance());
        Fixtures.placeOnHomeStraight(red, 1, 2, ClockwiseMovementStrategy.getInstance());

        String text = report();

        assertTrue(text.contains("[Block: R1(HomeStraight(2), "));
        assertTrue(text.contains("IndividualCaptureCount:0)]"));
    }

    @Test
    void playersAreListedInThePlayOrderTheyWereGiven() {
        BoardStateReportedPresenter twoPlayers = new BoardStateReportedPresenter(
                List.of(red, green), LudoBoard.getInstance(), new LongestDistanceDirectionStrategy());

        String text = twoPlayers.present(new BoardStateReported(1));

        assertTrue(text.indexOf("RED:") < text.indexOf("GREEN:"));
    }

    @Test
    void setTurnOrderChangesTheOrderOfTheRows() {
        BoardStateReportedPresenter twoPlayers = new BoardStateReportedPresenter(
                List.of(red, green), LudoBoard.getInstance(), new LongestDistanceDirectionStrategy());

        twoPlayers.setTurnOrder(List.of(green, red));
        String text = twoPlayers.present(new BoardStateReported(1));

        assertTrue(text.indexOf("GREEN:") < text.indexOf("RED:"));
    }

    @Test
    void theRoundNumberComesFromTheEvent() {
        assertTrue(presenter.present(new BoardStateReported(12)).contains("Round 12 Current Board State"));
    }
}
