package config.enums;

/**
 * Where a piece is on its journey: Base, the shared track, its HomeStraight, or Home (rule 6).
 */
public enum PieceLocation {
    /**
     * The piece is at Base and waits for a six.
     */
    BASE,
    /**
     * The piece is on the shared track.
     */
    TRACK,
    /**
     * The piece is on the HomeStraight of its colour.
     */
    HOME_STRAIGHT,
    /**
     * The piece has reached Home and does not move again.
     */
    HOME
}
