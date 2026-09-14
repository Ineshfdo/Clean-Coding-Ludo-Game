import dice.Dice;
import dice.SixSidedDice;
import ludoboard.Board;
import ludoboard.HomeStraightCell;
import ludoboard.LudoBoard;
import ludoboard.PlayerColor;
import numbergenerator.SeededRandomNumberGenerator;

public class main {

    private static final long DICE_SEED = 5L;
    private static final int ROLLS_TO_DEMONSTRATE = 6;

    public static void main(String[] args) {
        demonstrateDiceRolls();
        demonstrateBoardLayout();
    }

    private static void demonstrateDiceRolls() {
        SeededRandomNumberGenerator.getInstance().setSeed(DICE_SEED);

        Dice sharedDice = SixSidedDice.getInstance();
        for (int rollNumber = 0; rollNumber < ROLLS_TO_DEMONSTRATE; rollNumber++) {
            System.out.println(sharedDice.roll());
        }
    }

    private static void demonstrateBoardLayout() {
        Board board = LudoBoard.getInstance();

        for (PlayerColor color : PlayerColor.values()) {
            System.out.println(color.getDisplayName()
                    + " approach=" + board.getApproachCellPosition(color)
                    + " entry=" + board.getEntryCellPosition(color));

            for (int index = 0; index < HomeStraightCell.CELLS_PER_HOME_STRAIGHT; index++) {
                System.out.println(board.getHomeStraightCell(color, index));
            }
        }
    }
}
