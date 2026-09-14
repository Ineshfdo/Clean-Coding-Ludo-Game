package command;

import gamemessage.GameMessagePublisher;

// Wraps one legal action as an object, so an invoker (today the rule
// chain in GameFacade, later a rule-based AI choosing between
// several possible actions) can hold and run it without knowing how
// the action itself works or what it announces once it happens.
public interface Command {

    void execute(GameMessagePublisher messages);
}
