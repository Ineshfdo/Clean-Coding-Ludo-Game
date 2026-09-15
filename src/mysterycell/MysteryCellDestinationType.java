package mysterycell;

// T-11: the six possible outcomes when a piece lands on the Mystery Cell.
public enum MysteryCellDestinationType {
    ALPHA("Alpha"),
    BETA("Beta"),
    GAMMA("Gamma"),
    BASE("Base"),
    ENTRY("Entry"),
    APPROACH("Approach");

    private final String label;

    MysteryCellDestinationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
