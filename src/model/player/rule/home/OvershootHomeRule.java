package model.player.rule.home;

import model.board.Board;
import model.piece.Piece;

/**
 A move on the HomeStraight must not go past Home (rule 10).
 The piece needs the exact roll.
 */
public final class OvershootHomeRule extends ExactRollRule {

    private final Board board;

    /**
     Creates the rule.
     @param board gives the length of the HomeStraight
     */
    public OvershootHomeRule(Board board) {
        this.board = board;
    }

    @Override
    protected boolean appliesTo(Piece piece, int steps) {
        int newIndex = piece.getHomeStraightIndex() + steps;

        return newIndex > board.getHomeStraightLength();
    }
}
