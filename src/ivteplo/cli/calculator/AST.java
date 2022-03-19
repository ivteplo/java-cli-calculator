// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

import ivteplo.cli.calculator.utils.BigNumber;

public class AST {
    public static class Node {
        public int index;

        public Node(int index) {
            this.index = index;
        }

        public BigNumber evaluate(String sourceInput) {
            throw new Error("Don't know how to evaluate");
        }
    }

    public static class Number extends Node {
        public BigNumber value;

        public Number(BigNumber value, int index) {
            super(index);
            this.value = value;
        }

        public BigNumber evaluate(String sourceInput) {
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

        public BigNumber evaluate(String sourceInput) {
            BigNumber argument = this.argument.evaluate(sourceInput);

            return switch (operator) {
                case PLUS -> argument;
                case MINUS -> argument.multiply(new BigNumber(-1));
            };
        }
    }

    public static class WrappedExpression extends Node {
        public Node value;

        public WrappedExpression(Node value, int index) {
            super(index);
            this.value = value;
        }

        public BigNumber evaluate(String sourceInput) {
            return value.evaluate(sourceInput);
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
            DIVIDED_BY,
            EXPONENTIATION
        }

        public static int precedenceOf(Operator operator) {
            return switch (operator) {
                case PLUS, MINUS -> 1;
                case TIMES, DIVIDED_BY -> 2;
                case EXPONENTIATION -> 3;
            };
        }

        public BinaryExpression(Operator operator, Node left, Node right, int index) {
            super(index);
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        public BigNumber evaluate(String sourceInput) {
            BigNumber left = this.left.evaluate(sourceInput);
            BigNumber right = this.right.evaluate(sourceInput);

            return switch (operator) {
                case PLUS -> left.add(right);
                case MINUS -> left.substract(right);
                case TIMES -> left.multiply(right);
                case DIVIDED_BY -> divide(left, right, sourceInput);
                case EXPONENTIATION -> left.power(right);
            };
        }

        private BigNumber divide(BigNumber left, BigNumber right, String sourceInput) {
            if (right.toDouble() == 0.0d) {
                throw new CalculationError("Division by zero", sourceInput, this.left.index);
            }

            return left.divide(right);
        }
    }
}
