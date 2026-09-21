package model.round;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.player.Player;

// Reacts to the start and end of each game round.
public interface RoundListener {

    void onRoundStarted(int roundNumber, List<Player> allPlayers, GameMessagePublisher messagePublisher);

    void onRoundCompleted(int roundNumber, List<Player> allPlayers);
}
