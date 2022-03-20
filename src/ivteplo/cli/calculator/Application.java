// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

import java.math.BigDecimal;
import java.util.Scanner;

public class Application {
    private static final Calculator calculator = new Calculator();

    public static void main(String[] args) {
        while (true) {
            try {
                evaluateInput();
            } catch (CalculationError error) {
                System.out.println(error.getMessage());
            } catch (NumberFormatException error) {
                System.out.println(error);
                System.out.println("Maybe you've entered a too big number");
            }
        }
    }

    private static void evaluateInput() {
        BigDecimal result = calculator.evaluate(readLine());
        System.out.println(result.toString());
    }

    private static String readLine() {
        System.out.print("> ");
        return new Scanner(System.in).nextLine();
    }
}
