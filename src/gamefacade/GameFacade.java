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
import java.util.Map;
import ludoboard.Board;
import ludoboard.LudoBoard;
import ludoboard.PlayerColor;
import mysterycell.AlphaEffectRule;
import mysterycell.EffectActivationRule;
import mysterycell.GammaDirectionRule;
import mysterycell.MysteryCellEffects;
import mysterycell.MysteryCellManager;
import mysterycell.MysteryTeleportActivationRule;
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
import rule.BetaRestrictionRule;
import rule.BlockCaptureRule;
import rule.BlockMovementRule;
import rule.BlockadeBreakRule;
import rule.BlockadeRule;
import rule.CaptureRule;
import rule.ConsecutiveSixVoidRule;
import rule.DivideByBlockSizeRule;
import rule.MovementRule;
import rule.MysteryCellTeleportRule;
import rule.OpponentBlockadeRule;
import rule.OpponentCaptureRule;
import rule.RollValidityRule;
import rule.ThirdSixBlockadeBreakRule;
import rule.TurnRule;
import strategy.BlueStrategy;
import strategy.GreenStrategy;
import strategy.PlayerStrategy;
import strategy.PlayerStrategyRegistry;
import strategy.PreferEnteringBoardStrategy;
import strategy.RedStrategy;
import strategy.YellowStrategy;
import turn.StandardTurnProcessor;
import turn.TurnProcessor;

// Facade: Main only calls startGame(). Game logic publishes
// messages; ConsoleGameObserver decides the wording.
public final class GameFacade {

    // GAME_OVER: the game ends once this many players have every piece Home - the one
    // remaining player is automatically last place, so a 4th finisher is never needed.
    private static final int REQUIRED_FINISHERS_TO_END_GAME = 3;

    // T-4: shared so a block's direction is resolved the same way for movement and display.
    private static final BlockDirectionStrategy BLOCK_DIRECTION_STRATEGY =
            new LongestDistanceBlockDirectionStrategy();

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

        // T-10: shares the same seeded random source as everything else, for reproducibility.
        MysteryCellManager mysteryCellManager =
                new MysteryCellManager(board, SeededRandomNumberGenerator.getInstance());
        // T-11: this game's turn processor needs its own MysteryCellManager, so it is built per game.
        TurnProcessor turnProcessor = buildTurnProcessor(mysteryCellManager);

        // GAME_OVER: tracks the order in which players finish (every piece Home); the game
        // keeps running rounds until enough players have finished to decide the standings.
        List<PlayerColor> finishOrder = new ArrayList<>();
        int roundNumber = 0;
        while (finishOrder.size() < REQUIRED_FINISHERS_TO_END_GAME) {
            roundNumber++;
            messages.publish(GameMessage.roundStarted(roundNumber));
            mysteryCellManager.onRoundStarted(roundNumber, players, messages);
            // T-12/T-13: expires one round of every piece's Energized/Sick status and Beta
            // restriction before this round's turns.
            for (Player player : turnOrder) {
                player.tickMovementEffects();
                player.tickRestrictions();
            }
            for (Player player : turnOrder) {
                turnProcessor.playTurn(player, players, dice, board, messages);
                recordFinisherIfNewlyDone(player, finishOrder);
            }
            mysteryCellManager.onRoundCompleted(roundNumber, players);
            messages.publish(GameMessage.boardStateReported(roundNumber));
        }

        messages.publish(GameMessage.gameOver(buildFinalStandings(finishOrder, players)));
    }

    // GAME_OVER: a player is recorded the moment its 4th piece reaches Home - checked right
    // after that player's own turn, so finishing order stays accurate even when two players
    // finish within the same round.
    private static void recordFinisherIfNewlyDone(Player player, List<PlayerColor> finishOrder) {
        if (player.hasAllPiecesHome() && !finishOrder.contains(player.getColor())) {
            finishOrder.add(player.getColor());
        }
    }

    // GAME_OVER: ranks the players who finished first, then appends whichever single player
    // never finished - guaranteed to be exactly one, since the game stops as soon as 3 have.
    private static List<PlayerColor> buildFinalStandings(
            List<PlayerColor> finishOrder, List<Player> players) {
        List<PlayerColor> finalStandings = new ArrayList<>(finishOrder);
        for (Player player : players) {
            if (!finalStandings.contains(player.getColor())) {
                finalStandings.add(player.getColor());
            }
        }
        return finalStandings;
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

    private static TurnProcessor buildTurnProcessor(MysteryCellManager mysteryCellManager) {
        Board board = LudoBoard.getInstance();
        BlockadeRule blockadeRule = new OpponentBlockadeRule();
        HomeStraightEntryRule homeStraightEntryRule = new ApproachPassCountRule();
        homeStraightEntryRule.setNext(new HomeStraightEligibilityRule());
        ExactHomeRule exactHomeRule = new OvershootHomeRule();
        BlockMovementRule blockMovementRule = new DivideByBlockSizeRule();
        CoinToss coinToss = SeededCoinToss.getInstance();
        List<TurnRule> turnRules = List.of(
                new BaseExitRule(coinToss),
                new MovementRule(
                        blockadeRule, homeStraightEntryRule, exactHomeRule, blockMovementRule,
                        BLOCK_DIRECTION_STRATEGY));
        // T-16/T-17/T-18/T-19: Red, Green, Yellow, and Blue each get their own decision-making;
        // the default remains as a fallback for any future color without one (OCP).
        PlayerStrategy defaultStrategy = new PreferEnteringBoardStrategy();
        Map<PlayerColor, PlayerStrategy> strategiesByColor = Map.of(
                PlayerColor.RED, new RedStrategy(),
                PlayerColor.GREEN, new GreenStrategy(),
                PlayerColor.YELLOW, new YellowStrategy(),
                PlayerColor.BLUE, new BlueStrategy());
        PlayerStrategyRegistry strategyRegistry =
                new PlayerStrategyRegistry(strategiesByColor, defaultStrategy);
        RollValidityRule rollValidityRule = new ConsecutiveSixVoidRule();
        CaptureRule captureRule = new BlockCaptureRule();
        captureRule.setNext(new OpponentCaptureRule());
        BlockadeBreakRule blockadeBreakRule = new ThirdSixBlockadeBreakRule(homeStraightEntryRule);
        // T-12: the same seeded coin toss T-1 uses for direction, reused for Energized/Sick.
        AlphaEffectRule alphaEffectRule = new AlphaEffectRule(SeededCoinToss.getInstance());
        // T-14: reverses direction, or forwards on to Beta, when a piece/block lands on Gamma.
        GammaDirectionRule gammaDirectionRule = new GammaDirectionRule(board);
        // T-15: Alpha/Beta/Gamma effects activate only after genuine Mystery Cell teleportation.
        EffectActivationRule effectActivationRule = new MysteryTeleportActivationRule();
        MysteryCellEffects mysteryCellEffects =
                new MysteryCellEffects(alphaEffectRule, gammaDirectionRule, effectActivationRule);
        // T-11: shares the same seeded random source as everything else, for reproducibility.
        MysteryCellTeleportRule mysteryCellTeleportRule = new MysteryCellTeleportRule(
                mysteryCellManager, SeededRandomNumberGenerator.getInstance(), board, mysteryCellEffects);
        // T-13: checks every roll for the Beta restriction's consecutive-3 return-to-base condition.
        BetaRestrictionRule betaRestrictionRule = new BetaRestrictionRule();
        return new StandardTurnProcessor(
                turnRules, strategyRegistry, rollValidityRule, captureRule, blockadeBreakRule,
                mysteryCellTeleportRule, betaRestrictionRule);
    }
}
