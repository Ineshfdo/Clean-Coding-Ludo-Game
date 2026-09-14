package coin;

// T-1: heads or tails from the coin toss that sets a piece's direction.
public enum CoinTossResult {
    HEADS("Heads"),
    TAILS("Tails");

    private final String label;

    CoinTossResult(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
