package com.ocr.morsecode.tools;

public class MorseCode {

    private static final String NULL = "";
    private static final String SPACE = " ";
    private static final String DOT = ".";
    private static final String DASH = "-";
    private static final char CHAR_DOT = '.';
    private static final char CHAR_DASH = '-';
    private static final String WHITESPACE = "\\s";
    private static final String ALPHANUMERIC_WHITESPACE = "[^A-Z0-9\\s]";
    private static final String DOT_DASH_WHITESPACE = "[^.\\-\\s]";
    private static final int DIGIT_CODE_SIZE = 5;

    public static String encode(String input) {
        StringBuilder output = new StringBuilder();
        String c;
        input = input.toUpperCase().replaceAll(ALPHANUMERIC_WHITESPACE, NULL);
        for (char character : input.toCharArray()) {
            c = String.valueOf(character);
            if (c.matches(WHITESPACE)) {
                output.append(c);
            } else {
                try {
                    output.append(Character.valueOf(c).getCode());
                } catch (IllegalArgumentException e) {
                    int n = Integer.parseInt(c);
                    if (n < DIGIT_CODE_SIZE)
//                        c = DOT.repeat(n) + DASH.repeat(DIGIT_CODE_SIZE - n);
                        c = repeat(DOT, n) + repeat(DASH, DIGIT_CODE_SIZE - n);
                    else
//                        c = DASH.repeat(n - DIGIT_CODE_SIZE) + DOT.repeat(2 * DIGIT_CODE_SIZE - n);
                        c = repeat(DASH, n - DIGIT_CODE_SIZE) + repeat(DOT, 2 * DIGIT_CODE_SIZE - n);
                    output.append(c);
                }
                output.append(SPACE);
            }
        }
        return output.toString();
    }

    public static String decode(String input) {
        StringBuilder output = new StringBuilder();
        StringBuilder msg = new StringBuilder();
        input = input.replaceAll(DOT_DASH_WHITESPACE, NULL);
        for (char character : input.toCharArray()) {
            boolean decoded = false;
            if (character == CHAR_DOT || character == CHAR_DASH) {
                msg.append(character);
            } else {
                if (msg.length() == 0) {
                    output.append(SPACE);
                } else {
                    for (Character c : Character.values()) {
                        if (c.getCode().equals(msg.toString())) {
                            output.append(c.toString());
                            decoded = true;
                            break;
                        }
                    }
                    if (!decoded && msg.length() == DIGIT_CODE_SIZE) {
                        if (msg.charAt(DIGIT_CODE_SIZE - 1) == CHAR_DASH)
                            output.append(msg.indexOf(DASH));
                        else
                            output.append(msg.indexOf(DOT) + DIGIT_CODE_SIZE);
                    }
                    msg.delete(0, msg.length());
                }
            }
        }
        return output.toString();
    }

    private static String repeat(String input, int n) {
        if (n < 1)
            return NULL;
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < n; i++)
            output.append(input);
        return output.toString();
    }

    private enum Character {
        A(DOT + DASH), B(DASH + DOT + DOT + DOT), C(DASH + DOT + DASH + DOT), D(DASH + DOT + DOT), E(DOT),
        F(DOT + DOT + DASH + DOT), G(DASH + DASH + DOT), H(DOT + DOT + DOT + DOT), I(DOT + DOT),
        J(DOT + DASH + DASH + DASH), K(DASH + DOT + DASH), L(DOT + DASH + DOT + DOT), M(DASH + DASH), N(DASH + DOT),
        O(DASH + DASH + DASH), P(DOT + DASH + DASH + DOT), Q(DASH + DASH + DOT + DASH), R(DOT + DASH + DOT),
        S(DOT + DOT + DOT), T(DASH), U(DOT + DOT + DASH), V(DOT + DOT + DOT + DASH), W(DOT + DASH + DASH),
        X(DASH + DOT + DOT + DASH), Y(DASH + DOT + DASH + DASH), Z(DASH + DASH + DOT + DOT);

        private String code;

        Character(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}