package model.player.rule.mystery;

import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

/**
 Decides whether a piece that has just landed is teleported (T-11).
 */
public interface TeleportRule {

    /**
     Checks a piece that has just landed.
     @param mover the player who moved
     @param landedPiece the piece that landed
     @return the teleport command, or an empty result when the piece did not land on the active Mystery Cell
     */
    Optional<Command> findTeleport(Player mover, Piece landedPiece);
}
