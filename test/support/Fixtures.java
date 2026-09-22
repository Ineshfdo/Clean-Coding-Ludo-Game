package support;

import config.enums.PlayerColor;
import model.direction.ClockwiseMovementStrategy;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.BluePlayer;
import model.player.GreenPlayer;
import model.player.Player;
import model.player.RedPlayer;
import model.player.YellowPlayer;

// Builds pieces and players already in the state a test needs, so tests do not repeat set-up code.
public final class Fixtures {

    private Fixtures() {
    }

    public static Player playerOf(PlayerColor color) {
        return switch (color) {
            case RED -> new RedPlayer();
            case YELLOW -> new YellowPlayer();
            case GREEN -> new GreenPlayer();
            case BLUE -> new BluePlayer();
        };
    }

    public static Piece pieceOnTrack(
            PlayerColor color, int number, int position, MovementDirectionStrategy direction) {
        Piece piece = new Piece(color, number);

        piece.leaveBase(position);
        piece.assignMovementDirection(direction);

        return piece;
    }

    public static Piece clockwisePieceOnTrack(PlayerColor color, int number, int position) {
        return pieceOnTrack(color, number, position, ClockwiseMovementStrategy.getInstance());
    }

    // Puts one of the player's own pieces on the track, heading in the given direction.
    public static Piece placeOnTrack(
            Player player, int pieceIndex, int position, MovementDirectionStrategy direction) {
        Piece piece = player.getPieces().get(pieceIndex);

        piece.leaveBase(position);
        piece.assignMovementDirection(direction);

        return piece;
    }

    public static Piece placeOnTrackClockwise(Player player, int pieceIndex, int position) {
        return placeOnTrack(player, pieceIndex, position, ClockwiseMovementStrategy.getInstance());
    }

    public static Piece placeOnHomeStraight(
            Player player, int pieceIndex, int homeStraightIndex, MovementDirectionStrategy direction) {
        Piece piece = placeOnTrack(player, pieceIndex, 0, direction);

        piece.moveToHomeStraight(homeStraightIndex);

        return piece;
    }

    public static Piece placeHome(Player player, int pieceIndex) {
        Piece piece = placeOnTrack(player, pieceIndex, 0, ClockwiseMovementStrategy.getInstance());

        piece.moveHome();

        return piece;
    }

    public static void sendEveryPieceHome(Player player) {
        for (int index = 0; index < player.getPieces().size(); index++) {
            placeHome(player, index);
        }
    }
}
