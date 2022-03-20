// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class AST {
    public static class Node {
        public int index;

        public Node(int index) {
            this.index = index;
        }

        public BigDecimal evaluate(String sourceInput) {
            throw new Error("Don't know how to evaluate");
        }
    }

    public static class Number extends Node {
        public BigDecimal value;

        public Number(BigDecimal value, int index) {
            super(index);
            this.value = value;
        }

        public BigDecimal evaluate(String sourceInput) {
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

        public BigDecimal evaluate(String sourceInput) {
            BigDecimal argument = this.argument.evaluate(sourceInput);

            return switch (operator) {
                case PLUS -> argument;
                case MINUS -> argument.multiply(new BigDecimal(-1));
            };
        }
    }

    public static class WrappedExpression extends Node {
        public Node value;

        public WrappedExpression(Node value, int index) {
            super(index);
            this.value = value;
        }

        public BigDecimal evaluate(String sourceInput) {
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

        public BigDecimal evaluate(String sourceInput) {
            BigDecimal left = this.left.evaluate(sourceInput);
            BigDecimal right = this.right.evaluate(sourceInput);

            return switch (operator) {
                case PLUS -> left.add(right);
                case MINUS -> left.subtract(right);
                case TIMES -> left.multiply(right);
                case DIVIDED_BY -> divide(left, right, sourceInput);
                case EXPONENTIATION -> pow(left, right, sourceInput);
            };
        }

        private BigDecimal divide(BigDecimal left, BigDecimal right, String sourceInput) {
            if (right.equals(new BigDecimal(0))) {
                throw new CalculationError("Division by zero", sourceInput, this.left.index);
            }

            return left.divide(right, MathContext.DECIMAL128);
        }

        private BigDecimal pow(BigDecimal number, BigDecimal power, String sourceInput) {
            BigDecimal minusOne = new BigDecimal(-1);
            BigDecimal zero = new BigDecimal(0);
            BigDecimal one = new BigDecimal(1);
            BigDecimal two = new BigDecimal(2);

            if (power.signum() == -1) {
                BigDecimal positivePower = power.multiply(minusOne);
                BigDecimal resultForPositivePower = pow(number, positivePower, sourceInput);
                return divide(one, resultForPositivePower, sourceInput);
            }

            if (zero.equals(power.remainder(two))) {
                BigDecimal numberSquared = number.multiply(number);
                BigDecimal result = one;
                BigDecimal iterationsLeft = power.divide(two, RoundingMode.DOWN);

                while (!iterationsLeft.equals(zero)) {
                    result = result.multiply(numberSquared);
                    iterationsLeft = iterationsLeft.subtract(one);
                }

                return result;
            }

            if (one.equals(power.remainder(two))) {
                return number.multiply(pow(number, power.subtract(one), sourceInput));
            }

            BigDecimal powerIntegerPart = power.setScale(0, RoundingMode.DOWN);
            double powerFloatPart = power.subtract(powerIntegerPart).doubleValue();

            BigDecimal powToInteger = pow(number, powerIntegerPart, sourceInput);
            double powToDecimal = Math.pow(number.doubleValue(), powerFloatPart);

            return powToInteger.multiply(new BigDecimal(powToDecimal));
        }
    }
}
