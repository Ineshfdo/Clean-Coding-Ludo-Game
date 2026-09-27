package model.effect.restriction;

/**
 * State pattern: a restriction that can stop a piece from moving (T-13). A state carries data that
 * changes over time, so every change gives back the next state and the old object stays unchanged.
 */
public interface PieceRestrictionState {

    /**
     * Tells whether the piece may move.
     *
     * @return true while the piece cannot move
     */
    boolean forbidsMovement();

    /**
     * Gives the time left.
     *
     * @return the number of rounds left; 0 for no restriction
     */
    int getRoundsRemaining();

    /**
     * Called once per round. A restriction that is used up gives back the free state.
     *
     * @return the state after one round
     */
    PieceRestrictionState afterRoundElapses();

    /**
     * Records the first roll of a turn toward the condition of two consecutive rolls (T-13).
     *
     * @param rollValue the value of the roll
     * @return the state after the roll
     */
    PieceRestrictionState afterRollRecorded(int rollValue);

    /**
     * Tells whether the piece must go back to Base.
     *
     * @return true when the condition of two consecutive rolls of 3 is met
     */
    boolean hasTriggeredReturnToBase();
}
