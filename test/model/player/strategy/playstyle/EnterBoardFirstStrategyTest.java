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

@DisplayName("EnterBoardFirstStrategy")
class EnterBoardFirstStrategyTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final StrategyContext context = Commands.contextFor(red, List.of(red), 1);
    private final PlayerStrategy strategy = new EnterBoardFirstStrategy();

    private final Piece onTrack = Fixtures.placeOnTrackClockwise(red, 0, 10);
    private final Piece atBase = red.getPieces().get(1);
    private final Piece otherAtBase = red.getPieces().get(2);

    @Test
    void choosesTheCommandThatEntersTheBoard() {
        MoveCommand move = Commands.forPiece(onTrack);
        MoveCommand enter = Commands.entering(atBase);

        assertSame(enter, strategy.choose(List.of(move, enter), context));
    }

    @Test
    void choosesTheFirstEnteringCommandWhenSeveralEnter() {
        MoveCommand firstEnter = Commands.entering(atBase);
        MoveCommand secondEnter = Commands.entering(otherAtBase);

        assertSame(firstEnter, strategy.choose(List.of(firstEnter, secondEnter), context));
    }

    @Test
    void choosesTheFirstCommandWhenNoneEnters() {
        MoveCommand first = Commands.forPiece(onTrack);
        MoveCommand second = Commands.forPiece(atBase);

        assertSame(first, strategy.choose(List.of(first, second), context));
    }
}
