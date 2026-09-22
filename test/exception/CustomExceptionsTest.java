package exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

// Each custom exception extends the closest JDK RuntimeException, so callers can still catch that type.
@DisplayName("Custom exceptions")
class CustomExceptionsTest {

    private static final String MESSAGE = "something went wrong";
    private static final Throwable CAUSE = new IllegalStateException("root cause");

    record ExceptionSpec(
            String name, Function<String, RuntimeException> withMessage,
            BiFunction<String, Throwable, RuntimeException> withCause, Class<? extends RuntimeException> jdkParent) {

        @Override
        public String toString() {
            return name;
        }
    }

    static Stream<ExceptionSpec> everyCustomException() {
        return Stream.of(
                new ExceptionSpec("IllegalMoveException", IllegalMoveException::new,
                        IllegalMoveException::new, RuntimeException.class),
                new ExceptionSpec("InvalidPieceStateException", InvalidPieceStateException::new,
                        InvalidPieceStateException::new, IllegalStateException.class),
                new ExceptionSpec("PieceOwnershipException", PieceOwnershipException::new,
                        PieceOwnershipException::new, IllegalArgumentException.class),
                new ExceptionSpec("PlayerNotFoundException", PlayerNotFoundException::new,
                        PlayerNotFoundException::new, NoSuchElementException.class),
                new ExceptionSpec("UnpresentableMessageException", UnpresentableMessageException::new,
                        UnpresentableMessageException::new, IllegalStateException.class));
    }

    @ParameterizedTest(name = "{0} keeps the message it is given")
    @MethodSource("everyCustomException")
    void messageConstructorKeepsMessage(ExceptionSpec spec) {
        RuntimeException exception = spec.withMessage().apply(MESSAGE);

        assertEquals(MESSAGE, exception.getMessage());
    }

    @ParameterizedTest(name = "{0} has no cause when built from a message only")
    @MethodSource("everyCustomException")
    void messageConstructorLeavesCauseEmpty(ExceptionSpec spec) {
        RuntimeException exception = spec.withMessage().apply(MESSAGE);

        assertNull(exception.getCause());
    }

    @ParameterizedTest(name = "{0} keeps the cause it is given")
    @MethodSource("everyCustomException")
    void causeConstructorKeepsCauseAndMessage(ExceptionSpec spec) {
        RuntimeException exception = spec.withCause().apply(MESSAGE, CAUSE);

        assertSame(CAUSE, exception.getCause());
        assertEquals(MESSAGE, exception.getMessage());
    }

    @ParameterizedTest(name = "{0} is an unchecked exception of the expected JDK type")
    @MethodSource("everyCustomException")
    void extendsTheClosestJdkRuntimeException(ExceptionSpec spec) {
        RuntimeException exception = spec.withMessage().apply(MESSAGE);

        assertInstanceOf(spec.jdkParent(), exception);
    }
}
