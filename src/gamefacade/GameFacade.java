package gamefacade;

import dice.Dice;
import dice.SixSidedDice;
import ludoboard.Board;
import ludoboard.HomeStraightCell;
import ludoboard.LudoBoard;
import ludoboard.PlayerColor;
import numbergenerator.SeededRandomNumberGenerator;

// Facade: Main only ever calls startGame().
public final class GameFacade {

    private static final int ROLLS_TO_SIMULATE = 6;

    private GameFacade() {
    }

    public static void startGame(long seed) {
        SeededRandomNumberGenerator.getInstance().setSeed(seed);

        Board board = LudoBoard.getInstance();
        Dice dice = SixSidedDice.getInstance();

        simulateDiceRolls(dice);
        printBoardLayout(board);
        
    }

    // Stand-in for a future GameEngine's turn loop, which will roll for a player's piece instead of printing raw dice values.
    private static void simulateDiceRolls(Dice dice) {
        for (int rollNumber = 0; rollNumber < ROLLS_TO_SIMULATE; rollNumber++) {
            System.out.println(dice.roll());
        }
    }

    private static void printBoardLayout(Board board) {
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
