package gamefacade;

import java.util.ArrayList;
import java.util.List;

import dice.Dice;
import dice.SixSidedDice;
import gamemessage.GameMessage;
import gamemessage.GameMessageCenter;
import gamemessage.GameMessagePublisher;
import gamemessage.GameMessageType;
import gameoutput.ConsoleGameObserver;
import ludoboard.Board;
import ludoboard.LudoBoard;
import ludoboard.PlayerColor;
import numbergenerator.SeededRandomNumberGenerator;
import player.BluePlayer;
import player.GreenPlayer;
import player.Player;
import player.RedPlayer;
import player.YellowPlayer;
import rule.BaseExitRule;
import rule.ConsecutiveSixVoidRule;
import rule.MovementRule;
import rule.RollValidityRule;
import rule.TurnRule;
import strategy.PlayerStrategy;
import strategy.PreferEnteringBoardStrategy;
import turn.StandardTurnProcessor;
import turn.TurnProcessor;

// Facade: Main only ever calls startGame(). Game logic here only
// publishes GameMessages; ConsoleGameObserver decides the wording
// (Observer pattern), so this class never calls System.out itself.
public final class GameFacade {

    private static final int TEST_ROUND_COUNT = 20;

    // How a turn plays out (Template Method) is wired up once from
    // its rules (Rule 1/2, Chain of Responsibility), its roll-void
    // check (Rule 4, Chain of Responsibility), and its choice policy
    // (Rule 4, Strategy). Everything here is stateless, so one
    // processor is reused for every player's every turn.
    private static final TurnProcessor TURN_PROCESSOR = buildTurnProcessor();

    private GameFacade() {
    }

    public static void startGame(long seed) {
        GameMessageCenter.getInstance().clearObservers();
        GameMessagePublisher messages = GameMessageCenter.getInstance();
        messages.addObserver(new ConsoleGameObserver());

        messages.publish(GameMessage.of(GameMessageType.GAME_INITIALIZING));
        SeededRandomNumberGenerator.getInstance().setSeed(seed);

        Board board = LudoBoard.getInstance();
        messages.publish(GameMessage.of(GameMessageType.BOARD_INITIALIZED));

        Dice dice = SixSidedDice.getInstance();
        messages.publish(GameMessage.of(GameMessageType.DICE_INITIALIZED));

        List<Player> players = buildPlayers();
        messages.publish(GameMessage.of(GameMessageType.PLAYERS_CREATED));

        messages.publish(GameMessage.of(GameMessageType.GAME_STARTING));

        // The toss itself also proceeds clockwise; Red started.
        List<Player> tossOrder = buildTurnOrder(PlayerColor.RED, players, board);
        Player firstPlayer = determineFirstPlayer(tossOrder, dice, messages);
        List<Player> turnOrder = buildTurnOrder(firstPlayer.getColor(), players, board);

        for (int roundNumber = 1; roundNumber <= TEST_ROUND_COUNT; roundNumber++) {
            messages.publish(GameMessage.roundStarted(roundNumber));
            for (Player player : turnOrder) {
                TURN_PROCESSOR.playTurn(player, dice, board, messages);
            }
        }
    }

    private static List<Player> buildPlayers() {
        return List.of(
                new RedPlayer(),
                new YellowPlayer(),
                new GreenPlayer(),
                new BluePlayer());
    }

    // Rerolls everyone whenever two or more players tie for the
    // highest roll, since a toss must produce exactly one winner.
    private static Player determineFirstPlayer(
            List<Player> players, Dice dice, GameMessagePublisher messages) {
        messages.publish(GameMessage.of(GameMessageType.TOSS_STARTING));

        while (true) {
            Player highestRoller = players.get(0);
            int highestRoll = 0;
            int highestRollerCount = 0;

            for (Player player : players) {
                int rollValue = dice.roll();
                messages.publish(GameMessage.diceRolled(player.getColor(), rollValue));

                if (rollValue > highestRoll) {
                    highestRoll = rollValue;
                    highestRoller = player;
                    highestRollerCount = 1;
                } else if (rollValue == highestRoll) {
                    highestRollerCount++;
                }
            }

            if (highestRollerCount == 1) {
                messages.publish(GameMessage.tossWon(highestRoller.getColor(), highestRoll));
                return highestRoller;
            }

            messages.publish(GameMessage.tossTied(highestRoll));
        }
    }

    // Rule 3: play proceeds clockwise around the board starting from
    // a given color - the toss's fixed start, or later the winner's.
    private static List<Player> buildTurnOrder(
            PlayerColor startingColor, List<Player> players, Board board) {
        List<Player> turnOrder = new ArrayList<>();
        PlayerColor color = startingColor;

        for (int position = 0; position < players.size(); position++) {
            turnOrder.add(findPlayerByColor(players, color));
            color = board.getNextColorClockwise(color);
        }

        return turnOrder;
    }

    private static Player findPlayerByColor(List<Player> players, PlayerColor color) {
        return players.stream()
                .filter(player -> player.getColor() == color)
                .findFirst()
                .orElseThrow();
    }

    private static TurnProcessor buildTurnProcessor() {
        List<TurnRule> turnRules = List.of(new BaseExitRule(), new MovementRule());
        PlayerStrategy strategy = new PreferEnteringBoardStrategy();
        RollValidityRule rollValidityRule = new ConsecutiveSixVoidRule();
        return new StandardTurnProcessor(turnRules, strategy, rollValidityRule);
    }
}
