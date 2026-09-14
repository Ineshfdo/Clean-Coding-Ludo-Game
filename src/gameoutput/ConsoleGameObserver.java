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
            case TOSS_WON ->
                    "\n" + message.getColor() + " Player won the toss with a "
                            + message.getRollValue() + " and goes first!\n";
            case TURN_STARTED -> message.getColor() + " Player's turn:";
        };
    }
}
