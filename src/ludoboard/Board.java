package ludoboard;

// The board-geometry contract. Future Player/Piece/Rules classes depend on this abstraction (DIP) instead of the concrete LudoBoard.

public interface Board {

    int getStandardCellCount();

    StandardCell getStandardCell(int position);

    HomeStraightCell getHomeStraightCell(PlayerColor color, int indexFromApproach);

    int getApproachCellPosition(PlayerColor color);

    int getEntryCellPosition(PlayerColor color);

    int getPositionAfterMoving(int currentPosition, int steps);

    PlayerColor getNextColorClockwise(PlayerColor color);
}
