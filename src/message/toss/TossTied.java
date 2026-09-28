package message.toss;

import message.GameMessage;

/**
 Two or more players tied for the highest roll, so everyone rolls again.
 @param rollValue the value that was tied
 */
public record TossTied(int rollValue) implements GameMessage {
}
