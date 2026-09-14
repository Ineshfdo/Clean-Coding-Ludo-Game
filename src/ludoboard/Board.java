package ludoboard;

// Board-geometry contract; Player/Piece/Rules depend on this
// abstraction (DIP), not the concrete LudoBoard.

public interface Board {

    int getStandardCellCount();

    StandardCell getStandardCell(int position);

    HomeStraightCell getHomeStraightCell(PlayerColor color, int indexFromApproach);

    int getApproachCellPosition(PlayerColor color);

    int getEntryCellPosition(PlayerColor color);

    int getPositionAfterMoving(int currentPosition, int steps);

    int getForwardDistance(int fromPosition, int toPosition);

    PlayerColor getNextColorClockwise(PlayerColor color);
}
