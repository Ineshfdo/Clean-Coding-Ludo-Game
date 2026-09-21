package model.direction;

// Decides which way a piece travels when it leaves Base.
public interface EntryDirectionAssigner {

    EntryDirection assign();
}
