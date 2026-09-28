package controller;

import config.enums.PlayerColor;
import java.util.ArrayList;
import java.util.List;
import message.game.BoardStateReported;
import message.game.GameOver;
import message.game.RoundStarted;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.player.Player;
import model.round.RoundListener;
import utils.dice.Dice;

/**
 Plays rounds until every player has all pieces Home, and then publishes the final standings.
 In each round the effects expire, every player who has not finished takes a turn in turn order, and the board state is reported.
 */
public final class GameLoop {

    private final GameEngine gameEngine;
    private final RoundListener roundListener;
    private final Dice dice;
    private final Board board;
    private final GameMessagePublisher messagePublisher;

    /**
     Creates the loop for one game.
     @param gameEngine plays the turn of one player
     @param roundListener told when each round starts and ends
     @param dice the dice used for the rolls
     @param board the board the pieces move on
     @param messagePublisher where the events of the game are announced
     */
    public GameLoop(
            GameEngine gameEngine, RoundListener roundListener, Dice dice, Board board,
            GameMessagePublisher messagePublisher) {
        this.gameEngine = gameEngine;
        this.roundListener = roundListener;
        this.dice = dice;
        this.board = board;
        this.messagePublisher = messagePublisher;
    }

    /**
     Plays the whole game and publishes the final standings when every player has finished.
     @param allPlayers all players of the game
     @param turnOrder the players in the order in which they take their turns
     */
    public void play(List<Player> allPlayers, List<Player> turnOrder) {
        // GAME_OVER: the game ends only once every player has all pieces Home.
        List<PlayerColor> finalStandings = new ArrayList<>();
        RoundTracker roundTracker = new RoundTracker(turnOrder);

        while (!allPlayersFinished(allPlayers)) {
            int roundNumber = roundTracker.startNextRound();

            startRound(roundNumber, roundTracker.getTurnOrder(), allPlayers);
            playRound(roundTracker.getTurnOrder(), allPlayers, finalStandings);
            finishRound(roundNumber, allPlayers);
        }

        messagePublisher.publish(new GameOver(finalStandings));
    }

    private void startRound(int roundNumber, List<Player> turnOrder, List<Player> allPlayers) {
        messagePublisher.publish(new RoundStarted(roundNumber));
        roundListener.onRoundStarted(roundNumber, allPlayers, messagePublisher);

        // T-12/T-13: expire this round's effects and Beta restriction first.
        for (Player player : turnOrder) {
            player.tickMovementEffects();
            player.tickRestrictions();
        }
    }

    private void playRound(
            List<Player> turnOrder, List<Player> allPlayers, List<PlayerColor> finalStandings) {
        for (Player player : turnOrder) {
            // Finished players take no turn: no roll, no message.
            if (player.hasAllPiecesHome()) {
                continue;
            }

            gameEngine.playTurn(player, allPlayers, dice, board, messagePublisher);
            recordFinisherIfNewlyDone(player, finalStandings);
        }
    }

    private void finishRound(int roundNumber, List<Player> allPlayers) {
        roundListener.onRoundCompleted(roundNumber, allPlayers);
        messagePublisher.publish(new BoardStateReported(roundNumber));
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
}
