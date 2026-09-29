package message;

// Something that happened in the game, for example a move, a capture or teleport.
// Game classes publish messages and observers decide how to show them.
// Every kind of message is an immutable record.

public interface GameMessage {}