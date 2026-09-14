package command;

import gamemessage.GameMessagePublisher;

// Wraps one legal action as an object, so an invoker (a
// PlayerStrategy choosing among several legal options, or GameFacade
// when only one option exists) can hold and run it without knowing
// how the action itself works or what it announces once it happens.
public interface Command {

    void execute(GameMessagePublisher messages);

    CommandType getType();
}
