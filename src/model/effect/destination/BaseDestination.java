package model.effect.destination;

import config.constant.BoardConstants;
import java.util.List;
import message.mystery.PieceTeleported;
import message.observer.GameMessagePublisher;
import model.effect.mysterycell.MysteryCellDestination;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.Player;

// T-11: the pieces go back to Base, so there is no track cell and no effect.
public final class BaseDestination implements MysteryCellDestination {

    private static final String LABEL = "Base";

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public void receive(Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        for (Piece piece : teleportedPieces) {
            player.returnToBase(piece);
        }

        messagePublisher.publish(new PieceTeleported(
                PieceLabels.joinPieceLabels(teleportedPieces), getLabel(), BoardConstants.NO_TRACK_POSITION));
    }
}
