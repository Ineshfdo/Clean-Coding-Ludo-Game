package player;

import ludoboard.PlayerColor;

public final class Piece {

    private final PlayerColor color;
    private final int pieceNumber;

    private boolean atBase;
    private int trackPosition;

    Piece(PlayerColor color, int pieceNumber) {
        this.color = color;
        this.pieceNumber = pieceNumber;
        this.atBase = true;
    }

    public PlayerColor getColor() {
        return color;
    }

    public boolean isAtBase() {
        return atBase;
    }

    public int getTrackPosition() {
        if (atBase) {
            throw new IllegalStateException(this + " is still at base");
        }
        return trackPosition;
    }

    void leaveBase(int entryCellPosition) {
        this.trackPosition = entryCellPosition;
        this.atBase = false;
    }

    void moveTo(int newTrackPosition) {
        this.trackPosition = newTrackPosition;
    }

    @Override
    public String toString() {
        return color.getShortCode() + pieceNumber;
    }
}
