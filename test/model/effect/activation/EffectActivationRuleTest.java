package model.effect.activation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import model.effect.mysterycell.MysteryCellDestination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Effect activation rules")
class EffectActivationRuleTest {

    private final MysteryCellArrival arrival = new MysteryCellArrival(mock(MysteryCellDestination.class));

    // A rule with a fixed answer, so chaining can be tested on its own.
    private static EffectActivationRule ruleAnswering(boolean answer) {
        return new EffectActivationRule() {
            @Override
            protected boolean isSatisfiedBy(MysteryCellArrival arrival) {
                return answer;
            }
        };
    }

    @Test
    void arrivalKeepsItsDestination() {
        MysteryCellDestination destination = mock(MysteryCellDestination.class);

        assertSame(destination, new MysteryCellArrival(destination).getDestination());
    }

    @Test
    void teleportRuleAllowsAGenuineArrival() {
        assertTrue(new MysteryTeleportActivationRule().permitsActivation(arrival));
    }

    @Test
    void teleportRuleRefusesWhenThereWasNoArrival() {
        assertFalse(new MysteryTeleportActivationRule().permitsActivation(null));
    }

    @Test
    void aSatisfiedRuleWithNoNextRulePermitsActivation() {
        assertTrue(ruleAnswering(true).permitsActivation(arrival));
    }

    @Test
    void anUnsatisfiedRuleRefusesActivation() {
        assertFalse(ruleAnswering(false).permitsActivation(arrival));
    }

    @Test
    void theNextRuleCanStillRefuseAfterTheFirstIsSatisfied() {
        EffectActivationRule first = ruleAnswering(true);
        first.setNext(ruleAnswering(false));

        assertFalse(first.permitsActivation(arrival));
    }

    @Test
    void everyRuleInTheChainMustAgree() {
        EffectActivationRule first = ruleAnswering(true);
        first.setNext(ruleAnswering(true));

        assertTrue(first.permitsActivation(arrival));
    }
}
