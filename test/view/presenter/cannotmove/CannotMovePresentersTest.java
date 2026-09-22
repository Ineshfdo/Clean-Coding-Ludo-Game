package view.presenter.cannotmove;

import static org.junit.jupiter.api.Assertions.assertEquals;

import message.cannotmove.BlockRollTooSmall;
import message.cannotmove.EffectRollTooSmall;
import message.cannotmove.PieceBlocked;
import message.cannotmove.PieceNeedsExactRoll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Cannot-move presenters")
class CannotMovePresentersTest {

    @Test
    void pieceBlockedBlamesTheOpponentsBlockade() {
        assertEquals("  -> R1+R2 is blocked by an opponent's blockade and cannot move.",
                new PieceBlockedPresenter().present(new PieceBlocked("R1+R2")));
    }

    @Test
    void pieceNeedsExactRollAsksForAnExactRoll() {
        assertEquals("  -> R4 needs an exact roll to reach Home and cannot move.",
                new PieceNeedsExactRollPresenter().present(new PieceNeedsExactRoll("R4")));
    }

    @Test
    void blockRollTooSmallSaysTheBlockRollDividedToZero() {
        assertEquals("  -> R1+R2's block roll divided to zero cells and cannot move.",
                new BlockRollTooSmallPresenter().present(new BlockRollTooSmall("R1+R2")));
    }

    @Test
    void effectRollTooSmallBlamesTheSickEffect() {
        assertEquals("  -> R3's Sick effect halved this roll to zero cells and cannot move.",
                new EffectRollTooSmallPresenter().present(new EffectRollTooSmall("R3")));
    }
}
