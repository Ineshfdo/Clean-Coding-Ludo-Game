package model.round;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.player.Player;

/**
 Reacts to the start and the end of each round.
 */
public interface RoundListener {

    /**
     Called at the start of a round, before any turn is played.
     @param roundNumber the number of the round, counted from 1
     @param allPlayers all players of the game
     @param messagePublisher where events are announced
     */
    void onRoundStarted(int roundNumber, List<Player> allPlayers, GameMessagePublisher messagePublisher);

    /**
     Called at the end of a round, after every turn has been played.
     @param roundNumber the number of the round, counted from 1
     @param allPlayers all players of the game
     */
    void onRoundCompleted(int roundNumber, List<Player> allPlayers);
}
