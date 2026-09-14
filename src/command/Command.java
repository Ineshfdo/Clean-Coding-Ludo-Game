package command;

import gamemessage.GameMessagePublisher;

// Wraps one action as an object, so callers run it
// without knowing how it works.
public interface Command {

    void execute(GameMessagePublisher messages);

    CommandType getType();
}
