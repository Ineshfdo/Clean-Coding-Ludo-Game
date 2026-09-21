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

// Owns a player's four pieces; every piece change goes through here.
public abstract class Player {

    private final PlayerColor color;
    private final List<Piece> pieces;

    protected Player(PlayerColor color) {
        this.color = color;
        this.pieces = buildPieces(color);
    }

    public PlayerColor getColor() {
        return color;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    // T-3: this player's pieces standing on one track cell.
    public List<Piece> getPiecesAt(int trackPosition) {
        return pieces.stream()
                .filter(Piece::isOnTrack)
                .filter(piece -> piece.getTrackPosition() == trackPosition)
                .toList();
    }

    // T-7: total captures across all pieces.
    public int getCaptureCount() {
        return pieces.stream().mapToInt(Piece::getCaptureCount).sum();
    }

    // GAME_OVER: a player wins once all 4 pieces reach Home.
    public boolean hasAllPiecesHome() {
        return pieces.stream().allMatch(Piece::isHome);
    }

    // On the board means past Base and not yet Home.
    public int countPiecesOnBoard() {
        return (int) pieces.stream().filter(piece -> !piece.isAtBase() && !piece.isHome()).count();
    }

    public int countPiecesAtBase() {
        return (int) pieces.stream().filter(Piece::isAtBase).count();
    }

    public void recordCapture(Piece piece) {
        requireOwnership(piece);
        piece.recordCapture();
    }

    public void leaveBase(Piece piece, Board board) {
        requireOwnership(piece);
        piece.leaveBase(board.getEntryCellPosition(color));
    }

    // Rule 7/T-9: sends a piece to Base with its stored info reset.
    public void returnToBase(Piece piece) {
        requireOwnership(piece);
        piece.returnToBase();
    }

    // T-11: places a piece directly on a track cell (a jump, not a move).
    public void teleportTo(Piece piece, int trackPosition) {
        requireOwnership(piece);
        piece.moveTo(trackPosition);
    }

    // T-1/T-11: teleporting onto Approach counts as arriving there, like landing on it.
    public void recordApproachPass(Piece piece) {
        requireOwnership(piece);
        piece.recordApproachPass();
    }

    // T-12: assigns a piece's own Energized/Sick effect (Alpha teleport).
    public void applyIndividualEffect(Piece piece, MovementEffect effect) {
        requireOwnership(piece);
        piece.applyIndividualEffect(effect);
    }

    // T-12: assigns the block's shared effect, recorded against its size.
    public void applyBlockEffect(Piece piece, MovementEffect effect, int blockSize) {
        requireOwnership(piece);
        piece.applyBlockEffect(effect, blockSize);
    }

    // T-12: expires every piece's movement effects by one round.
    public void tickMovementEffects() {
        for (Piece piece : pieces) {
            piece.tickIndividualEffect();
            piece.tickBlockEffect();
        }
    }

    // T-13: puts a piece into the Beta-restricted state.
    public void applyRestriction(Piece piece, PieceRestrictionState restrictionState) {
        requireOwnership(piece);
        piece.applyRestriction(restrictionState);
    }

    // T-13: expires one round of every piece's restriction.
    public void tickRestrictions() {
        for (Piece piece : pieces) {
            piece.tickRestriction();
        }
    }

    // T-13: records this round's roll for every restricted piece.
    public void recordRestrictionRoll(int rollValue) {
        for (Piece piece : pieces) {
            if (piece.getRestrictionState().forbidsMovement()) {
                piece.recordRestrictionRoll(rollValue);
            }
        }
    }

    // T-13: pieces whose Beta restriction has triggered a return to Base.
    public List<Piece> findPiecesTriggeredForReturnToBase() {
        List<Piece> triggeredPieces = new ArrayList<>();

        for (Piece piece : pieces) {
            if (piece.getRestrictionState().hasTriggeredReturnToBase()) {
                triggeredPieces.add(piece);
            }
        }

        return triggeredPieces;
    }

    // T-1: gives a piece that just left Base its coin-toss direction.
    public void assignMovementDirection(Piece piece, MovementDirectionStrategy movementDirection) {
        requireOwnership(piece);
        piece.assignMovementDirection(movementDirection);
    }

    // T-4/T-5: block members share one direction and record the block size.
    public void adoptBlockDirection(Piece piece, MovementDirectionStrategy blockDirection, int blockSize) {
        requireOwnership(piece);
        piece.adoptBlockDirection(blockDirection, blockSize);
    }

    // T-5: a piece leaving its block resumes its original direction.
    public void restoreOriginalDirection(Piece piece) {
        requireOwnership(piece);
        piece.restoreOriginalDirection();
    }

    // T-14: a Gamma teleport permanently reverses a piece's direction.
    public void reverseDirection(Piece piece) {
        requireOwnership(piece);
        piece.reverseDirection();
    }

    // Rule 1: moves a piece by the dice value using the travel direction (T-1).
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
