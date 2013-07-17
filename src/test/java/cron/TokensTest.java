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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class TokensTest {
    private Tokens tokens;

    @Test
    public void empty() {
        tokenize("");
        assertFalse(tokens.hasNext());
        assertEndOfInput();
    }

    @Test
    public void end() {
        tokenize("1");
        assertNextIsNumber(1);
        assertFalse(tokens.hasNext());
        assertEndOfInput();
    }

    @Test
    public void offset() {
        tokenize("5,6");
        tokens.offset(5);
        assertNextIsNumber(0);
        assertNextIs(Token.VALUE_SEPARATOR);
        assertNextIsNumber(1);
        assertEndOfInput();
    }

    @Test
    public void resetClearsOffset() {
        tokenize("2,2");
        tokens.offset(1);
        assertNextIsNumber(1);
        assertNextIs(Token.VALUE_SEPARATOR);
        tokens.reset();
        assertNextIsNumber(2);
    }

    @Test
    public void resetClearsKeywords() {
        tokenize("FRI,FRI");
        tokens.keywords(DayOfWeekField.Builder.KEYWORDS);
        assertNextIsNumber(5);
        assertNextIs(Token.VALUE_SEPARATOR);
        tokens.reset();
        try {
            tokens.next();
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Bad keyword 'FRI' at position 4 in string: FRI,FRI", e.getMessage());
        }
    }

    @Test
    public void matchOne() {
        tokenize("?");
        assertNextIs(Token.MATCH_ONE);
        assertEndOfInput();
    }

    @Test
    public void matchAll() {
        tokenize("*");
        assertNextIs(Token.MATCH_ALL);
        assertEndOfInput();
    }

    @Test
    public void skip() {
        tokenize("/");
        assertNextIs(Token.SKIP);
        assertEndOfInput();
    }

    @Test
    public void range() {
        tokenize("-");
        assertNextIs(Token.RANGE);
        assertEndOfInput();
    }

    @Test
    public void last() {
        tokenize("1L");
        assertNextIsNumber(1);
        assertNextIs(Token.LAST);
        assertEndOfInput();
    }

    @Test
    public void lastAlone() {
        tokenize("L");
        assertNextIs(Token.LAST);
        assertEndOfInput();
    }

    @Test
    public void weekday() {
        tokenize("1W");
        assertNextIsNumber(1);
        assertNextIs(Token.WEEKDAY);
        assertEndOfInput();
    }

    @Test
    public void nth() {
        tokenize("1#2");
        assertNextIsNumber(1);
        assertNextIs(Token.NTH);
        assertNextIsNumber(2);
        assertEndOfInput();
    }

    @Test
    public void multipleWhitespaceCharacters() {
        tokenize(" \t \t \t \t ");
        assertEquals(Token.FIELD_SEPARATOR, tokens.next());
        assertEndOfInput();
    }

    @Test
    public void keywordRange() {
        tokenize("MON-FRI");
        tokens.keywords(DayOfWeekField.Builder.KEYWORDS);
        assertEquals(Token.NUMBER, tokens.next());
        assertEquals(1, tokens.number());
        assertEquals(Token.RANGE, tokens.next());
        assertEquals(Token.NUMBER, tokens.next());
        assertEquals(5, tokens.number());
        assertEndOfInput();
    }

    @Test
    public void badCharacter() {
        tokenize("5%");
        assertEquals(Token.NUMBER, tokens.next());
        try {
            tokens.next();
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Bad character '%' at position 1 in string: 5%", e.getMessage());
        }
    }

    @Test
    public void badLetter() {
        tokenize("1F");
        assertEquals(Token.NUMBER, tokens.next());
        try {
            tokens.next();
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Bad character 'F' at position 1 in string: 1F", e.getMessage());
        }
    }

    @Test
    public void badKeywordOfValidLength() {
        tokenize("ABC");
        tokens.keywords(DayOfWeekField.Builder.KEYWORDS);
        try {
            tokens.next();
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Bad keyword 'ABC' at position 0 in string: ABC", e.getMessage());
        }
    }

    @Test
    public void badKeywordOfInvalidLength() {
        tokenize("AB");
        tokens.keywords(DayOfWeekField.Builder.KEYWORDS);
        try {
            tokens.next();
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Bad keyword 'AB' at position 0 in string: AB", e.getMessage());
        }
    }

    private void assertEndOfInput() {
        assertNextIs(Token.END_OF_INPUT);
    }

    private void assertNextIsNumber(int expected) {
        assertNextIs(Token.NUMBER);
        assertEquals(expected, tokens.number());
    }

    private void assertNextIs(Token expected) {
        assertEquals(expected, tokens.next());
    }

    private void tokenize(String s) {
        tokens = new Tokens(s);
    }
}
