package model.player.rule.roll;

import message.observer.GameMessagePublisher;

// Reacts to an accepted roll before the player picks a move (e.g. home gate, Beta restriction).
public interface RollHook {

    void onRollAccepted(RollEvent roll, GameMessagePublisher messagePublisher);
}
