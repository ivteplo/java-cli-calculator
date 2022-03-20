// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

import ivteplo.cli.calculator.AST.Node;

import java.math.BigDecimal;

public class Calculator {
    public BigDecimal evaluate(String input) {
        Node node = new Parser(input).parse();
        return node.evaluate(input);
    }
}
