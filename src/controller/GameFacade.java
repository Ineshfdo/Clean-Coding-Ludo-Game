package controller;

import config.enums.GameMessageType;
import config.enums.PlayerColor;
import exception.IllegalMoveException;
import exception.InvalidPieceStateException;
import exception.PieceOwnershipException;
import exception.PlayerNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.GameMessage;
import message.observer.GameMessageCenter;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.board.LudoBoard;
import model.effect.activation.EffectActivationRule;
import model.effect.activation.MysteryTeleportActivationRule;
import model.effect.mysterycell.MysteryCellSchedule;
import model.effect.rule.AlphaEffectRule;
import model.effect.rule.GammaDirectionRule;
import model.effect.rule.MysteryCellEffectRules;
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
import utils.randomgenerator.SeededRandomNumberGenerator;
import view.ConsoleGameObserver;

public final class GameFacade {

    // T-4: shared so movement and display agree on block direction.
    private static final BlockTravelDirectionStrategy BLOCK_TRAVEL_DIRECTION_STRATEGY =
        new LongestDistanceDirectionStrategy();

    private static final Logger LOGGER = Logger.getLogger(GameFacade.class.getName());

    private static final int NO_ROLL_YET = 0;

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
        Board board = LudoBoard.getInstance();

        GameMessageCenter.getInstance().clearObservers();
        GameMessagePublisher messagePublisher = GameMessageCenter.getInstance();
        ConsoleGameObserver consoleObserver =
            new ConsoleGameObserver(allPlayers, board, BLOCK_TRAVEL_DIRECTION_STRATEGY);
        messagePublisher.addObserver(consoleObserver);

        SeededRandomNumberGenerator.getInstance().setSeed(seed);

        Dice dice = SixSidedDice.getInstance();

        // 3.1: announce every player's own pieces before starting.
        announcePlayerRoster(allPlayers, messagePublisher);

        messagePublisher.publish(GameMessage.of(GameMessageType.GAME_STARTING));

        // The toss order is also clockwise, starting from Red.
        List<Player> tossOrder = buildTurnOrder(PlayerColor.RED, allPlayers, board);
        Player firstPlayer = determineFirstPlayer(tossOrder, dice, messagePublisher);
        List<Player> turnOrder = buildTurnOrder(firstPlayer.getColor(), allPlayers, board);

        // Round reports list players in play order, starting from the toss winner.
        consoleObserver.setTurnOrder(turnOrder);

        // T-10: reuses the same seeded random source for reproducibility.
        MysteryCellSchedule mysteryCellSchedule =
            new MysteryCellSchedule(board, SeededRandomNumberGenerator.getInstance());

        // T-11: each game needs its own MysteryCellSchedule instance.
        GameEngine gameEngine = buildGameEngine(mysteryCellSchedule);

        // GAME_OVER: the game ends only once every player has all pieces Home.
        List<PlayerColor> finalStandings = new ArrayList<>();
        RoundTracker roundTracker = new RoundTracker(turnOrder);

        while (!allPlayersFinished(allPlayers)) {
            int roundNumber = roundTracker.startNextRound();
            messagePublisher.publish(GameMessage.roundStarted(roundNumber));
            mysteryCellSchedule.onRoundStarted(roundNumber, allPlayers, messagePublisher);

            // T-12/T-13: expire this round's effects and Beta restriction first.
            for (Player player : roundTracker.getTurnOrder()) {
                player.tickMovementEffects();
                player.tickRestrictions();
            }

            for (Player player : roundTracker.getTurnOrder()) {
                // Finished players take no turn: no roll, no message.
                if (player.hasAllPiecesHome()) {
                    continue;
                }

                gameEngine.playTurn(player, allPlayers, dice, board, messagePublisher);
                recordFinisherIfNewlyDone(player, finalStandings);
            }

            mysteryCellSchedule.onRoundCompleted(roundNumber, allPlayers);
            messagePublisher.publish(GameMessage.boardStateReported(roundNumber));
        }

        messagePublisher.publish(GameMessage.gameOver(finalStandings));
    }

    private static boolean allPlayersFinished(List<Player> allPlayers) {
        return allPlayers.stream().allMatch(Player::hasAllPiecesHome);
    }

    // GAME_OVER: record a player the moment its 4th piece reaches Home.
    private static void recordFinisherIfNewlyDone(Player player, List<PlayerColor> finalStandings) {
        if (player.hasAllPiecesHome() && !finalStandings.contains(player.getColor())) {
            finalStandings.add(player.getColor());
        }
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

    // Rerolls everyone when two or more players tie highest.
    private static Player determineFirstPlayer(
            List<Player> allPlayers, Dice dice, GameMessagePublisher messagePublisher) {
        messagePublisher.publish(GameMessage.of(GameMessageType.TOSS_STARTING));

        while (true) {
            Player highestRoller = allPlayers.get(0);
            int highestRoll = NO_ROLL_YET;
            int highestRollerCount = 0;

            for (Player player : allPlayers) {
                int rollValue = dice.roll();
                messagePublisher.publish(GameMessage.diceRolled(player.getColor(), rollValue));

                if (rollValue > highestRoll) {
                    highestRoll = rollValue;
                    highestRoller = player;
                    highestRollerCount = 1;
                } else if (rollValue == highestRoll) {
                    highestRollerCount++;
                }
            }

            if (highestRollerCount == 1) {
                messagePublisher.publish(GameMessage.tossWon(highestRoller.getColor(), highestRoll));
                return highestRoller;
            }

            messagePublisher.publish(GameMessage.tossTied(highestRoll));
        }
    }

    // Rule 3: turn order runs clockwise from a given color.
    private static List<Player> buildTurnOrder(
            PlayerColor startingColor, List<Player> allPlayers, Board board) {
        List<Player> turnOrder = new ArrayList<>();
        PlayerColor color = startingColor;

        for (int position = 0; position < allPlayers.size(); position++) {
            turnOrder.add(findPlayerByColor(allPlayers, color));
            color = board.getNextColorClockwise(color);
        }

        return turnOrder;
    }

    private static Player findPlayerByColor(List<Player> allPlayers, PlayerColor color) {
        return allPlayers.stream()
            .filter(player -> player.getColor() == color)
            .findFirst()
            .orElseThrow(() -> new PlayerNotFoundException("No player with color " + color));
    }

    private static GameEngine buildGameEngine(MysteryCellSchedule mysteryCellSchedule) {
        Board board = LudoBoard.getInstance();

        // T-7: one tracker, shared by the engine (updates it) and the capture rule (reads it).
        HomeGateTracker homeGateTracker = new HomeGateTracker();

        BlockadeLimitRule blockadeLimitRule = new PassingBlockadeRule();
        HomeStraightEntryRule homeStraightEntryRule = buildHomeStraightEntryRule(homeGateTracker);
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
        // A forced blockade break never uses the open gate, so it gets an always-closed chain.
        HomeStraightEntryRule forcedBreakEntryRule = buildHomeStraightEntryRule(color -> false);
        BlockadeBreakRule blockadeBreakRule =
            new ThirdSixBlockadeBreakRule(forcedBreakEntryRule, blockadeLimitRule);

        // T-12: reuses T-1's same seeded coin toss for effects.
        AlphaEffectRule alphaEffectRule = new AlphaEffectRule(SeededCoinToss.getInstance());

        // T-14: reverses direction, or forwards to Beta, on Gamma.
        GammaDirectionRule gammaDirectionRule = new GammaDirectionRule(board);

        // T-15: effects only activate after genuine Mystery Cell teleport.
        EffectActivationRule effectActivationRule = new MysteryTeleportActivationRule();
        MysteryCellEffectRules mysteryCellEffectRules =
            new MysteryCellEffectRules(alphaEffectRule, gammaDirectionRule, effectActivationRule);

        // T-11: reuses the same seeded random source for reproducibility.
        MysteryCellTeleportRule mysteryCellTeleportRule = new MysteryCellTeleportRule(
            mysteryCellSchedule, SeededRandomNumberGenerator.getInstance(), board, mysteryCellEffectRules);

        // T-13: checks each roll for Beta's consecutive-3 return trigger.
        BetaRestrictionRule betaRestrictionRule = new BetaRestrictionRule();

        return new GameEngine(
            turnRules, strategyRegistry, rollValidityRule, captureCheckRule, blockadeBreakRule,
            mysteryCellTeleportRule, betaRestrictionRule, homeGateTracker
        );
    }

    private static HomeStraightEntryRule buildHomeStraightEntryRule(HomeGateStatus homeGate) {
        HomeStraightEntryRule entryRule = new ApproachPassCountRule();
        entryRule.setNext(new HomeStraightEligibilityRule(homeGate));

        return entryRule;
    }
}
