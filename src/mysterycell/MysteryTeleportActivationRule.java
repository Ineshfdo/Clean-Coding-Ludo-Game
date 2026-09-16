package mysterycell;

// T-15: the required check - a genuine MysteryCellArrival must exist. Ordinary movement
// never creates one, so landing on cell 9, 27, or 46 through normal play can never satisfy
// this rule, and its effect is correctly rejected.
public final class MysteryTeleportActivationRule extends EffectActivationRule {

    @Override
    protected boolean allowsActivation(MysteryCellArrival arrival) {
        return arrival != null;
    }
}
