package support;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

// Runs code while System.out is redirected, and always puts the real console back afterwards.
public final class ConsoleCapture {

    private ConsoleCapture() {
    }

    public static String during(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));

        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }

        return captured.toString(StandardCharsets.UTF_8);
    }
}
