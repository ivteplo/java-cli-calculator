// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator.utils;

public class BigNumber {
    public long integerPart;
    public long decimalPart;

    public BigNumber(long integerPart, long decimalPart) {
        this.integerPart = integerPart;
        this.decimalPart = decimalPart;
        normalize();
    }

    public BigNumber(double number) {
        integerPart = (long) Math.floor(number);
        double decimal = number - integerPart;
        decimalPart = (long)(decimal * Math.pow(10, Math.max(0, Double.toString(decimal).length() - 2)));
    }

    public BigNumber(String number) {
        if (number.indexOf('.') != -1) {
            String[] parts = number.split("\\.");
            integerPart = Long.parseLong(parts[0]);
            decimalPart = Long.parseLong(parts[1]);
            normalize();
        } else {
            integerPart = Long.parseLong(number);
            decimalPart = 0;
            normalize();
        }
    }

    private void normalize() {
        while (decimalPart % 10 == 0 && decimalPart != 0) {
            decimalPart /= 10;
        }
    }

    public String toString() {
        return integerPart + "." + decimalPart;
    }

    private long[] decimalPartsOfSameLength(long first, long second) {
        int firstLength = (first + "").length();
        int secondLength = (second + "").length();

        while (firstLength < secondLength) {
            first *= 10;
            firstLength += 1;
        }

        while (firstLength > secondLength) {
            second *= 10;
            secondLength += 1;
        }

        return new long[] { first, second };
    }

    public BigNumber add(BigNumber number) {
        long[] decimalParts = decimalPartsOfSameLength(decimalPart, number.decimalPart);

        return new BigNumber(
            integerPart + number.integerPart,
            decimalParts[0] + decimalParts[1]
        );
    }

    public BigNumber substract(BigNumber number) {
        long[] decimalParts = decimalPartsOfSameLength(decimalPart, number.decimalPart);

        return new BigNumber(
                integerPart - number.integerPart,
                decimalParts[0] - decimalParts[1]
        );
    }

    public double toDouble() {
        // Make the decimal part be in range from 0 to 1
        double decimal = decimalPart / Math.pow(10, Long.toString(decimalPart).length());
        return integerPart + decimal;
    }

    public BigNumber multiply(BigNumber number) {
        return new BigNumber(toDouble() * number.toDouble());
    }

    public BigNumber divide(BigNumber number) {
        return new BigNumber(toDouble() / number.toDouble());
    }

    public BigNumber power(BigNumber number) {
        return new BigNumber(Math.pow(toDouble(), number.toDouble()));
    }
}
