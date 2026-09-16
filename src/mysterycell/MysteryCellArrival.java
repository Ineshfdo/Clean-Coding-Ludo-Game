package mysterycell;

// T-15: proof that a piece's current position was reached through genuine Mystery Cell
// teleportation. Only TeleportCommand ever constructs one - ordinary movement commands
// (MoveCommand/BlockMoveCommand) have no way to produce one.
public final class MysteryCellArrival {

    private final MysteryCellDestinationType destinationType;

    public MysteryCellArrival(MysteryCellDestinationType destinationType) {
        this.destinationType = destinationType;
    }

    public MysteryCellDestinationType getDestinationType() {
        return destinationType;
    }
}
