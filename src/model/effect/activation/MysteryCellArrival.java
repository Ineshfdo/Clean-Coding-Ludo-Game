package model.effect.activation;

import model.effect.mysterycell.MysteryCellDestination;

// T-15: proof a piece arrived by a genuine Mystery Cell teleport.
public final class MysteryCellArrival {

    private final MysteryCellDestination destination;

    public MysteryCellArrival(MysteryCellDestination destination) {
        this.destination = destination;
    }

    public MysteryCellDestination getDestination() {
        return destination;
    }
}
