package model.player;

import config.constant.BoardConstants;
import config.enums.PlayerColor;
import exception.IllegalMoveException;
import exception.PieceOwnershipException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.effect.movement.MovementEffect;
import model.effect.restriction.PieceRestrictionState;
import model.piece.Piece;

/**
 Base class of the four colour players: RedPlayer, YellowPlayer, GreenPlayer and BluePlayer.
 A player owns its four pieces, and every change of a piece goes through the player, which checks that the piece belongs to it.
 Every method that takes a piece throws {@link PieceOwnershipException} when the piece belongs to another player.
 */
public abstract class Player {

    private final PlayerColor color;
    private final List<Piece> pieces;

    /**
     Creates a player with four pieces at Base.
     @param color the colour of the player
     */
    protected Player(PlayerColor color) {
        this.color = color;
        this.pieces = buildPieces(color);
    }

    /**
     Gives the colour of the player.
     @return the colour
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     Gives the pieces of the player.
     @return the four pieces; the list cannot be changed
     */
    public List<Piece> getPieces() {
        return pieces;
    }

    /**
     Finds the pieces of this player that stand on one track cell (T-3).
     @param trackPosition the track position to look at
     @return the pieces on that cell; empty when there are none
     */
    public List<Piece> getPiecesAt(int trackPosition) {
        return pieces.stream()
                .filter(Piece::isOnTrack)
                .filter(piece -> piece.getTrackPosition() == trackPosition)
                .toList();
    }

    /**
     Counts the captures of all pieces together (T-7).
     @return the total number of captures
     */
    public int getCaptureCount() {
        return pieces.stream().mapToInt(Piece::getCaptureCount).sum();
    }

    /**
     Tells whether the player has finished.
     A player finishes when all four pieces are Home.
     @return true when every piece is Home
     */
    public boolean hasAllPiecesHome() {
        return pieces.stream().allMatch(Piece::isHome);
    }

    /**
     Counts the pieces that have left Base and are not yet Home.
     @return the number of pieces on the board
     */
    public int countPiecesOnBoard() {
        return (int) pieces.stream().filter(piece -> !piece.isAtBase() && !piece.isHome()).count();
    }

    /**
     Counts the pieces that are still at Base.
     @return the number of pieces at Base
     */
    public int countPiecesAtBase() {
        return (int) pieces.stream().filter(Piece::isAtBase).count();
    }

    /**
     Adds one capture to a piece.
     @param piece the piece that captured an opponent
     */
    public void recordCapture(Piece piece) {
        requireOwnership(piece);
        piece.recordCapture();
    }

    /**
     Puts a piece on the Entry cell of this player.
     @param piece the piece that leaves Base
     @param board the board that gives the Entry cell
     */
    public void leaveBase(Piece piece, Board board) {
        requireOwnership(piece);
        piece.leaveBase(board.getEntryCellPosition(color));
    }

    /**
     Sends a piece back to Base and resets its stored information (rule 7, T-9).
     @param piece the piece to send back
     */
    public void returnToBase(Piece piece) {
        requireOwnership(piece);
        piece.returnToBase();
    }

    /**
     Puts a piece directly on a track cell.
     This is a jump and not a move (T-11).
     @param piece the piece to place
     @param trackPosition the track position to jump to
     */
    public void teleportTo(Piece piece, int trackPosition) {
        requireOwnership(piece);
        piece.moveTo(trackPosition);
    }

    /**
     Counts an arrival on the Approach cell as a pass, in the same way as landing on it by a move (T-1, T-11).
     @param piece the piece that arrived
     */
    public void recordApproachPass(Piece piece) {
        requireOwnership(piece);
        piece.recordApproachPass();
    }

    /**
     Gives a piece its own Energized or Sick effect, after an Alpha teleport (T-12).
     @param piece the piece that gets the effect
     @param effect the effect to give
     */
    public void applyIndividualEffect(Piece piece, MovementEffect effect) {
        requireOwnership(piece);
        piece.applyIndividualEffect(effect);
    }

    /**
     Gives a piece the shared effect of its blockade, and records the size of the blockade (T-12).
     @param piece a piece of the blockade
     @param effect the effect of the blockade
     @param blockSize the number of pieces in the blockade
     */
    public void applyBlockEffect(Piece piece, MovementEffect effect, int blockSize) {
        requireOwnership(piece);
        piece.applyBlockEffect(effect, blockSize);
    }

    /**
     Uses up one round of the movement effects of every piece (T-12).
     */
    public void tickMovementEffects() {
        for (Piece piece : pieces) {
            piece.tickIndividualEffect();
            piece.tickBlockEffect();
        }
    }

    /**
     Puts a piece into a restriction, for example Beta (T-13).
     @param piece the piece to restrict
     @param restrictionState the restriction to apply
     */
    public void applyRestriction(Piece piece, PieceRestrictionState restrictionState) {
        requireOwnership(piece);
        piece.applyRestriction(restrictionState);
    }

    /**
     Uses up one round of the restriction of every piece (T-13).
     */
    public void tickRestrictions() {
        for (Piece piece : pieces) {
            piece.tickRestriction();
        }
    }

    /**
     Records the roll for every restricted piece (T-13).
     @param rollValue the value of the first roll of the turn
     */
    public void recordRestrictionRoll(int rollValue) {
        for (Piece piece : pieces) {
            if (piece.getRestrictionState().forbidsMovement()) {
                piece.recordRestrictionRoll(rollValue);
            }
        }
    }

    /**
     Finds the pieces whose Beta restriction has triggered a return to Base (T-13).
     @return the triggered pieces; empty when there are none
     */
    public List<Piece> findPiecesTriggeredForReturnToBase() {
        List<Piece> triggeredPieces = new ArrayList<>();

        for (Piece piece : pieces) {
            if (piece.getRestrictionState().hasTriggeredReturnToBase()) {
                triggeredPieces.add(piece);
            }
        }

        return triggeredPieces;
    }

    /**
     Gives a piece that has just left Base its direction (T-1).
     @param piece the piece that left Base
     @param movementDirection the direction given by the coin toss
     */
    public void assignMovementDirection(Piece piece, MovementDirectionStrategy movementDirection) {
        requireOwnership(piece);
        piece.assignMovementDirection(movementDirection);
    }

    /**
     Makes a piece travel in the direction of its blockade, and records the size of the blockade (T-4, T-5).
     @param piece a piece of the blockade
     @param blockDirection the direction of the blockade
     @param blockSize the number of pieces in the blockade
     */
    public void adoptBlockDirection(Piece piece, MovementDirectionStrategy blockDirection, int blockSize) {
        requireOwnership(piece);
        piece.adoptBlockDirection(blockDirection, blockSize);
    }

    /**
     Gives a piece that leaves its blockade its original direction back (T-5).
     @param piece the piece that left the blockade
     */
    public void restoreOriginalDirection(Piece piece) {
        requireOwnership(piece);
        piece.restoreOriginalDirection();
    }

    /**
     Reverses the direction of a piece for good, after a Gamma teleport (T-14).
     @param piece the piece to reverse
     */
    public void reverseDirection(Piece piece) {
        requireOwnership(piece);
        piece.reverseDirection();
    }

    /**
     Moves a piece along the track or the HomeStraight by the given steps, in the given direction (rule 1, T-1).
     @param piece the piece to move
     @param steps the number of steps to move
     @param board the board that gives the track
     @param homeEntryPolicy decides whether the piece may enter its HomeStraight
     @param travelDirection the direction in which the piece travels
     @throws IllegalMoveException if the piece is at Base or already Home
     */
    public void moveForward(
            Piece piece, int steps, Board board, HomeEntryPolicy homeEntryPolicy,
            MovementDirectionStrategy travelDirection) {
        requireOwnership(piece);
        requireMovable(piece);

        PieceMovement.move(piece, steps, board, homeEntryPolicy, travelDirection);
    }

    private void requireMovable(Piece piece) {
        if (!piece.isOnTrack() && !piece.isOnHomeStraight()) {
            throw new IllegalMoveException(
                    piece + " can't move: it is at Base or already Home");
        }
    }

    private void requireOwnership(Piece piece) {
        if (piece.getColor() != color) {
            throw new PieceOwnershipException(piece + " does not belong to " + color);
        }
    }

    private static List<Piece> buildPieces(PlayerColor color) {
        List<Piece> newPieces = new ArrayList<>(BoardConstants.PIECES_PER_PLAYER);

        for (int pieceNumber = 1; pieceNumber <= BoardConstants.PIECES_PER_PLAYER; pieceNumber++) {
            newPieces.add(new Piece(color, pieceNumber));
        }

        return Collections.unmodifiableList(newPieces);
    }
}
