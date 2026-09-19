package controller;

import config.enums.GameMessageType;
import config.enums.PlayerColor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import message.GameMessage;
import model.board.Board;
import model.board.LudoBoard;
import model.effect.activation.EffectActivationRule;
import model.effect.activation.MysteryTeleportActivationRule;
import model.effect.mysterycell.MysteryCellManager;
import model.effect.rule.AlphaEffectRule;
import model.effect.rule.GammaDirectionRule;
import model.effect.rule.MysteryCellEffects;
import model.piece.Piece;
import model.player.BluePlayer;
import model.player.GreenPlayer;
import model.player.Player;
import model.player.RedPlayer;
import model.player.YellowPlayer;
import model.player.rule.block.BlockStepsRule;
import model.player.rule.block.BlockadeBreakRule;
import model.player.rule.block.BlockadeLimitRule;
import model.player.rule.block.DivideByBlockSizeRule;
import model.player.rule.block.PassingBlockadeRule;
import model.player.rule.block.ThirdSixBlockadeBreakRule;
import model.player.rule.capture.BlockCaptureRule;
import model.player.rule.capture.CaptureCheckRule;
import model.player.rule.capture.PieceCaptureRule;
import model.player.rule.home.ApproachPassCountRule;
import model.player.rule.home.ExactRollRule;
import model.player.rule.home.HomeStraightEligibilityRule;
import model.player.rule.home.HomeStraightEntryRule;
import model.player.rule.home.OvershootHomeRule;
import model.player.rule.mystery.BetaRestrictionRule;
import model.player.rule.mystery.MysteryCellTeleportRule;
import model.player.rule.roll.ConsecutiveSixVoidRule;
import model.player.rule.roll.RollValidityRule;
import model.player.rule.turn.EnterBoardRule;
import model.player.rule.turn.MovePiecesRule;
import model.player.rule.turn.TurnRule;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.PlayerStrategyRegistry;
import model.player.strategy.blockdirection.BlockTravelDirectionStrategy;
import model.player.strategy.blockdirection.LongestDistanceDirectionStrategy;
import model.player.strategy.playstyle.BlueStrategy;
import model.player.strategy.playstyle.EnterBoardFirstStrategy;
import model.player.strategy.playstyle.GreenStrategy;
import model.player.strategy.playstyle.RedStrategy;
import model.player.strategy.playstyle.YellowStrategy;
import utils.coin.CoinToss;
import utils.coin.SeededCoinToss;
import utils.dice.Dice;
import utils.dice.SixSidedDice;
import utils.randomgenerator.SeededRandomNumberGenerator;
import view.ConsoleGameObserver;
import view.observer.GameMessageCenter;
import view.observer.GameMessagePublisher;

public final class GameFacade {

    // GAME_OVER: game ends once 3 finish - 4th is automatic.
    private static final int REQUIRED_FINISHERS_TO_END_GAME = 3;

    // T-4: shared so movement and display agree on block direction.
    private static final BlockTravelDirectionStrategy BLOCK_TRAVEL_DIRECTION_STRATEGY =
        new LongestDistanceDirectionStrategy();

    private GameFacade() {}

    public static void startGame(long seed) {

        List<Player> players = buildPlayers();
        Board board = LudoBoard.getInstance();

        GameMessageCenter.getInstance().clearObservers();
        GameMessagePublisher messages = GameMessageCenter.getInstance();
        messages.addObserver(new ConsoleGameObserver(players, board, BLOCK_TRAVEL_DIRECTION_STRATEGY));

        SeededRandomNumberGenerator.getInstance().setSeed(seed);

        Dice dice = SixSidedDice.getInstance();

        // 3.1: announce every player's own pieces before starting.
        announcePlayerRoster(players, messages);

        messages.publish(GameMessage.of(GameMessageType.GAME_STARTING));

        // The toss order is also clockwise, starting from Red.
        List<Player> tossOrder = buildTurnOrder(PlayerColor.RED, players, board);
        Player firstPlayer = determineFirstPlayer(tossOrder, dice, messages);
        List<Player> turnOrder = buildTurnOrder(firstPlayer.getColor(), players, board);

        // T-10: reuses the same seeded random source for reproducibility.
        MysteryCellManager mysteryCellManager =
            new MysteryCellManager(board, SeededRandomNumberGenerator.getInstance());

        // T-11: each game needs its own MysteryCellManager instance.
        GameEngine gameEngine = buildGameEngine(mysteryCellManager);

        // GAME_OVER: tracks finish order until enough players finish.
        List<PlayerColor> finishOrder = new ArrayList<>();
        RoundTracker roundTracker = new RoundTracker(turnOrder);

        while (finishOrder.size() < REQUIRED_FINISHERS_TO_END_GAME) {
            int roundNumber = roundTracker.startNextRound();
            messages.publish(GameMessage.roundStarted(roundNumber));
            mysteryCellManager.onRoundStarted(roundNumber, players, messages);

            // T-12/T-13: expire this round's effects and Beta restriction first.
            for (Player player : roundTracker.getTurnOrder()) {
                player.tickMovementEffects();
                player.tickRestrictions();
            }

            for (Player player : roundTracker.getTurnOrder()) {
                gameEngine.playTurn(player, players, dice, board, messages);
                recordFinisherIfNewlyDone(player, finishOrder);
            }

            mysteryCellManager.onRoundCompleted(roundNumber, players);
            messages.publish(GameMessage.boardStateReported(roundNumber));
        }

        messages.publish(GameMessage.gameOver(buildFinalStandings(finishOrder, players)));
    }

    // GAME_OVER: record a player the moment its 4th piece reaches Home.
    private static void recordFinisherIfNewlyDone(Player player, List<PlayerColor> finishOrder) {
        if (player.hasAllPiecesHome() && !finishOrder.contains(player.getColor())) {
            finishOrder.add(player.getColor());
        }
    }

    // GAME_OVER: ranks finishers, then appends the one unfinished player.
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

    // Message per player, naming its pieces.
    private static void announcePlayerRoster(List<Player> players, GameMessagePublisher messages) {
        for (Player player : players) {
            messages.publish(GameMessage.playerRosterAnnounced(player.getColor(), buildPieceLabels(player)));
        }
    }

    private static List<String> buildPieceLabels(Player player) {
        return player.getPieces().stream().map(Piece::toString).toList();
    }

    private static List<Player> buildPlayers() {
        return List.of(
            new RedPlayer(),
            new YellowPlayer(),
            new GreenPlayer(),
            new BluePlayer());
    }

    // Rerolls everyone when two or more players tie highest.
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

    // Rule 3: turn order runs clockwise from a given color.
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

    private static GameEngine buildGameEngine(MysteryCellManager mysteryCellManager) {
        Board board = LudoBoard.getInstance();

        BlockadeLimitRule blockadeLimitRule = new PassingBlockadeRule();
        HomeStraightEntryRule homeStraightEntryRule = new ApproachPassCountRule();
        homeStraightEntryRule.setNext(new HomeStraightEligibilityRule());
        ExactRollRule exactRollRule = new OvershootHomeRule();
        BlockStepsRule blockStepsRule = new DivideByBlockSizeRule();

        CoinToss coinToss = SeededCoinToss.getInstance();
        List<TurnRule> turnRules = List.of(
            new EnterBoardRule(coinToss),
            new MovePiecesRule(
                blockadeLimitRule, homeStraightEntryRule, exactRollRule, blockStepsRule,
                BLOCK_TRAVEL_DIRECTION_STRATEGY));

        // T-16-19: each color gets its own strategy; default is fallback.
        PlayerStrategy defaultStrategy = new EnterBoardFirstStrategy();
        Map<PlayerColor, PlayerStrategy> strategiesByColor = Map.of(
            PlayerColor.RED, new RedStrategy(),
            PlayerColor.GREEN, new GreenStrategy(),
            PlayerColor.YELLOW, new YellowStrategy(),
            PlayerColor.BLUE, new BlueStrategy());
        PlayerStrategyRegistry strategyRegistry =
            new PlayerStrategyRegistry(strategiesByColor, defaultStrategy);

        RollValidityRule rollValidityRule = new ConsecutiveSixVoidRule();
        CaptureCheckRule captureCheckRule = new BlockCaptureRule();
        captureCheckRule.setNext(new PieceCaptureRule());
        BlockadeBreakRule blockadeBreakRule = new ThirdSixBlockadeBreakRule(homeStraightEntryRule);

        // T-12: reuses T-1's same seeded coin toss for effects.
        AlphaEffectRule alphaEffectRule = new AlphaEffectRule(SeededCoinToss.getInstance());

        // T-14: reverses direction, or forwards to Beta, on Gamma.
        GammaDirectionRule gammaDirectionRule = new GammaDirectionRule(board);

        // T-15: effects only activate after genuine Mystery Cell teleport.
        EffectActivationRule effectActivationRule = new MysteryTeleportActivationRule();
        MysteryCellEffects mysteryCellEffects =
            new MysteryCellEffects(alphaEffectRule, gammaDirectionRule, effectActivationRule);

        // T-11: reuses the same seeded random source for reproducibility.
        MysteryCellTeleportRule mysteryCellTeleportRule = new MysteryCellTeleportRule(
            mysteryCellManager, SeededRandomNumberGenerator.getInstance(), board, mysteryCellEffects);

        // T-13: checks each roll for Beta's consecutive-3 return trigger.
        BetaRestrictionRule betaRestrictionRule = new BetaRestrictionRule();

        return new GameEngine(
            turnRules, strategyRegistry, rollValidityRule, captureCheckRule, blockadeBreakRule,
            mysteryCellTeleportRule, betaRestrictionRule
        );
    }
}
