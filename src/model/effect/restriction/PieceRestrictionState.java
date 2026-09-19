package model.effect.restriction;

// T-13: State pattern - a restriction carries data that changes over time.
public interface PieceRestrictionState {

    boolean forbidsMovement();

    int getRoundsRemaining();

    // T-13: called once per round; expires back to NoRestrictionState.
    PieceRestrictionState afterRoundElapses();

    // T-13: records this round's roll toward the consecutive-3 condition.
    PieceRestrictionState afterRollRecorded(int rollValue);

    boolean hasTriggeredReturnToBase();
}
