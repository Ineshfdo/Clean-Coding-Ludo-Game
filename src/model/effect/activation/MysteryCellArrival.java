package model.effect.activation;

import config.enums.MysteryCellDestinationType;

// T-15: proof a piece arrived by a genuine Mystery Cell teleport.
public final class MysteryCellArrival {

    private final MysteryCellDestinationType destinationType;

    public MysteryCellArrival(MysteryCellDestinationType destinationType) {
        this.destinationType = destinationType;
    }

    public MysteryCellDestinationType getDestinationType() {
        return destinationType;
    }
}
