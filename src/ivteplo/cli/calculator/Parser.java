// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

import ivteplo.cli.calculator.AST.*;
import static ivteplo.cli.calculator.AST.BinaryExpression.precedenceOf;

public class Parser {
    private final Lexer lexer;
    private Token lastPeekedToken;

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    public Node parse() {
        if (peekToken("Number")) {
            return postParse(parseNumber());
        }

        throw new Error("Unexpected token: " + getCurrentToken().value);
    }

    private Node postParse(AST.Node node) {
        if (peekToken("Operator")) {
            return parseBinaryExpression(node);
        }

        return node;
    }

    private AST.Number parseNumber() {
        Token numberToken = eatToken("Number");
        return new AST.Number(Integer.parseInt(numberToken.value));
    }

    private Node parseBinaryExpression(Node left) {
        Token operatorToken = eatToken("Operator");
        Node right = parse();
        BinaryExpression.Operator operator = switch (operatorToken.value) {
            case "+" -> BinaryExpression.Operator.PLUS;
            case "-" -> BinaryExpression.Operator.MINUS;
            case "*" -> BinaryExpression.Operator.TIMES;
            case "/" -> BinaryExpression.Operator.DIVIDED_BY;
            default -> throw new Error("Unknown operator: " + operatorToken.value);
        };

        if (right instanceof BinaryExpression) {
            int rightPrecedence = precedenceOf(((BinaryExpression) right).operator);
            int leftPrecedence = precedenceOf(operator);

            if (rightPrecedence < leftPrecedence) {
                left = new BinaryExpression(operator, left, ((BinaryExpression) right).left);
                operator = ((BinaryExpression) right).operator;
                right = ((BinaryExpression) right).right;
            }
        }

        return postParse(new BinaryExpression(operator, left, right));
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
            throw new Error("Expected token of type " + type + ", but got " + token.type);
        }

        lastPeekedToken = null;
        return token;
    }
}
