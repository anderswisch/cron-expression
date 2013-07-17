/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anders Wisch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cron;

final class Tokens {
    private int number;

    public int number() {
        return number;
    }

    private int offset;
    private Keywords keywords;

    public void offset(int offset) {
        this.offset = offset;
    }

    public void keywords(Keywords k) {
        keywords = k;
    }

    public void reset() {
        offset = 0;
        keywords = null;
    }

    private final String source;
    private final int length;
    private int position;

    public Tokens(String s) {
        source = s;
        length = s.length();
        position = 0;
    }

    public boolean hasNext() {
        return hasNextChar();
    }

    public Token next() {
        if (position >= length)
            return Token.END_OF_INPUT;
        int start = position;
        char c = currentChar();
        switch (c) {
            case ' ':
            case '\t':
                do {
                    if (!hasNextChar()) {
                        position++;
                        break;
                    }
                    c = nextChar();
                } while (isWhitespace(c));
                return Token.FIELD_SEPARATOR;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                do {
                    if (!hasNextChar()) {
                        position++;
                        break;
                    }
                    c = nextChar();
                } while (isDigit(c));
                number = Integer.parseInt(substringFrom(start)) - offset;
                return Token.NUMBER;
            case ',':
                position++;
                return Token.VALUE_SEPARATOR;
            case '*':
                position++;
                return Token.MATCH_ALL;
            case '-':
                position++;
                return Token.RANGE;
            case '/':
                position++;
                return Token.SKIP;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                do {
                    if (!hasNextChar()) {
                        position++;
                        break;
                    }
                    c = nextChar();
                } while (isLetter(c));
                if (position - start == 1) {
                    c = source.charAt(start);
                    if (c == 'L') {
                        return Token.LAST;
                    } else if (c == 'W') {
                        return Token.WEEKDAY;
                    }
                    throw new IllegalArgumentException(badCharacter(c, start));
                } else {
                    if (keywords != null) {
                        try {
                            int mapped = keywords.get(source, start, position);
                            if (mapped != -1) {
                                number = mapped;
                                return Token.NUMBER;
                            }
                        } catch (IllegalArgumentException ignore) {
                        }
                    }
                    throw new IllegalArgumentException(badKeyword(start));
                }
            case '?':
                position++;
                return Token.MATCH_ONE;
            case '#':
                position++;
                return Token.NTH;
        }
        throw new IllegalArgumentException(badCharacter(c, position));
    }

    private String badCharacter(char c, int index) {
        return "Bad character '" + c + "' at position " + index + " in string: " + source;
    }

    private String badKeyword(int start) {
        return "Bad keyword '" + substringFrom(start) + "' at position " + start + " in string: " + source;
    }

    private String substringFrom(int start) {
        return source.substring(start, position);
    }

    private boolean hasNextChar() {
        return position < length - 1;
    }

    private char nextChar() {
        return source.charAt(++position);
    }

    private char currentChar() {
        return source.charAt(position);
    }

    private static boolean isLetter(char c) {
        return 'A' <= c && c <= 'Z';
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }
}
