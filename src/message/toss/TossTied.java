package message.toss;

import message.GameMessage;

// Two or more players tied for the highest roll, so everyone rolls again.
public record TossTied(int rollValue) implements GameMessage {
}
