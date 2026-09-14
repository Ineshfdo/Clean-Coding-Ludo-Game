package gamefacade;

import gameoutput.ConsoleGameObserver;
import dice.Dice;
import dice.SixSidedDice;
import gamemessage.GameMessage;
import gamemessage.GameMessageCenter;
import gamemessage.GameMessagePublisher;
import gamemessage.GameMessageType;
import java.util.List;
import ludoboard.LudoBoard;
import numbergenerator.SeededRandomNumberGenerator;
import player.BluePlayer;
import player.GreenPlayer;
import player.Player;
import player.RedPlayer;
import player.YellowPlayer;

// Facade: Main only ever calls startGame(). Game logic here only
// publishes GameMessages; ConsoleGameObserver decides the wording
// (Observer pattern), so this class never calls System.out itself.
public final class GameFacade {

    private GameFacade() {
    }

    public static void startGame(long seed) {
        GameMessagePublisher messages = GameMessageCenter.getInstance();
        messages.addObserver(new ConsoleGameObserver());

        messages.publish(GameMessage.of(GameMessageType.GAME_INITIALIZING));
        SeededRandomNumberGenerator.getInstance().setSeed(seed);

        LudoBoard.getInstance();
        messages.publish(GameMessage.of(GameMessageType.BOARD_INITIALIZED));

        Dice dice = SixSidedDice.getInstance();
        messages.publish(GameMessage.of(GameMessageType.DICE_INITIALIZED));

        List<Player> players = buildPlayers();
        messages.publish(GameMessage.of(GameMessageType.PLAYERS_CREATED));

        messages.publish(GameMessage.of(GameMessageType.GAME_STARTING));

        determineFirstPlayer(players, dice, messages);
    }

    private static List<Player> buildPlayers() {
        return List.of(
                new RedPlayer(),
                new YellowPlayer(),
                new GreenPlayer(),
                new BluePlayer());
    }

    private static void determineFirstPlayer(
            List<Player> players, Dice dice, GameMessagePublisher messages) {
        messages.publish(GameMessage.of(GameMessageType.TOSS_STARTING));

        Player firstPlayer = players.get(0);
        int highestRoll = 0;

        for (Player player : players) {
            int rollValue = dice.roll();
            messages.publish(GameMessage.diceRolled(player.getColor(), rollValue));

            if (rollValue > highestRoll) {
                highestRoll = rollValue;
                firstPlayer = player;
            }
        }

        messages.publish(GameMessage.tossWon(firstPlayer.getColor(), highestRoll));
    }
}
