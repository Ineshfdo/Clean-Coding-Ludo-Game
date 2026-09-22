package model.player.rule.roll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import java.util.List;
import model.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.Fixtures;

@DisplayName("Roll rules")
class RollRulesTest {

    @Nested
    @DisplayName("ConsecutiveSixVoidRule")
    class ConsecutiveSixVoid {

        private final RollValidityRule rule = new ConsecutiveSixVoidRule();

        @Test
        void aThirdSixInARowIsVoided() {
            assertTrue(rule.isVoided(3, 6));
        }

        @ParameterizedTest(name = "{0} sixes in a row, rolled {1}: not voided")
        @CsvSource({"1,6", "2,6", "3,5", "3,1", "0,3", "4,6"})
        void anyOtherRollIsAccepted(int consecutiveSixCount, int rollValue) {
            assertFalse(rule.isVoided(consecutiveSixCount, rollValue));
        }

        @Test
        void aLaterRuleInTheChainCanStillVoidTheRoll() {
            RollValidityRule alwaysVoids = new RollValidityRule() {
                @Override
                protected boolean appliesTo(int consecutiveSixCount, int rollValue) {
                    return true;
                }
            };
            rule.setNext(alwaysVoids);

            assertTrue(rule.isVoided(1, 2));
        }

        @Test
        void aChainWhereNoRuleAppliesAcceptsTheRoll() {
            RollValidityRule neverVoids = new RollValidityRule() {
                @Override
                protected boolean appliesTo(int consecutiveSixCount, int rollValue) {
                    return false;
                }
            };
            rule.setNext(neverVoids);

            assertFalse(rule.isVoided(1, 2));
        }
    }

    @Nested
    @DisplayName("RollEvent")
    class RollEventTests {

        @Test
        void exposesWhatItWasBuiltWith() {
            Player red = Fixtures.playerOf(PlayerColor.RED);
            List<Player> everyone = List.of(red);

            RollEvent event = new RollEvent(red, everyone, 2, 5);

            assertSame(red, event.getPlayer());
            assertSame(everyone, event.getAllPlayers());
            assertEquals(2, event.getRollNumber());
            assertEquals(5, event.getRollValue());
        }
    }
}
