package model.player.rule.roll;

import message.observer.GameMessagePublisher;

/**
 Reacts to an accepted roll before the player chooses a move, for example the home gate and the Beta restriction.
 */
public interface RollHook {

    /**
     Called after every roll that is not void.
     @param roll what happened: the player, the roll number and the roll value
     @param messagePublisher where events are announced
     */
    void onRollAccepted(RollEvent roll, GameMessagePublisher messagePublisher);
}
