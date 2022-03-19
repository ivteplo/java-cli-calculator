// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

public class AST {
    public static class Node {
        public int index;

        public Node(int index) {
            this.index = index;
        }

        public int evaluate(String sourceInput) {
            throw new Error("Don't know how to evaluate");
        }
    }

    public static class Number extends Node {
        public int value;

        public Number(int value, int index) {
            super(index);
            this.value = value;
        }

        public int evaluate(String sourceInput) {
            return this.value;
        }
    }

    public static class UnaryExpression extends Node {
        public Operator operator;
        public Node argument;

        public enum Operator {
            PLUS,
            MINUS
        }

        public UnaryExpression(Operator operator, Node argument, int index) {
            super(index);
            this.operator = operator;
            this.argument = argument;
        }

        public int evaluate(String sourceInput) {
            int argument = this.argument.evaluate(sourceInput);

            return switch (operator) {
                case PLUS -> +argument;
                case MINUS -> -argument;
            };
        }
    }

    public static class BinaryExpression extends Node {
        public Node left;
        public Node right;
        public Operator operator;

        public enum Operator {
            PLUS,
            MINUS,
            TIMES,
            DIVIDED_BY
        }

        public static int precedenceOf(Operator operator) {
            return switch (operator) {
                case PLUS, MINUS -> 1;
                case TIMES, DIVIDED_BY -> 2;
            };
        }

        public BinaryExpression(Operator operator, Node left, Node right, int index) {
            super(index);
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        public int evaluate(String sourceInput) {
            int left = this.left.evaluate(sourceInput);
            int right = this.right.evaluate(sourceInput);

            return switch (operator) {
                case PLUS -> left + right;
                case MINUS -> left - right;
                case TIMES -> left * right;
                case DIVIDED_BY -> divide(left, right, sourceInput);
            };
        }

        private int divide(int left, int right, String sourceInput) {
            if (right == 0) {
                throw new CalculationError("Division by zero", sourceInput, this.left.index);
            }

            return left / right;
        }
    }
}
