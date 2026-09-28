package message.turn;

import message.GameMessage;

/**
 The roll gave the player nothing to move.
 */
public record NoPieceMovable() implements GameMessage {
}
