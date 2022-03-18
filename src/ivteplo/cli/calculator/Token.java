// Copyright (c) 2022 Ivan Teplov
package ivteplo.cli.calculator;

public class Token {
    public String type;
    public String value;
    public int index;

    public Token(String type, String value, int index) {
        this.type = type;
        this.value = value;
        this.index = index;
    }
}
