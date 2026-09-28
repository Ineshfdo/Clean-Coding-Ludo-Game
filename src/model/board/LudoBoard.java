package model.board;

import java.util.EnumMap;
import java.util.Map;

import config.constant.BoardConstants;
import config.constant.MysteryCellConstants;
import config.enums.PlayerColor;
import model.board.cell.HomeStraightCell;
import model.board.cell.StandardCell;

// The board of the game (Singleton)
 
public final class LudoBoard implements Board, MysteryCellPositions, TurnOrderLayout {

    private static final LudoBoard SHARED_INSTANCE = new LudoBoard();

    private final Map<PlayerColor, Integer> approachPositionByColor;
    private final Map<PlayerColor, Integer> entryPositionByColor;
    private final StandardCell[] standardCells;
    private final Map<PlayerColor, HomeStraightCell[]> homeStraightCellsByColor;
    private final Map<PlayerColor, PlayerColor> nextColorClockwiseByColor; // Turn Order

    // Builds the fixed board geometry once, for the single shared instance.
    private LudoBoard() {
        this.approachPositionByColor = buildApproachPositions();
        this.entryPositionByColor = buildEntryPositions();
        this.standardCells = buildStandardCells();
        this.homeStraightCellsByColor = buildHomeStraightCells();
        this.nextColorClockwiseByColor = buildNextColorClockwise();
    }

        /**
         * Gives the one shared board.
        
         * @return the shared instance
        */
    
    public static LudoBoard getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int getStandardCellCount() {
        return BoardConstants.STANDARD_CELL_COUNT;
    }

    @Override
    public int getHomeStraightLength() {
        return BoardConstants.CELLS_PER_HOME_STRAIGHT;
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
        return MysteryCellConstants.ALPHA_CELL_POSITION;
    }

    @Override
    public int getBetaCellPosition() {
        return MysteryCellConstants.BETA_CELL_POSITION;
    }

    @Override
    public int getGammaCellPosition() {
        return MysteryCellConstants.GAMMA_CELL_POSITION;
    }

    @Override
    public int getPositionAfterMoving(int currentPosition, int steps) {
        return (currentPosition + steps) % BoardConstants.STANDARD_CELL_COUNT;
    }

    @Override
    public int getPositionAfterMovingBackward(int currentPosition, int steps) {
        return ((currentPosition - steps) % BoardConstants.STANDARD_CELL_COUNT
            + BoardConstants.STANDARD_CELL_COUNT) % BoardConstants.STANDARD_CELL_COUNT;
    }

    @Override
    public int getForwardDistance(int fromPosition, int toPosition) {
        return ((toPosition - fromPosition) % BoardConstants.STANDARD_CELL_COUNT
            + BoardConstants.STANDARD_CELL_COUNT) % BoardConstants.STANDARD_CELL_COUNT;
    }

    @Override
    public PlayerColor getNextColorClockwise(PlayerColor color) {
        return nextColorClockwiseByColor.get(color);
    }

    private static Map<PlayerColor, Integer> buildApproachPositions() {
        Map<PlayerColor, Integer> positionByColor = new EnumMap<>(PlayerColor.class);

        positionByColor.put(PlayerColor.YELLOW, BoardConstants.YELLOW_APPROACH_POSITION);
        positionByColor.put(PlayerColor.BLUE, BoardConstants.BLUE_APPROACH_POSITION);
        positionByColor.put(PlayerColor.RED, BoardConstants.RED_APPROACH_POSITION);
        positionByColor.put(PlayerColor.GREEN, BoardConstants.GREEN_APPROACH_POSITION);

        return positionByColor;
    }

    private static Map<PlayerColor, Integer> buildEntryPositions() {
        Map<PlayerColor, Integer> positionByColor = new EnumMap<>(PlayerColor.class);

        positionByColor.put(PlayerColor.YELLOW, BoardConstants.YELLOW_ENTRY_POSITION);
        positionByColor.put(PlayerColor.BLUE, BoardConstants.BLUE_ENTRY_POSITION);
        positionByColor.put(PlayerColor.RED, BoardConstants.RED_ENTRY_POSITION);
        positionByColor.put(PlayerColor.GREEN, BoardConstants.GREEN_ENTRY_POSITION);

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

    // Builds every track cell, marking each colour's Approach and Entry cell.
    private StandardCell[] buildStandardCells() {
        PlayerColor[] approachOwnerAtPosition = buildOwnerAtPosition(approachPositionByColor);
        PlayerColor[] entryOwnerAtPosition = buildOwnerAtPosition(entryPositionByColor);

        StandardCell[] cells = new StandardCell[BoardConstants.STANDARD_CELL_COUNT];

        for (int position = 0; position < BoardConstants.STANDARD_CELL_COUNT; position++) {
            cells[position] = new StandardCell(
            position, approachOwnerAtPosition[position], entryOwnerAtPosition[position]);
        }

        return cells;
    }

    // Builds an array mapping each position to its owning colour, so buildStandardCells can look it up by position.
    private static PlayerColor[] buildOwnerAtPosition(Map<PlayerColor, Integer> positionByColor) {
        PlayerColor[] ownerAtPosition = new PlayerColor[BoardConstants.STANDARD_CELL_COUNT];

        for (Map.Entry<PlayerColor, Integer> entry : positionByColor.entrySet()) {
            ownerAtPosition[entry.getValue()] = entry.getKey();
        }

        return ownerAtPosition;
    }

    // Builds each colour's own Home Straight cells, in order from the Approach.
    private static Map<PlayerColor, HomeStraightCell[]> buildHomeStraightCells() {
        Map<PlayerColor, HomeStraightCell[]> cellsByColor = new EnumMap<>(PlayerColor.class);

        for (PlayerColor color : PlayerColor.values()) {
            HomeStraightCell[] cells = new HomeStraightCell[BoardConstants.CELLS_PER_HOME_STRAIGHT];

            for (int index = 0; index < cells.length; index++) {
                cells[index] = new HomeStraightCell(color, index);
            }

            cellsByColor.put(color, cells);
        }

        return cellsByColor;
    }
}
