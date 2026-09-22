package model.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.MovementEffectType;
import config.enums.PlayerColor;
import exception.InvalidPieceStateException;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.direction.MovementDirectionStrategy;
import model.effect.movement.MovementEffect;
import model.effect.restriction.BetaRestrictedState;
import model.effect.restriction.NoRestrictionState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Piece")
class PieceTest {

    private static final MovementDirectionStrategy CLOCKWISE = ClockwiseMovementStrategy.getInstance();
    private static final MovementDirectionStrategy COUNTER_CLOCKWISE = CounterClockwiseMovementStrategy.getInstance();

    private final Piece piece = new Piece(PlayerColor.RED, 1);

    @Nested
    @DisplayName("a new piece")
    class NewPiece {

        @Test
        void startsAtBase() {
            assertTrue(piece.isAtBase());
            assertFalse(piece.isOnTrack());
            assertFalse(piece.isOnHomeStraight());
            assertFalse(piece.isHome());
        }

        @Test
        void knowsItsColor() {
            assertEquals(PlayerColor.RED, piece.getColor());
        }

        @Test
        void isNamedByColorCodeAndNumber() {
            assertEquals("R1", piece.toString());
            assertEquals("G3", new Piece(PlayerColor.GREEN, 3).toString());
        }

        @Test
        void hasNoMovementDirectionYet() {
            assertFalse(piece.hasMovementDirection());
        }

        @Test
        void hasNoCapturesOrApproachPasses() {
            assertEquals(0, piece.getCaptureCount());
            assertEquals(0, piece.getApproachPassCount());
        }

        @Test
        void hasNoEffectAndNoRestriction() {
            assertFalse(piece.getIndividualEffect().isActive());
            assertFalse(piece.getBlockEffect().isActive());
            assertFalse(piece.getRestrictionState().forbidsMovement());
        }
    }

    @Nested
    @DisplayName("location guards")
    class LocationGuards {

        @Test
        void trackPositionCannotBeReadAtBase() {
            assertThrows(InvalidPieceStateException.class, piece::getTrackPosition);
        }

        @Test
        void trackPositionCannotBeReadOnTheHomeStraight() {
            piece.moveToHomeStraight(1);

            assertThrows(InvalidPieceStateException.class, piece::getTrackPosition);
        }

        @Test
        void homeStraightIndexCannotBeReadOnTheTrack() {
            piece.leaveBase(28);

            assertThrows(InvalidPieceStateException.class, piece::getHomeStraightIndex);
        }

        @Test
        void movementDirectionCannotBeReadBeforeItIsAssigned() {
            assertThrows(InvalidPieceStateException.class, piece::getMovementDirection);
        }

        @Test
        void originalMovementDirectionCannotBeReadBeforeItIsAssigned() {
            assertThrows(InvalidPieceStateException.class, piece::getOriginalMovementDirection);
        }

        @Test
        void errorMessageNamesThePieceAndItsLocation() {
            InvalidPieceStateException exception =
                    assertThrows(InvalidPieceStateException.class, piece::getTrackPosition);

            assertTrue(exception.getMessage().contains("R1"));
            assertTrue(exception.getMessage().contains("BASE"));
        }
    }

    @Nested
    @DisplayName("moving between locations")
    class Locations {

        @Test
        void leaveBaseLandsOnTheEntryCell() {
            piece.leaveBase(28);

            assertTrue(piece.isOnTrack());
            assertEquals(28, piece.getTrackPosition());
        }

        @Test
        void leaveBaseResetsApproachPasses() {
            piece.recordApproachPass();

            piece.leaveBase(28);

            assertEquals(0, piece.getApproachPassCount());
        }

        @Test
        void moveToChangesTheTrackPosition() {
            piece.leaveBase(28);

            piece.moveTo(33);

            assertEquals(33, piece.getTrackPosition());
        }

        @Test
        void moveToHomeStraightRecordsTheIndex() {
            piece.leaveBase(28);

            piece.moveToHomeStraight(2);

            assertTrue(piece.isOnHomeStraight());
            assertEquals(2, piece.getHomeStraightIndex());
        }

        @Test
        void moveHomeMarksThePieceHome() {
            piece.leaveBase(28);

            piece.moveHome();

            assertTrue(piece.isHome());
            assertFalse(piece.isOnTrack());
        }
    }

    @Nested
    @DisplayName("direction")
    class Direction {

        @Test
        void assignSetsBothCurrentAndOriginalDirection() {
            piece.assignMovementDirection(CLOCKWISE);

            assertTrue(piece.hasMovementDirection());
            assertSame(CLOCKWISE, piece.getMovementDirection());
            assertSame(CLOCKWISE, piece.getOriginalMovementDirection());
        }

        @Test
        void assignedPieceHasNotAdoptedABlockDirection() {
            piece.assignMovementDirection(CLOCKWISE);

            assertFalse(piece.hasAdoptedBlockDirection());
        }

        @Test
        void adoptBlockDirectionChangesOnlyTheCurrentDirection() {
            piece.assignMovementDirection(CLOCKWISE);

            piece.adoptBlockDirection(COUNTER_CLOCKWISE, 2);

            assertSame(COUNTER_CLOCKWISE, piece.getMovementDirection());
            assertSame(CLOCKWISE, piece.getOriginalMovementDirection());
            assertTrue(piece.hasAdoptedBlockDirection());
        }

        @Test
        void adoptBlockDirectionRemembersTheBlockSize() {
            piece.assignMovementDirection(CLOCKWISE);

            piece.adoptBlockDirection(COUNTER_CLOCKWISE, 3);

            assertEquals(3, piece.getAdoptedForBlockSize());
        }

        @Test
        void restoreOriginalDirectionGoesBackToTheDirectionFromBaseExit() {
            piece.assignMovementDirection(CLOCKWISE);
            piece.adoptBlockDirection(COUNTER_CLOCKWISE, 2);

            piece.restoreOriginalDirection();

            assertSame(CLOCKWISE, piece.getMovementDirection());
            assertFalse(piece.hasAdoptedBlockDirection());
        }

        @Test
        void reverseDirectionFlipsBothCurrentAndOriginalDirection() {
            piece.assignMovementDirection(CLOCKWISE);

            piece.reverseDirection();

            assertSame(COUNTER_CLOCKWISE, piece.getMovementDirection());
            assertSame(COUNTER_CLOCKWISE, piece.getOriginalMovementDirection());
        }
    }

    @Nested
    @DisplayName("counters")
    class Counters {

        @Test
        void recordApproachPassAddsOne() {
            piece.recordApproachPass();
            piece.recordApproachPass();

            assertEquals(2, piece.getApproachPassCount());
        }

        @Test
        void recordCaptureAddsOne() {
            piece.recordCapture();

            assertEquals(1, piece.getCaptureCount());
        }
    }

    @Nested
    @DisplayName("movement effects")
    class Effects {

        private final MovementEffect energized = MovementEffect.of(MovementEffectType.ENERGIZED, 4);

        @Test
        void individualEffectIsStored() {
            piece.applyIndividualEffect(energized);

            assertSame(energized, piece.getIndividualEffect());
        }

        @Test
        void blockEffectIsActiveOnlyForTheSizeItWasAssignedTo() {
            piece.applyBlockEffect(energized, 2);

            assertTrue(piece.hasActiveBlockEffectForSize(2));
            assertFalse(piece.hasActiveBlockEffectForSize(3));
        }

        @Test
        void blockEffectIsNotActiveWhenNoEffectWasAssigned() {
            assertFalse(piece.hasActiveBlockEffectForSize(0));
        }

        @Test
        void tickIndividualEffectUsesUpOneRound() {
            piece.applyIndividualEffect(energized);

            piece.tickIndividualEffect();

            assertTrue(piece.getIndividualEffect().isActive());
            assertEquals(4, piece.getIndividualEffect().applyTo(2));
        }

        @Test
        void individualEffectEventuallyExpires() {
            piece.applyIndividualEffect(MovementEffect.of(MovementEffectType.SICK, 0));

            piece.tickIndividualEffect();

            assertFalse(piece.getIndividualEffect().isActive());
        }

        @Test
        void blockEffectEventuallyExpires() {
            piece.applyBlockEffect(MovementEffect.of(MovementEffectType.SICK, 0), 2);

            piece.tickBlockEffect();

            assertFalse(piece.getBlockEffect().isActive());
        }
    }

    @Nested
    @DisplayName("restriction")
    class Restriction {

        @Test
        void applyRestrictionForbidsMovement() {
            piece.applyRestriction(new BetaRestrictedState());

            assertTrue(piece.getRestrictionState().forbidsMovement());
        }

        @Test
        void tickRestrictionUsesUpOneRound() {
            piece.applyRestriction(new BetaRestrictedState());

            piece.tickRestriction();

            assertEquals(3, piece.getRestrictionState().getRoundsRemaining());
        }

        @Test
        void recordRestrictionRollFeedsTheReturnToBaseTrigger() {
            piece.applyRestriction(new BetaRestrictedState());

            piece.recordRestrictionRoll(3);
            piece.recordRestrictionRoll(3);

            assertTrue(piece.getRestrictionState().hasTriggeredReturnToBase());
        }
    }

    @Nested
    @DisplayName("returnToBase")
    class ReturnToBase {

        @Test
        void putsThePieceBackAtBase() {
            piece.leaveBase(28);

            piece.returnToBase();

            assertTrue(piece.isAtBase());
        }

        @Test
        void clearsCountersAndDirection() {
            piece.leaveBase(28);
            piece.assignMovementDirection(CLOCKWISE);
            piece.adoptBlockDirection(COUNTER_CLOCKWISE, 2);
            piece.recordApproachPass();
            piece.recordCapture();

            piece.returnToBase();

            assertEquals(0, piece.getApproachPassCount());
            assertEquals(0, piece.getCaptureCount());
            assertEquals(0, piece.getAdoptedForBlockSize());
            assertFalse(piece.hasMovementDirection());
        }

        @Test
        void clearsEffectsAndRestriction() {
            piece.applyIndividualEffect(MovementEffect.of(MovementEffectType.ENERGIZED, 4));
            piece.applyBlockEffect(MovementEffect.of(MovementEffectType.SICK, 4), 2);
            piece.applyRestriction(new BetaRestrictedState());

            piece.returnToBase();

            assertFalse(piece.getIndividualEffect().isActive());
            assertFalse(piece.getBlockEffect().isActive());
            assertSame(NoRestrictionState.getInstance(), piece.getRestrictionState());
        }
    }

    @Nested
    @DisplayName("copyForPreview")
    class CopyForPreview {

        @Test
        void addsTheExtraApproachPassesToTheCopyOnly() {
            piece.recordApproachPass();

            Piece preview = piece.copyForPreview(1);

            assertEquals(2, preview.getApproachPassCount());
            assertEquals(1, piece.getApproachPassCount());
        }

        @Test
        void usesTheOriginalDirectionEvenWhileInABlock() {
            piece.assignMovementDirection(CLOCKWISE);
            piece.adoptBlockDirection(COUNTER_CLOCKWISE, 2);

            Piece preview = piece.copyForPreview(0);

            assertSame(CLOCKWISE, preview.getMovementDirection());
        }

        @Test
        void keepsColorNumberAndCaptureCount() {
            piece.recordCapture();

            Piece preview = piece.copyForPreview(0);

            assertEquals("R1", preview.toString());
            assertEquals(1, preview.getCaptureCount());
        }
    }
}
