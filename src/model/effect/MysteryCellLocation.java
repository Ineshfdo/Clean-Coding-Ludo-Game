package model.effect;

// T-19: read-only view of where the Mystery Cell currently is, so a PlayerStrategy (e.g.
// BlueStrategy) can preview whether a candidate move would land on it - without depending on
// MysteryCellManager's full spawn/relocation responsibilities (ISP).
public interface MysteryCellLocation {

    boolean isActive();

    int getCurrentCellPosition();
}
