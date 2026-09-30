package view.presenter.game;

import java.util.List;

import message.game.BoardStateReported;
import model.player.Player;
import utils.color.PlayerColorNames;
import view.presenter.EventPresenter;
import view.presenter.PieceCountLine;

/**
 Presents {@link BoardStateReported}: it writes the report at the end of a round: the piece count of every player, on the board and at the base.
 */
public final class BoardStateReportedPresenter extends EventPresenter<BoardStateReported> {

    // Reports list players in this order; the toss winner's turn order replaces it.
    private List<Player> playersInTurnOrder;

    /**
     Creates the presenter.
     @param allPlayers all players of the game; the report lists them in this order until a turn order is set
     */
    public BoardStateReportedPresenter(List<Player> allPlayers) {
        super(BoardStateReported.class);
        this.playersInTurnOrder = allPlayers;
    }

    /**
     Sets the order in which the report lists the players.
     @param turnOrder the players in play order, starting with the winner of the toss
     */
    public void setTurnOrder(List<Player> turnOrder) {
        this.playersInTurnOrder = turnOrder;
    }

    @Override
    public String present(BoardStateReported message) {
        return describeRoundStatusSummary();
    }

    // Requirement 5: a per-player board/base tally, printed at the end of every round.
    private String describeRoundStatusSummary() {
        StringBuilder summary = new StringBuilder("\n-------------------------------\n");

        for (Player player : playersInTurnOrder) {
            summary.append(PieceCountLine.describe(
                            PlayerColorNames.displayNameOf(player.getColor()),
                            player.countPiecesOnBoard(), player.countPiecesAtBase()))
                    .append('\n');
        }

        summary.append("-------------------------------\n");

        return summary.toString();
    }
}
