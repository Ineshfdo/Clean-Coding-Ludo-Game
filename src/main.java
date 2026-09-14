import dice.Dice;
import dice.SixSidedDice;
import numbergenerator.SeededRandomNumberGenerator;

public class main {

    private static final long DICE_SEED = 5L;
    private static final int ROLLS_TO_DEMONSTRATE = 3;

    public static void main(String[] args) {
        SeededRandomNumberGenerator.getInstance().setSeed(DICE_SEED);

        Dice sharedDice = SixSidedDice.getInstance();
        for (int rollNumber = 0; rollNumber < ROLLS_TO_DEMONSTRATE; rollNumber++) {
            System.out.println(sharedDice.roll());
        }
    }
}
