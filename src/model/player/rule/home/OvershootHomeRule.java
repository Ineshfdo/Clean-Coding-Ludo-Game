package model.player.rule.home;

import model.board.Board;
import model.piece.Piece;

// Rule 10: a HomeStraight move must land exactly on Home.
public final class OvershootHomeRule extends ExactRollRule {

    private final Board board;

    public OvershootHomeRule(Board board) {
        this.board = board;
    }

    @Override
    protected boolean appliesTo(Piece piece, int steps) {
        int newIndex = piece.getHomeStraightIndex() + steps;

        return newIndex > board.getHomeStraightLength();
    }
}
