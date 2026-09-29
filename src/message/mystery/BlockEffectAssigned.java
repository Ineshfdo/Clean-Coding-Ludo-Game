package message.mystery;

import message.GameMessage;

/**
A coin toss gave a teleported blockade a shared Energized or Sick effect (T-12).
@param blockLabel the joined names of the pieces of the blockade
@param effectLabel the effect: Energized or Sick
*/
public record BlockEffectAssigned(String blockLabel, String effectLabel) implements GameMessage {}