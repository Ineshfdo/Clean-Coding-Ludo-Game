package view.presenter.toss;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.PlayerColor;
import message.toss.DiceRolled;
import message.toss.TossStarting;
import message.toss.TossTied;
import message.toss.TossWon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Toss presenters")
class TossPresentersTest {

    @Test
    void tossStartingAnnouncesTheDiceRollForFirstPlayer() {
        assertEquals("Rolling The Dice To Determine Who Goes First\n--------------------------------------------\n",
                new TossStartingPresenter().present(new TossStarting()));
    }

    @Test
    void diceRolledNamesThePlayerAndTheValue() {
        assertEquals("RED Player rolls a 4", new DiceRolledPresenter().present(new DiceRolled(PlayerColor.RED, 4)));
    }

    @Test
    void tossTiedNamesTheTiedValueAndAsksForARerollOfEveryone() {
        assertEquals("There Was A Tie For The Highest Roll (6)! EVERYONE REROLLS...\n",
                new TossTiedPresenter().present(new TossTied(6)));
    }

    @Test
    void tossWonNamesTheWinnerAndTheWinningRoll() {
        assertEquals("GREEN Player Won The Toss With A 5 And Goes First!",
                new TossWonPresenter().present(new TossWon(PlayerColor.GREEN, 5)));
    }

    @Test
    void eachPresenterHandlesItsOwnEventType() {
        assertEquals(TossStarting.class, new TossStartingPresenter().getEventType());
        assertEquals(DiceRolled.class, new DiceRolledPresenter().getEventType());
        assertEquals(TossTied.class, new TossTiedPresenter().getEventType());
        assertEquals(TossWon.class, new TossWonPresenter().getEventType());
    }
}
