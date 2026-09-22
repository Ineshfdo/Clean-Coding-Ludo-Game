package view.presenter.mystery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import message.mystery.BetaRestrictionApplied;
import message.mystery.BetaRestrictionTriggered;
import message.mystery.BlockEffectAssigned;
import message.mystery.IndividualEffectAssigned;
import message.mystery.MysteryCellAppeared;
import message.mystery.MysteryCellRelocated;
import message.mystery.PieceDirectionReversed;
import message.mystery.PieceTeleported;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Mystery presenters")
class MysteryPresentersTest {

    private static final String BORDER = "=".repeat(40);

    @Test
    void mysteryCellAppearedIsShownInABannerWithItsCellAndLifespan() {
        String text = new MysteryCellAppearedPresenter().present(new MysteryCellAppeared(20));

        assertEquals("\n" + BORDER + "\n"
                + "A Mystery Cell has appeared at cell 20 will be here and will be at that location for the next 4 rounds.\n"
                + BORDER, text);
    }

    @Test
    void mysteryCellRelocatedIsShownInABannerWithItsNewCell() {
        String text = new MysteryCellRelocatedPresenter().present(new MysteryCellRelocated(31));

        assertEquals("\n" + BORDER + "\n"
                + "The Mystery Cell has relocated to cell 31 will be here and will be at that location for the next 4 rounds.\n"
                + BORDER, text);
    }

    @Test
    void pieceTeleportedNamesTheDestinationAndTheCell() {
        String text = new PieceTeleportedPresenter().present(new PieceTeleported("R1", "Alpha", 9));

        assertEquals("  -> R1 landed on the Mystery Cell! Teleported to Alpha (cell 9).", text);
    }

    @Test
    void pieceTeleportedToBaseHasNoCell() {
        String text = new PieceTeleportedPresenter().present(new PieceTeleported("R1+R2", "Base", -1));

        assertEquals("  -> R1+R2 landed on the Mystery Cell! Teleported to Base.", text);
    }

    @Test
    void individualEffectAssignedNamesThePieceAndTheEffect() {
        String text = new IndividualEffectAssignedPresenter().present(new IndividualEffectAssigned("R1", "Energized"));

        assertEquals("  -> R1 is now Energized (individual effect, lasts 4 rounds).", text);
    }

    @Test
    void blockEffectAssignedNamesTheBlockAndTheEffect() {
        String text = new BlockEffectAssignedPresenter().present(new BlockEffectAssigned("R1+R2", "Sick"));

        assertEquals("  -> Block R1+R2 is now Sick (block effect, lasts 4 rounds, overrides individual effects).", text);
    }

    @Test
    void betaRestrictionAppliedSaysHowLongThePieceIsStuck() {
        String text = new BetaRestrictionAppliedPresenter().present(new BetaRestrictionApplied("R3"));

        assertEquals("  -> R3 cannot move for the next 4 rounds (Beta restriction).", text);
    }

    @Test
    void betaRestrictionTriggeredSaysThePieceIsSentBackToBase() {
        String text = new BetaRestrictionTriggeredPresenter().present(new BetaRestrictionTriggered("R3"));

        assertEquals("  -> R3 rolled a 3 two rounds in a row while Beta-restricted and is sent back to Base!", text);
    }

    @Test
    void pieceDirectionReversedNamesTheNewDirection() {
        String text = new PieceDirectionReversedPresenter().present(new PieceDirectionReversed("R1", "Counter-Clockwise"));

        assertEquals("  -> R1 landed on Gamma and reversed direction - now moving Counter-Clockwise.", text);
    }
}
