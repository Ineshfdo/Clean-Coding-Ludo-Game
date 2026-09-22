package view.presenter.turn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.PlayerColor;
import message.turn.HomeGateOpened;
import message.turn.NoPieceMovable;
import message.turn.ThirdSixVoided;
import message.turn.TurnRolled;
import message.turn.TurnStarted;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Turn presenters")
class TurnPresentersTest {

    @Test
    void turnStartedNamesWhoseTurnItIs() {
        assertEquals("\n- GREEN Player's Turn -", new TurnStartedPresenter().present(new TurnStarted(PlayerColor.GREEN)));
    }

    @Test
    void turnRolledNamesThePlayerAndTheRoll() {
        assertEquals("Red player rolled 3", new TurnRolledPresenter().present(new TurnRolled(PlayerColor.RED, 3)));
    }

    @Test
    void noPieceMovableSaysNothingCouldMove() {
        assertEquals("  -> No pieces on the board could be moved.",
                new NoPieceMovablePresenter().present(new NoPieceMovable()));
    }

    @Test
    void thirdSixVoidedExplainsTheTurnPasses() {
        assertEquals("  -> Three sixes in a row! This roll is void - turn passes to the next player.",
                new ThirdSixVoidedPresenter().present(new ThirdSixVoided()));
    }

    @Test
    void homeGateOpenedNamesThePlayerWhoseGateOpened() {
        assertEquals("The home gate opens for the Green player: no opponent pieces remain to capture.",
                new HomeGateOpenedPresenter().present(new HomeGateOpened(PlayerColor.GREEN)));
    }
}
