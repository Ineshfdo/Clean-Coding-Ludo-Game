package model.player.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import config.enums.PlayerColor;
import java.util.List;
import java.util.Map;
import model.board.Board;
import model.board.LudoBoard;
import model.effect.mysterycell.MysteryCellLocation;
import model.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Fixtures;

@DisplayName("Strategy infrastructure")
class StrategyInfrastructureTest {

    private final PlayerStrategy redStrategy = mock(PlayerStrategy.class);
    private final PlayerStrategy defaultStrategy = mock(PlayerStrategy.class);
    private final PlayerStrategyLookup lookup =
            new PlayerStrategyRegistry(Map.of(PlayerColor.RED, redStrategy), defaultStrategy);

    @Test
    void theRegistryReturnsTheStrategyRegisteredForAColor() {
        assertSame(redStrategy, lookup.getStrategyFor(PlayerColor.RED));
    }

    @Test
    void theRegistryFallsBackToTheDefaultForAnUnregisteredColor() {
        assertSame(defaultStrategy, lookup.getStrategyFor(PlayerColor.BLUE));
    }

    @Test
    void theContextExposesWhatItWasBuiltWith() {
        Player red = Fixtures.playerOf(PlayerColor.RED);
        List<Player> everyone = List.of(red);
        Board board = LudoBoard.getInstance();
        MysteryCellLocation location = mock(MysteryCellLocation.class);

        StrategyContext context = new StrategyContext(red, everyone, board, location, 2);

        assertSame(red, context.getPlayer());
        assertSame(everyone, context.getAllPlayers());
        assertSame(board, context.getBoard());
        assertSame(location, context.getMysteryCellLocation());
        assertEquals(2, context.getRollNumber());
    }
}
