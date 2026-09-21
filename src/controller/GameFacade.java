package controller;

import config.enums.GameMessageType;
import config.enums.PlayerColor;
import exception.IllegalMoveException;
import exception.InvalidPieceStateException;
import exception.PieceOwnershipException;
import exception.PlayerNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.GameMessage;
import message.observer.GameMessageCenter;
import message.observer.GameMessagePublisher;
import message.observer.GameMessageRegistry;
import model.board.LudoBoard;
import model.direction.CoinTossEntryDirectionAssigner;
import model.effect.activation.EffectActivationRule;
import model.effect.activation.MysteryTeleportActivationRule;
import model.effect.destination.AlphaDestination;
import model.effect.destination.ApproachDestination;
import model.effect.destination.BaseDestination;
import model.effect.destination.BetaDestination;
import model.effect.destination.EntryDestination;
import model.effect.destination.GammaDestination;
import model.effect.mysterycell.MysteryCellDestination;
import model.effect.mysterycell.MysteryCellLocation;
import model.effect.mysterycell.MysteryCellSchedule;
import model.effect.rule.AlphaEffectRule;
import model.effect.rule.GammaDirectionRule;
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
import model.player.rule.home.HomeGateStatus;
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
import utils.randomgenerator.RandomNumberGenerator;
import utils.randomgenerator.SeedableRandomNumberGenerator;
import utils.randomgenerator.SeededRandomNumberGenerator;
import view.ConsoleGameObserver;

public final class GameFacade {

    // T-4: shared so movement and display agree on block direction.
    private static final BlockTravelDirectionStrategy BLOCK_TRAVEL_DIRECTION_STRATEGY =
        new LongestDistanceDirectionStrategy();

    private static final Logger LOGGER = Logger.getLogger(GameFacade.class.getName());

    private GameFacade() {}

    public static void startGame(long seed) {
        try {
            playGame(seed);
        } catch (IllegalMoveException | InvalidPieceStateException
                | PieceOwnershipException | PlayerNotFoundException exception) {
            LOGGER.log(
                    Level.SEVERE, "Game aborted; the simulation cannot continue", exception);
        }
    }

    private static void playGame(long seed) {
        List<Player> allPlayers = buildPlayers();
        LudoBoard board = LudoBoard.getInstance();

        GameMessageRegistry messageRegistry = GameMessageCenter.getInstance();
        messageRegistry.clearObservers();
        GameMessagePublisher messagePublisher = GameMessageCenter.getInstance();
        ConsoleGameObserver consoleObserver =
            new ConsoleGameObserver(allPlayers, board, BLOCK_TRAVEL_DIRECTION_STRATEGY);
        messageRegistry.addObserver(consoleObserver);

        SeedableRandomNumberGenerator randomNumberGenerator = SeededRandomNumberGenerator.getInstance();
        randomNumberGenerator.setSeed(seed);

        Dice dice = SixSidedDice.getInstance();

        // 3.1: announce every player's own pieces before starting.
        announcePlayerRoster(allPlayers, messagePublisher);

        messagePublisher.publish(GameMessage.of(GameMessageType.GAME_STARTING));

        // The toss order is also clockwise, starting from Red.
        TurnOrderBuilder turnOrderBuilder = new TurnOrderBuilder(board);
        List<Player> tossOrder = turnOrderBuilder.buildFrom(PlayerColor.RED, allPlayers);
        Player firstPlayer = new FirstPlayerSelector(dice, messagePublisher).select(tossOrder);
        List<Player> turnOrder = turnOrderBuilder.buildFrom(firstPlayer.getColor(), allPlayers);

        // Round reports list players in play order, starting from the toss winner.
        consoleObserver.setTurnOrder(turnOrder);

        // T-10: reuses the same seeded random source for reproducibility.
        MysteryCellSchedule mysteryCellSchedule = new MysteryCellSchedule(board, randomNumberGenerator);

        // T-11: each game needs its own MysteryCellSchedule instance.
        GameEngine gameEngine = buildGameEngine(board, mysteryCellSchedule, randomNumberGenerator);

        new GameLoop(gameEngine, mysteryCellSchedule, dice, board, messagePublisher)
            .play(allPlayers, turnOrder);
    }

    // Message per player, naming its pieces.
    private static void announcePlayerRoster(List<Player> allPlayers, GameMessagePublisher messagePublisher) {
        for (Player player : allPlayers) {
            messagePublisher.publish(GameMessage.playerRosterAnnounced(player.getColor(), buildPieceLabels(player)));
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

    private static GameEngine buildGameEngine(
            LudoBoard board, MysteryCellLocation mysteryCellLocation,
            RandomNumberGenerator randomNumberGenerator) {

        // T-7: one tracker, shared by the engine (updates it) and the capture rule (reads it).
        HomeGateTracker homeGateTracker = new HomeGateTracker();

        BlockadeLimitRule blockadeLimitRule = new PassingBlockadeRule();
        HomeStraightEntryRule homeStraightEntryRule = buildHomeStraightEntryRule(homeGateTracker);
        ExactRollRule exactRollRule = new OvershootHomeRule(board);
        BlockStepsRule blockStepsRule = new DivideByBlockSizeRule();

        CoinToss coinToss = SeededCoinToss.getInstance();
        List<TurnRule> turnRules = List.of(
            new EnterBoardRule(new CoinTossEntryDirectionAssigner(coinToss)),
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
        // A forced blockade break never uses the open gate, so it gets an always-closed chain.
        HomeStraightEntryRule forcedBreakEntryRule = buildHomeStraightEntryRule(color -> false);
        BlockadeBreakRule blockadeBreakRule =
            new ThirdSixBlockadeBreakRule(forcedBreakEntryRule, blockadeLimitRule);

        // T-15: effects only activate after genuine Mystery Cell teleport.
        EffectActivationRule effectActivationRule = new MysteryTeleportActivationRule();

        // T-12: reuses T-1's same seeded coin toss for effects.
        AlphaEffectRule alphaEffectRule = new AlphaEffectRule(coinToss);

        // T-14: reverses direction, or forwards to Beta, on Gamma.
        MysteryCellDestination betaDestination = new BetaDestination(board, effectActivationRule);
        GammaDirectionRule gammaDirectionRule = new GammaDirectionRule(betaDestination);

        // T-11: the list order is the random draw order, so keep it stable for reproducible games.
        List<MysteryCellDestination> mysteryCellDestinations = List.of(
            new AlphaDestination(board, effectActivationRule, alphaEffectRule),
            betaDestination,
            new GammaDestination(board, effectActivationRule, gammaDirectionRule),
            new BaseDestination(),
            new EntryDestination(board, effectActivationRule),
            new ApproachDestination(board, effectActivationRule));

        // T-11: reuses the same seeded random source for reproducibility.
        MysteryCellTeleportRule mysteryCellTeleportRule = new MysteryCellTeleportRule(
            mysteryCellLocation, randomNumberGenerator, mysteryCellDestinations);

        // T-13: checks each roll for Beta's consecutive-3 return trigger.
        BetaRestrictionRule betaRestrictionRule = new BetaRestrictionRule();

        return new GameEngine(
            turnRules, strategyRegistry, rollValidityRule, captureCheckRule, blockadeBreakRule,
            mysteryCellTeleportRule, mysteryCellLocation, List.of(homeGateTracker, betaRestrictionRule)
        );
    }

    private static HomeStraightEntryRule buildHomeStraightEntryRule(HomeGateStatus homeGate) {
        HomeStraightEntryRule entryRule = new ApproachPassCountRule();
        entryRule.setNext(new HomeStraightEligibilityRule(homeGate));

        return entryRule;
    }
}
