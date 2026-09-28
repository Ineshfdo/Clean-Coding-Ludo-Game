package message.move;

import config.enums.PlayerColor;
import message.GameMessage;

/**
 A piece left Base and entered the track.
 The event also tells the new piece count of the player.
 @param playerColor the colour of the player
 @param pieceLabel the name of the piece
 @param newPosition the Entry cell where the piece landed
 @param piecesOnBoard the number of pieces of the player that are now on the board
 @param piecesAtBase the number of pieces of the player that are now at Base
 */
public record PieceEnteredBoard(
        PlayerColor playerColor, String pieceLabel, int newPosition, int piecesOnBoard,
        int piecesAtBase) implements GameMessage {
}
