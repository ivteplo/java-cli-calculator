// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

import ivteplo.cli.calculator.AST.*;

import static ivteplo.cli.calculator.AST.BinaryExpression.precedenceOf;

public class Parser {
    private final Lexer lexer;
    private final String input;
    private Token lastPeekedToken;

    public Parser(String input) {
        this.input = input;
        lexer = new Lexer(input);
    }

    public Node parse() {
        return parse(true);
    }

    public Node parse(boolean callPostParse) {
        Node result;
        Token token = getCurrentToken();

        if (peekToken("Number")) {
            result = parseNumber();
        } else if (peekToken("Operator") && (token.value.equals("+") || token.value.equals("-"))) {
            result = parseUnaryExpression();
        } else if (peekToken("LeftParenthesis")) {
            result = parseWrappedExpression();
        } else {
            throw new CalculationError("Unexpected token: " + token.value, input, token.index);
        }

        return callPostParse ? postParse(result) : result;
    }

    private Node postParse(AST.Node node) {
        if (peekToken("Operator")) {
            return postParse(parseBinaryExpression(node));
        }

        return node;
    }

    private AST.Number parseNumber() {
        Token numberToken = eatToken("Number");
        return new AST.Number(Integer.parseInt(numberToken.value), numberToken.index);
    }

    private UnaryExpression parseUnaryExpression() {
        Token operatorToken = eatToken("Operator");
        UnaryExpression.Operator operator = switch (operatorToken.value) {
            case "+" -> UnaryExpression.Operator.PLUS;
            case "-" -> UnaryExpression.Operator.MINUS;
            default -> {
                String errorMessage = "Unknown operator: " + operatorToken.value;
                throw new CalculationError(errorMessage, input, operatorToken.index);
            }
        };

        Node argument = parse(false);
        return new UnaryExpression(operator, argument, operatorToken.index);
    }

    private WrappedExpression parseWrappedExpression() {
        Token start = eatToken("LeftParenthesis");
        Node value = parse();
        eatToken("RightParenthesis");
        return new WrappedExpression(value, start.index);
    }

    private Node parseBinaryExpression(Node left) {
        Token operatorToken = eatToken("Operator");
        Node right = parse();
        BinaryExpression.Operator operator = switch (operatorToken.value) {
            case "+" -> BinaryExpression.Operator.PLUS;
            case "-" -> BinaryExpression.Operator.MINUS;
            case "*" -> BinaryExpression.Operator.TIMES;
            case "/" -> BinaryExpression.Operator.DIVIDED_BY;
            default -> {
                String errorMessage = "Unknown operator: " + operatorToken.value;
                throw new CalculationError(errorMessage, input, operatorToken.index);
            }
        };

        if (right instanceof BinaryExpression) {
            int rightPrecedence = precedenceOf(((BinaryExpression) right).operator);
            int leftPrecedence = precedenceOf(operator);

            if (rightPrecedence < leftPrecedence) {
                left = new BinaryExpression(operator, left, ((BinaryExpression) right).left, left.index);
                operator = ((BinaryExpression) right).operator;
                right = ((BinaryExpression) right).right;
            }
        }

        return new BinaryExpression(operator, left, right, left.index);
    }

    private Token getCurrentToken() {
        if (lastPeekedToken == null) {
            lastPeekedToken = lexer.nextToken();
        }

        return lastPeekedToken;
    }

    private boolean peekToken(String type) {
        return getCurrentToken().type.equals(type);
    }

    private Token eatToken(String type) {
        Token token = getCurrentToken();

        if (!token.type.equals(type)) {
            String message = "Expected token of type " + type + ", but got " + token.type;
            throw new CalculationError(message, input, token.index);
        }

        lastPeekedToken = null;
        return token;
    }
}
