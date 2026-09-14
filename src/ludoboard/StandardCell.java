package ludoboard;


// One of the board's 52 shared-track cells. Approach or entryOwner when the board wires it that way during construction.
public final class StandardCell {

    private final int position;
    private final PlayerColor approachOwner;
    private final PlayerColor entryOwner;

    StandardCell(int position, PlayerColor approachOwner, PlayerColor entryOwner) {
        this.position = position;
        this.approachOwner = approachOwner;
        this.entryOwner = entryOwner;
    }

    public int getPosition() {
        return position;
    }

    public boolean isApproachPointFor(PlayerColor color) {
        return approachOwner == color;
    }

    public boolean isEntryPointFor(PlayerColor color) {
        return entryOwner == color;
    }

    @Override
    public String toString() {
        return "StandardCell" + position;
    }
}
