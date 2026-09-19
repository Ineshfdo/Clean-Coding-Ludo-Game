package model.piece;

// T-13: State pattern - unlike T-12's stateless MovementEffectType enum, this state must carry
// its own data (rounds remaining, consecutive-roll count) that changes over time, so it is
// modeled as real polymorphic classes instead of a fixed enum table.
public interface PieceRestrictionState {

    boolean forbidsMovement();

    int getRoundsRemaining();

    // T-13: called once per round; a restriction eventually expires back to NoRestrictionState.
    PieceRestrictionState afterRoundElapses();

    // T-13: records this round's roll toward the Beta restriction's consecutive-3 condition.
    PieceRestrictionState afterRollRecorded(int rollValue);

    boolean hasTriggeredReturnToBase();
}
