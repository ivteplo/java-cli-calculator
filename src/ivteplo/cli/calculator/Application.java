// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

import java.util.Scanner;

public class Application {
    private static final Calculator calculator = new Calculator();

    public static void main(String[] args) {
        while (true) {
            try {
                evaluateInput();
            } catch (CalculationError error) {
                System.out.println(error.getMessage());
            }
        }
    }

    private static void evaluateInput() {
        int result = calculator.evaluate(readLine());
        System.out.println(result);
    }

    private static String readLine() {
        System.out.print("> ");
        return new Scanner(System.in).nextLine();
    }
}
