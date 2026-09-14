package gamefacade;

import command.Command;
import dice.Dice;
import dice.SixSidedDice;
import gamemessage.GameMessage;
import gamemessage.GameMessageCenter;
import gamemessage.GameMessagePublisher;
import gamemessage.GameMessageType;
import gameoutput.ConsoleGameObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import rule.MovementRule;
import rule.TurnRule;

// Facade: Main only ever calls startGame(). Game logic here only
// publishes GameMessages; ConsoleGameObserver decides the wording
// (Observer pattern), so this class never calls System.out itself.
public final class GameFacade {

    private static final int TEST_ROUND_COUNT = 20;

    // Chain of Responsibility: a 6 lets a Base piece enter the board
    // (Rule 2); otherwise an already-entered piece moves forward
    // (Rule 1). Both rules are stateless, so one chain is reused.
    private static final TurnRule TURN_RULE_CHAIN = buildTurnRuleChain();

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
                playTurn(player, dice, board, messages);
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

    private static TurnRule buildTurnRuleChain() {
        TurnRule baseExitRule = new BaseExitRule();
        TurnRule movementRule = new MovementRule();
        baseExitRule.setNext(movementRule);
        return baseExitRule;
    }

    // Ask the rule chain (Rule 2 then Rule 1) for the one legal
    // Command this roll produces, if any, and run it.
    private static void playTurn(
            Player player, Dice dice, Board board, GameMessagePublisher messages) {
        messages.publish(GameMessage.turnStarted(player.getColor()));

        int rollValue = dice.roll();
        messages.publish(GameMessage.turnRolled(player.getColor(), rollValue));

        Optional<Command> command = TURN_RULE_CHAIN.handle(player, rollValue, board);
        if (command.isPresent()) {
            command.get().execute(messages);
        } else {
            messages.publish(GameMessage.noPieceMovable());
        }
    }
}
