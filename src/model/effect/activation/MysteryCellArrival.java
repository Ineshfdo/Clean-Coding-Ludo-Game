package model.effect.activation;

import model.effect.mysterycell.MysteryCellDestination;

/**
 Proof that a piece arrived by a genuine Mystery Cell teleport (T-15).
 It names the destination that was reached.
 */
public final class MysteryCellArrival {

    private final MysteryCellDestination destination;

    /**
     Creates the proof of an arrival.
     @param destination the destination that was reached
     */
    public MysteryCellArrival(MysteryCellDestination destination) {
        this.destination = destination;
    }

    /**
     Gives the destination that was reached.
     @return the destination
     */
    public MysteryCellDestination getDestination() {
        return destination;
    }
}
