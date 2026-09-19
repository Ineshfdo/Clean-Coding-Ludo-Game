package model.effect.mysterycell;

// T-19: read-only view of the Mystery Cell's location (ISP).
public interface MysteryCellLocation {

    boolean isActive();

    int getCurrentCellPosition();
}
