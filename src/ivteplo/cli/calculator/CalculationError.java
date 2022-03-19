package ivteplo.cli.calculator;

public class CalculationError extends Error {
    private static String buildErrorMessage(String message, String input, int index) {
        return message +
            " at position " +
            (index + 1) +
            "\n\t" +
            input +
            "\n\t" +
            " ".repeat(Math.max(0, index - 1)) +
            "^";
    }

    public CalculationError(String message, String input, int index) {
        super(buildErrorMessage(message, input, index));
    }
}
