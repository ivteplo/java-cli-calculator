// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

public class Lexer {
    private final String input;
    private int index = 0;

    public Lexer(String inputString) {
        this.input = inputString;
    }

    // Check if we have reached the end of input
    private boolean reachedEnd() {
        return index >= input.length();
    }

    public Token nextToken() {
        if (reachedEnd()) {
            return new Token("EndOfFile", "", input.length());
        }

        int start = index;
        char currentChar = input.charAt(index);

        if (Character.isDigit(currentChar)) {
            StringBuilder value = new StringBuilder();

            while (!reachedEnd() && Character.isDigit(currentChar = input.charAt(index))) {
                value.append(currentChar);
                index += 1;
            }

            return new Token("Number", value.toString(), start);
        }

        switch (currentChar) {
            case '+', '-', '*', '/' -> {
                index += 1;
                return new Token("Operator", currentChar + "", start);
            }
            default -> throw new Error("Unexpected character at position 1:" + (index + 1));
        }
    }
}
