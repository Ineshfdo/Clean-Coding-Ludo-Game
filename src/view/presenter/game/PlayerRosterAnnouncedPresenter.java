package view.presenter.game;

import config.constant.BoardConstants;
import java.util.List;
import message.game.PlayerRosterAnnounced;
import view.presenter.EventPresenter;

/**
 Presents {@link PlayerRosterAnnounced}: it announces the pieces of a player before the game begins.
 */
public final class PlayerRosterAnnouncedPresenter extends EventPresenter<PlayerRosterAnnounced> {

    /**
     Creates the presenter for {@link PlayerRosterAnnounced}.
     */
    public PlayerRosterAnnouncedPresenter() {
        super(PlayerRosterAnnounced.class);
    }

    // 3.1: "The red player has four (04) pieces named R1, R2, R3, and R4."
    @Override
    public String present(PlayerRosterAnnounced message) {
        List<String> pieceLabels = message.pieceLabels();

        return "The " + message.playerColor().name().toLowerCase() + " player has "
                + describePieceCountInWords(pieceLabels.size()) + " ("
                + String.format("%02d", pieceLabels.size()) + ") pieces named "
                + joinWithAnd(pieceLabels) + ".";
    }

    private static String describePieceCountInWords(int pieceCount) {
        return switch (pieceCount) {
            case BoardConstants.PIECES_PER_PLAYER -> "four";
            default -> String.valueOf(pieceCount);
        };
    }

    // "R1, R2, R3, and R4": comma-separated, with "and" before the last.
    private static String joinWithAnd(List<String> labels) {
        if (labels.size() == 1) {
            return labels.get(0);
        }

        String allButLast = String.join(", ", labels.subList(0, labels.size() - 1));

        return allButLast + ", and " + labels.get(labels.size() - 1);
    }
}
