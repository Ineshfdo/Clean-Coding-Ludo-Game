package gamefacade;

import coin.CoinToss;
import coin.SeededCoinToss;
import dice.Dice;
import dice.SixSidedDice;
import gamemessage.GameMessage;
import gamemessage.GameMessageCenter;
import gamemessage.GameMessagePublisher;
import gamemessage.GameMessageType;
import gameoutput.ConsoleGameObserver;
import java.util.ArrayList;
import java.util.List;
import ludoboard.Board;
import ludoboard.LudoBoard;
import ludoboard.PlayerColor;
import numbergenerator.SeededRandomNumberGenerator;
import player.ApproachPassCountRule;
import player.BlockDirectionStrategy;
import player.BluePlayer;
import player.ExactHomeRule;
import player.GreenPlayer;
import player.HomeStraightEligibilityRule;
import player.HomeStraightEntryRule;
import player.LongestDistanceBlockDirectionStrategy;
import player.OvershootHomeRule;
import player.Player;
import player.RedPlayer;
import player.YellowPlayer;
import rule.BaseExitRule;
import rule.BlockMovementRule;
import rule.BlockadeBreakRule;
import rule.BlockadeRule;
import rule.CaptureRule;
import rule.ConsecutiveSixVoidRule;
import rule.MixedDirectionBlockMovementRule;
import rule.MovementRule;
import rule.OpponentBlockadeRule;
import rule.OpponentCaptureRule;
import rule.RollValidityRule;
import rule.ThirdSixBlockadeBreakRule;
import rule.TurnRule;
import strategy.PlayerStrategy;
import strategy.PreferEnteringBoardStrategy;
import turn.StandardTurnProcessor;
import turn.TurnProcessor;

// Facade: Main only calls startGame(). Game logic publishes
// messages; ConsoleGameObserver decides the wording.
public final class GameFacade {

    private static final int TEST_ROUND_COUNT = 23;

    // T-4: shared so a block's direction is resolved the same way for movement and display.
    private static final BlockDirectionStrategy BLOCK_DIRECTION_STRATEGY =
            new LongestDistanceBlockDirectionStrategy();

    // Wires Rules 1/2, Rule 4's void check, and its Strategy
    // into one reusable turn processor.
    private static final TurnProcessor TURN_PROCESSOR = buildTurnProcessor();

    private GameFacade() {
    }

    public static void startGame(long seed) {
        // Built before the observer, which needs the roster and board for reports.
        List<Player> players = buildPlayers();
        Board board = LudoBoard.getInstance();

        GameMessageCenter.getInstance().clearObservers();
        GameMessagePublisher messages = GameMessageCenter.getInstance();
        messages.addObserver(new ConsoleGameObserver(players, board, BLOCK_DIRECTION_STRATEGY));

        messages.publish(GameMessage.of(GameMessageType.GAME_INITIALIZING));
        SeededRandomNumberGenerator.getInstance().setSeed(seed);

        messages.publish(GameMessage.of(GameMessageType.BOARD_INITIALIZED));

        Dice dice = SixSidedDice.getInstance();
        messages.publish(GameMessage.of(GameMessageType.DICE_INITIALIZED));

        messages.publish(GameMessage.of(GameMessageType.PLAYERS_CREATED));

        messages.publish(GameMessage.of(GameMessageType.GAME_STARTING));

        // The toss itself also proceeds clockwise; Red started.
        List<Player> tossOrder = buildTurnOrder(PlayerColor.RED, players, board);
        Player firstPlayer = determineFirstPlayer(tossOrder, dice, messages);
        List<Player> turnOrder = buildTurnOrder(firstPlayer.getColor(), players, board);

        for (int roundNumber = 1; roundNumber <= TEST_ROUND_COUNT; roundNumber++) {
            messages.publish(GameMessage.roundStarted(roundNumber));
            for (Player player : turnOrder) {
                TURN_PROCESSOR.playTurn(player, players, dice, board, messages);
            }
            messages.publish(GameMessage.boardStateReported(roundNumber));
        }
    }

    private static List<Player> buildPlayers() {
        return List.of(
                new RedPlayer(),
                new YellowPlayer(),
                new GreenPlayer(),
                new BluePlayer());
    }

    // Rerolls everyone when two or more players tie for
    // the highest roll.
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

    // Rule 3: turn order runs clockwise from a given
    // color - toss start or winner.
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
        BlockadeRule blockadeRule = new OpponentBlockadeRule();
        HomeStraightEntryRule homeStraightEntryRule = new ApproachPassCountRule();
        homeStraightEntryRule.setNext(new HomeStraightEligibilityRule());
        ExactHomeRule exactHomeRule = new OvershootHomeRule();
        BlockMovementRule blockMovementRule = new MixedDirectionBlockMovementRule();
        CoinToss coinToss = SeededCoinToss.getInstance();
        List<TurnRule> turnRules = List.of(
                new BaseExitRule(coinToss),
                new MovementRule(
                        blockadeRule, homeStraightEntryRule, exactHomeRule, blockMovementRule,
                        BLOCK_DIRECTION_STRATEGY));
        PlayerStrategy strategy = new PreferEnteringBoardStrategy();
        RollValidityRule rollValidityRule = new ConsecutiveSixVoidRule();
        CaptureRule captureRule = new OpponentCaptureRule();
        BlockadeBreakRule blockadeBreakRule = new ThirdSixBlockadeBreakRule(homeStraightEntryRule);
        return new StandardTurnProcessor(
                turnRules, strategy, rollValidityRule, captureRule, blockadeBreakRule);
    }
}
