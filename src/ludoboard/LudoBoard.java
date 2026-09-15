package ludoboard;

import java.util.EnumMap;
import java.util.Map;

// Singleton: one board layout is shared by every player, piece, and rules.
public final class LudoBoard implements Board {

    private static final int STANDARD_CELL_COUNT = 52;

    // T-11: fixed Mystery Cell teleport destinations - the same three cells for every color.
    private static final int ALPHA_CELL_POSITION = 9;
    private static final int BETA_CELL_POSITION = 27;
    private static final int GAMMA_CELL_POSITION = 46;

    private static final LudoBoard SHARED_INSTANCE = new LudoBoard();

    private final Map<PlayerColor, Integer> approachPositionByColor;
    private final Map<PlayerColor, Integer> entryPositionByColor;
    private final StandardCell[] standardCells;
    private final Map<PlayerColor, HomeStraightCell[]> homeStraightCellsByColor;
    private final Map<PlayerColor, PlayerColor> nextColorClockwiseByColor;

    private LudoBoard() {
        this.approachPositionByColor = buildApproachPositions();
        this.entryPositionByColor = buildEntryPositions();
        this.standardCells = buildStandardCells();
        this.homeStraightCellsByColor = buildHomeStraightCells();
        this.nextColorClockwiseByColor = buildNextColorClockwise();
    }

    public static LudoBoard getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int getStandardCellCount() {
        return STANDARD_CELL_COUNT;
    }

    @Override
    public StandardCell getStandardCell(int position) {
        return standardCells[position];
    }

    @Override
    public HomeStraightCell getHomeStraightCell(PlayerColor color, int indexFromApproach) {
        return homeStraightCellsByColor.get(color)[indexFromApproach];
    }

    @Override
    public int getApproachCellPosition(PlayerColor color) {
        return approachPositionByColor.get(color);
    }

    @Override
    public int getEntryCellPosition(PlayerColor color) {
        return entryPositionByColor.get(color);
    }

    @Override
    public int getAlphaCellPosition() {
        return ALPHA_CELL_POSITION;
    }

    @Override
    public int getBetaCellPosition() {
        return BETA_CELL_POSITION;
    }

    @Override
    public int getGammaCellPosition() {
        return GAMMA_CELL_POSITION;
    }

    @Override
    public int getPositionAfterMoving(int currentPosition, int steps) {
        return (currentPosition + steps) % STANDARD_CELL_COUNT;
    }

    @Override
    public int getPositionAfterMovingBackward(int currentPosition, int steps) {
        return ((currentPosition - steps) % STANDARD_CELL_COUNT + STANDARD_CELL_COUNT)
                % STANDARD_CELL_COUNT;
    }

    @Override
    public int getForwardDistance(int fromPosition, int toPosition) {
        return ((toPosition - fromPosition) % STANDARD_CELL_COUNT + STANDARD_CELL_COUNT)
                % STANDARD_CELL_COUNT;
    }

    @Override
    public PlayerColor getNextColorClockwise(PlayerColor color) {
        return nextColorClockwiseByColor.get(color);
    }

    private static Map<PlayerColor, Integer> buildApproachPositions() {
        Map<PlayerColor, Integer> positionByColor = new EnumMap<>(PlayerColor.class);
        positionByColor.put(PlayerColor.YELLOW, 0);
        positionByColor.put(PlayerColor.BLUE, 13);
        positionByColor.put(PlayerColor.RED, 26);
        positionByColor.put(PlayerColor.GREEN, 39);
        return positionByColor;
    }

    private static Map<PlayerColor, Integer> buildEntryPositions() {
        Map<PlayerColor, Integer> positionByColor = new EnumMap<>(PlayerColor.class);
        positionByColor.put(PlayerColor.YELLOW, 2);
        positionByColor.put(PlayerColor.BLUE, 15);
        positionByColor.put(PlayerColor.RED, 28);
        positionByColor.put(PlayerColor.GREEN, 41);
        return positionByColor;
    }

    // Colors are physically arranged clockwise around the board:
    private static Map<PlayerColor, PlayerColor> buildNextColorClockwise() {
        Map<PlayerColor, PlayerColor> nextColorByColor = new EnumMap<>(PlayerColor.class);
        nextColorByColor.put(PlayerColor.YELLOW, PlayerColor.BLUE);
        nextColorByColor.put(PlayerColor.BLUE, PlayerColor.RED);
        nextColorByColor.put(PlayerColor.RED, PlayerColor.GREEN);
        nextColorByColor.put(PlayerColor.GREEN, PlayerColor.YELLOW);
        return nextColorByColor;
    }

    private StandardCell[] buildStandardCells() {
        PlayerColor[] approachOwnerAtPosition = buildOwnerAtPosition(approachPositionByColor);
        PlayerColor[] entryOwnerAtPosition = buildOwnerAtPosition(entryPositionByColor);

        StandardCell[] cells = new StandardCell[STANDARD_CELL_COUNT];
        for (int position = 0; position < STANDARD_CELL_COUNT; position++) {
            cells[position] = new StandardCell(
            position, approachOwnerAtPosition[position], entryOwnerAtPosition[position]);
        }
        return cells;
    }

    private static PlayerColor[] buildOwnerAtPosition(Map<PlayerColor, Integer> positionByColor) {
        PlayerColor[] ownerAtPosition = new PlayerColor[STANDARD_CELL_COUNT];
        for (Map.Entry<PlayerColor, Integer> entry : positionByColor.entrySet()) {
            ownerAtPosition[entry.getValue()] = entry.getKey();
        }
        return ownerAtPosition;
    }

    private static Map<PlayerColor, HomeStraightCell[]> buildHomeStraightCells() {
        Map<PlayerColor, HomeStraightCell[]> cellsByColor = new EnumMap<>(PlayerColor.class);
        for (PlayerColor color : PlayerColor.values()) {
            HomeStraightCell[] cells = new HomeStraightCell[HomeStraightCell.CELLS_PER_HOME_STRAIGHT];
            for (int index = 0; index < cells.length; index++) {
                cells[index] = new HomeStraightCell(color, index);
            }
            cellsByColor.put(color, cells);
        }
        return cellsByColor;
    }
}
