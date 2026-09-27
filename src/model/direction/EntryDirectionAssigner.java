package model.direction;

/**
 * Decides in which direction a piece travels when it leaves Base.
 */
public interface EntryDirectionAssigner {

    /**
     * Chooses the direction of a piece that is leaving Base.
     *
     * @return the direction, together with the coin toss that decided it
     */
    EntryDirection assign();
}
