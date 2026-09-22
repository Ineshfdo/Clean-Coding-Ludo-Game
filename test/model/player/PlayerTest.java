package model.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.MovementEffectType;
import config.enums.PlayerColor;
import exception.IllegalMoveException;
import exception.PieceOwnershipException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.effect.movement.MovementEffect;
import model.effect.restriction.BetaRestrictedState;
import model.piece.Piece;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import support.Fixtures;

@DisplayName("Player")
class PlayerTest {

    private static final Board BOARD = LudoBoard.getInstance();
    private static final HomeEntryPolicy ENTRY_ALLOWED = piece -> false;

    private final Player red = Fixtures.playerOf(PlayerColor.RED);

    @Nested
    @DisplayName("a new player")
    class NewPlayer {

        @Test
        void hasItsColor() {
            assertEquals(PlayerColor.RED, red.getColor());
        }

        @Test
        void ownsFourPiecesNumberedOneToFour() {
            List<Piece> pieces = red.getPieces();

            assertEquals(4, pieces.size());
            assertEquals("R1", pieces.get(0).toString());
            assertEquals("R4", pieces.get(3).toString());
        }

        @Test
        void ownsPiecesOfItsOwnColorOnly() {
            for (Piece piece : red.getPieces()) {
                assertEquals(PlayerColor.RED, piece.getColor());
            }
        }

        @Test
        void startsWithEveryPieceAtBase() {
            assertEquals(4, red.countPiecesAtBase());
            assertEquals(0, red.countPiecesOnBoard());
        }

        @Test
        void hasNotFinished() {
            assertFalse(red.hasAllPiecesHome());
        }

        @Test
        void hasNoCaptures() {
            assertEquals(0, red.getCaptureCount());
        }

        @Test
        void pieceListCannotBeChangedFromOutside() {
            List<Piece> pieces = red.getPieces();
            Piece extra = new Piece(PlayerColor.RED, 5);

            assertThrows(UnsupportedOperationException.class, () -> pieces.add(extra));
        }
    }

    @Nested
    @DisplayName("counting pieces")
    class Counting {

        @Test
        void aPieceOnTheTrackCountsAsOnBoard() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);

            assertEquals(1, red.countPiecesOnBoard());
            assertEquals(3, red.countPiecesAtBase());
        }

        @Test
        void aPieceOnTheHomeStraightCountsAsOnBoard() {
            Fixtures.placeOnHomeStraight(red, 0, 2, ClockwiseMovementStrategy.getInstance());

            assertEquals(1, red.countPiecesOnBoard());
        }

        @Test
        void aPieceThatIsHomeIsNeitherOnBoardNorAtBase() {
            Fixtures.placeHome(red, 0);

            assertEquals(0, red.countPiecesOnBoard());
            assertEquals(3, red.countPiecesAtBase());
        }

        @Test
        void getPiecesAtReturnsOnlyPiecesOnThatTrackCell() {
            Piece first = Fixtures.placeOnTrackClockwise(red, 0, 10);
            Piece second = Fixtures.placeOnTrackClockwise(red, 1, 10);
            Fixtures.placeOnTrackClockwise(red, 2, 11);

            assertEquals(List.of(first, second), red.getPiecesAt(10));
        }

        @Test
        void getPiecesAtIgnoresPiecesNotOnTheTrack() {
            assertTrue(red.getPiecesAt(0).isEmpty());
        }

        @Test
        void allPiecesHomeOnlyWhenEveryPieceIsHome() {
            Fixtures.sendEveryPieceHome(red);

            assertTrue(red.hasAllPiecesHome());
        }

        @Test
        void oneMissingPieceMeansNotFinished() {
            Fixtures.placeHome(red, 0);
            Fixtures.placeHome(red, 1);
            Fixtures.placeHome(red, 2);

            assertFalse(red.hasAllPiecesHome());
        }

        @Test
        void captureCountAddsUpAllPieces() {
            red.recordCapture(red.getPieces().get(0));
            red.recordCapture(red.getPieces().get(0));
            red.recordCapture(red.getPieces().get(2));

            assertEquals(3, red.getCaptureCount());
        }
    }

    @Nested
    @DisplayName("changing a piece")
    class Changing {

        private final Piece piece = red.getPieces().get(0);

        @Test
        void leaveBasePlacesThePieceOnTheEntryCell() {
            red.leaveBase(piece, BOARD);

            assertTrue(piece.isOnTrack());
            assertEquals(28, piece.getTrackPosition());
        }

        @Test
        void returnToBaseSendsThePieceBack() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);

            red.returnToBase(piece);

            assertTrue(piece.isAtBase());
        }

        @Test
        void teleportToJumpsToTheGivenCell() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);

            red.teleportTo(piece, 46);

            assertEquals(46, piece.getTrackPosition());
        }

        @Test
        void recordApproachPassCountsOnThePiece() {
            red.recordApproachPass(piece);

            assertEquals(1, piece.getApproachPassCount());
        }

        @Test
        void applyIndividualEffectSetsThePiecesOwnEffect() {
            MovementEffect sick = MovementEffect.of(MovementEffectType.SICK, 4);

            red.applyIndividualEffect(piece, sick);

            assertSame(sick, piece.getIndividualEffect());
        }

        @Test
        void applyBlockEffectRecordsTheBlockSize() {
            red.applyBlockEffect(piece, MovementEffect.of(MovementEffectType.ENERGIZED, 4), 2);

            assertTrue(piece.hasActiveBlockEffectForSize(2));
        }

        @Test
        void tickMovementEffectsExpiresEveryPiecesEffects() {
            red.applyIndividualEffect(piece, MovementEffect.of(MovementEffectType.SICK, 0));
            red.applyBlockEffect(piece, MovementEffect.of(MovementEffectType.SICK, 0), 2);

            red.tickMovementEffects();

            assertFalse(piece.getIndividualEffect().isActive());
            assertFalse(piece.getBlockEffect().isActive());
        }

        @Test
        void applyRestrictionStopsThePieceMoving() {
            red.applyRestriction(piece, new BetaRestrictedState());

            assertTrue(piece.getRestrictionState().forbidsMovement());
        }

        @Test
        void tickRestrictionsUsesUpARoundOnEveryRestrictedPiece() {
            red.applyRestriction(piece, new BetaRestrictedState());

            red.tickRestrictions();

            assertEquals(3, piece.getRestrictionState().getRoundsRemaining());
        }

        @Test
        void recordRestrictionRollOnlyCountsForRestrictedPieces() {
            Piece free = red.getPieces().get(1);
            red.applyRestriction(piece, new BetaRestrictedState());

            red.recordRestrictionRoll(3);
            red.recordRestrictionRoll(3);

            assertEquals(List.of(piece), red.findPiecesTriggeredForReturnToBase());
            assertFalse(free.getRestrictionState().hasTriggeredReturnToBase());
        }

        @Test
        void noPieceIsTriggeredForReturnToBaseWithoutARestriction() {
            red.recordRestrictionRoll(3);
            red.recordRestrictionRoll(3);

            assertTrue(red.findPiecesTriggeredForReturnToBase().isEmpty());
        }

        @Test
        void assignMovementDirectionSetsTheDirection() {
            red.assignMovementDirection(piece, ClockwiseMovementStrategy.getInstance());

            assertSame(ClockwiseMovementStrategy.getInstance(), piece.getMovementDirection());
        }

        @Test
        void adoptBlockDirectionChangesTheCurrentDirection() {
            red.assignMovementDirection(piece, ClockwiseMovementStrategy.getInstance());

            red.adoptBlockDirection(piece, CounterClockwiseMovementStrategy.getInstance(), 2);

            assertSame(CounterClockwiseMovementStrategy.getInstance(), piece.getMovementDirection());
        }

        @Test
        void restoreOriginalDirectionUndoesTheBlockDirection() {
            red.assignMovementDirection(piece, ClockwiseMovementStrategy.getInstance());
            red.adoptBlockDirection(piece, CounterClockwiseMovementStrategy.getInstance(), 2);

            red.restoreOriginalDirection(piece);

            assertSame(ClockwiseMovementStrategy.getInstance(), piece.getMovementDirection());
        }

        @Test
        void reverseDirectionFlipsTheDirection() {
            red.assignMovementDirection(piece, ClockwiseMovementStrategy.getInstance());

            red.reverseDirection(piece);

            assertSame(CounterClockwiseMovementStrategy.getInstance(), piece.getMovementDirection());
        }

        @Test
        void moveForwardMovesAPieceOnTheTrack() {
            Fixtures.placeOnTrackClockwise(red, 0, 10);

            red.moveForward(piece, 5, BOARD, ENTRY_ALLOWED, ClockwiseMovementStrategy.getInstance());

            assertEquals(15, piece.getTrackPosition());
        }
    }

    @Nested
    @DisplayName("moving a piece that cannot move")
    class IllegalMoves {

        @Test
        void aPieceAtBaseCannotMoveForward() {
            Piece piece = red.getPieces().get(0);

            assertThrows(IllegalMoveException.class,
                    () -> red.moveForward(piece, 3, BOARD, ENTRY_ALLOWED, ClockwiseMovementStrategy.getInstance()));
        }

        @Test
        void aPieceAlreadyHomeCannotMoveForward() {
            Piece piece = Fixtures.placeHome(red, 0);

            assertThrows(IllegalMoveException.class,
                    () -> red.moveForward(piece, 3, BOARD, ENTRY_ALLOWED, ClockwiseMovementStrategy.getInstance()));
        }
    }

    @Nested
    @DisplayName("acting on another player's piece")
    class Ownership {

        private final Piece foreignPiece = new Piece(PlayerColor.BLUE, 1);

        record Action(String name, BiConsumer<Player, Piece> run) {

            @Override
            public String toString() {
                return name;
            }
        }

        static Stream<Action> everyPieceAction() {
            return Stream.of(
                    new Action("recordCapture", Player::recordCapture),
                    new Action("leaveBase", (player, piece) -> player.leaveBase(piece, BOARD)),
                    new Action("returnToBase", Player::returnToBase),
                    new Action("teleportTo", (player, piece) -> player.teleportTo(piece, 5)),
                    new Action("recordApproachPass", Player::recordApproachPass),
                    new Action("applyIndividualEffect",
                            (player, piece) -> player.applyIndividualEffect(piece, MovementEffect.none())),
                    new Action("applyBlockEffect",
                            (player, piece) -> player.applyBlockEffect(piece, MovementEffect.none(), 2)),
                    new Action("applyRestriction",
                            (player, piece) -> player.applyRestriction(piece, new BetaRestrictedState())),
                    new Action("assignMovementDirection",
                            (player, piece) -> player.assignMovementDirection(
                                    piece, ClockwiseMovementStrategy.getInstance())),
                    new Action("adoptBlockDirection",
                            (player, piece) -> player.adoptBlockDirection(
                                    piece, ClockwiseMovementStrategy.getInstance(), 2)),
                    new Action("restoreOriginalDirection", Player::restoreOriginalDirection),
                    new Action("reverseDirection", Player::reverseDirection),
                    new Action("moveForward",
                            (player, piece) -> player.moveForward(
                                    piece, 3, BOARD, ENTRY_ALLOWED, ClockwiseMovementStrategy.getInstance())));
        }

        @ParameterizedTest(name = "{0} is refused")
        @MethodSource("everyPieceAction")
        void aPlayerCannotChangeAnotherPlayersPiece(Action action) {
            assertThrows(PieceOwnershipException.class, () -> action.run().accept(red, foreignPiece));
        }

        @Test
        void theErrorNamesThePieceAndTheOwner() {
            PieceOwnershipException exception =
                    assertThrows(PieceOwnershipException.class, () -> red.recordCapture(foreignPiece));

            assertTrue(exception.getMessage().contains("B1"));
            assertTrue(exception.getMessage().contains("RED"));
        }
    }

    @Nested
    @DisplayName("the four colors")
    class Colors {

        static Stream<Arguments> everyColor() {
            return Stream.of(
                    Arguments.of(new RedPlayer(), PlayerColor.RED),
                    Arguments.of(new YellowPlayer(), PlayerColor.YELLOW),
                    Arguments.of(new GreenPlayer(), PlayerColor.GREEN),
                    Arguments.of(new BluePlayer(), PlayerColor.BLUE));
        }

        @ParameterizedTest(name = "{0} plays {1}")
        @MethodSource("everyColor")
        void eachPlayerClassPlaysItsOwnColor(Player player, PlayerColor expectedColor) {
            assertEquals(expectedColor, player.getColor());
            assertEquals(expectedColor, player.getPieces().get(0).getColor());
        }
    }
}
