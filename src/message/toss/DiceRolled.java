package message.toss;

import config.enums.PlayerColor;
import message.GameMessage;

/**
A player rolled the die during the first-player toss.
@param playerColor the colour of the player
@param rollValue the value of the roll
*/
public record DiceRolled(PlayerColor playerColor, int rollValue) implements GameMessage {}
