package model.player.strategy.playstyle;

import static org.junit.jupiter.api.Assertions.assertSame;

import config.enums.PlayerColor;
import java.util.List;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Commands;
import support.Fixtures;

// Yellow is winning-focused: leave Base, capture only when a piece still needs one, else advance the leader.
// Yellow's Approach is cell 0, so a piece on cell 50 is far closer to Home than one on cell 10.
@DisplayName("YellowStrategy")
class YellowStrategyTest {

    private final Player yellow = Fixtures.playerOf(PlayerColor.YELLOW);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final StrategyContext context = Commands.contextFor(yellow, List.of(yellow, green), 1);
    private final PlayerStrategy strategy = new YellowStrategy();

    private final Piece farFromHome = Fixtures.placeOnTrackClockwise(yellow, 0, 10);
    private final Piece nearHome = Fixtures.placeOnTrackClockwise(yellow, 1, 50);
    private final Piece atBase = yellow.getPieces().get(2);

    @Test
    void leavingBaseComesFirst() {
        MoveCommand enter = Commands.entering(atBase);

        assertSame(enter, strategy.choose(List.of(Commands.forPiece(nearHome), enter), context));
    }

    @Test
    void aCaptureIsTakenForAPieceThatStillNeedsOne() {
        Fixtures.placeOnTrackClockwise(green, 0, 14);
        MoveCommand advance = Commands.forPiece(nearHome);
        MoveCommand capture = Commands.movingTo(farFromHome, 14);

        assertSame(capture, strategy.choose(List.of(advance, capture), context));
    }

    @Test
    void aCaptureIsIgnoredForAPieceThatHasAlreadyCaptured() {
        Fixtures.placeOnTrackClockwise(green, 0, 14);
        farFromHome.recordCapture();
        MoveCommand advance = Commands.forPiece(nearHome);
        MoveCommand capture = Commands.movingTo(farFromHome, 14);

        assertSame(advance, strategy.choose(List.of(capture, advance), context));
    }

    @Test
    void withoutAnythingToCaptureThePieceClosestToHomeAdvances() {
        MoveCommand far = Commands.forPiece(farFromHome);
        MoveCommand near = Commands.forPiece(nearHome);

        assertSame(near, strategy.choose(List.of(far, near), context));
    }

    @Test
    void aMoveThatLandsOnNobodyIsNotACapture() {
        Fixtures.placeOnTrackClockwise(green, 0, 30);
        MoveCommand far = Commands.movingTo(farFromHome, 14);
        MoveCommand near = Commands.forPiece(nearHome);

        assertSame(near, strategy.choose(List.of(far, near), context));
    }
}
