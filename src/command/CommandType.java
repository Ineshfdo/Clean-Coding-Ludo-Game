package command;

// Lets a PlayerStrategy tell legal Commands apart without needing
// instanceof against concrete command classes.
public enum CommandType {
    ENTER_BOARD,
    MOVE_FORWARD
}
