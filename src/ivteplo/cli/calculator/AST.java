// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

public class AST {
    public static class Node {
        public int evaluate() {
            throw new Error("Don't know how to evaluate");
        }
    }

    public static class Number extends Node {
        public int value;

        public Number(int value) {
            this.value = value;
        }

        public int evaluate() {
            return this.value;
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

        public BinaryExpression(Operator operator, Node left, Node right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        public int evaluate() {
            int left = this.left.evaluate();
            int right = this.right.evaluate();

            return switch (operator) {
                case PLUS -> left + right;
                case MINUS -> left - right;
                case TIMES -> left * right;
                case DIVIDED_BY -> divide(left, right);
            };
        }

        private int divide(int left, int right) {
            if (right == 0) {
                throw new Error("Division by zero");
            }

            return left / right;
        }
    }
}
