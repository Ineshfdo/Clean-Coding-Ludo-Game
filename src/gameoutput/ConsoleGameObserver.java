package gameoutput;

import gamemessage.GameMessage;
import gamemessage.GameMessageObserver;

// Observer: turns a GameMessage into the actual command-line text.
public final class ConsoleGameObserver implements GameMessageObserver {

    @Override
    public void onGameMessage(GameMessage message) {
        System.out.println(describe(message));
    }

    private static String describe(GameMessage message) {
        return switch (message.getType()) {
            case GAME_INITIALIZING -> "Initializing Game Facade...";
            case BOARD_INITIALIZED -> "Board initialized.";
            case DICE_INITIALIZED -> "Dice initialized.";
            case PLAYERS_CREATED -> "Players created.";
            case GAME_STARTING -> "Starting the Ludo game!\n";
            case TOSS_STARTING -> "Rolling dice to determine who goes first...";
            case DICE_ROLLED ->
                    message.getColor() + " Player rolled a " + message.getRollValue();
            case TOSS_TIED ->
                    "There was a tie for the highest roll (" + message.getRollValue()
                            + ")! Everyone rerolls...\n";
            case TOSS_WON ->
                    message.getColor() + " Player won the toss with a "
                            + message.getRollValue() + " and goes first!";
            case ROUND_STARTED ->
                    "\n" + message.getRoundNumber() + ". Round " + message.getRoundNumber();
            case TURN_STARTED -> "- " + message.getColor() + " Player's Turn -";
            case TURN_ROLLED -> "  -> Rolled a " + message.getRollValue();
            case NO_PIECE_MOVABLE -> "  -> No pieces on the board could be moved.";
            case PIECE_MOVED ->
                    "  -> Moved " + message.getPieceLabel() + " to cell "
                            + message.getNewPosition() + ".";
            case PIECE_ENTERED_BOARD ->
                    "  -> " + message.getPieceLabel()
                            + " left Base and entered the board at (X) position cell "
                            + message.getNewPosition() + ".";
            case PIECE_ENTERED_HOME_STRAIGHT ->
                    "  -> " + message.getPieceLabel() + " entered its HomeStraight at "
                            + message.getHomeStraightCellLabel() + ".";
            case PIECE_REACHED_HOME ->
                    "  -> " + message.getPieceLabel() + " reached Home and is removed from play!";
            case THIRD_SIX_VOIDED ->
                    "  -> Three sixes in a row! This roll is void - turn passes to the next player.";
        };
    }
}
