package model.player.rule.mystery;

import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

// T-11: decides whether a piece that just landed is teleported.
public interface TeleportRule {

    Optional<Command> findTeleport(Player mover, Piece landedPiece);
}
