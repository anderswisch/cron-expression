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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DefaultFieldTest {
    private DefaultField field;

    @Test
    public void emptyMonthField() {
        parse("", 1, 12);
        for (int month = 1; month <= 12; month++)
            assertFalse(field.contains(month));
    }

    @Test
    public void backwardsRange() {
        parse("2-1", 1, 2);
        assertFalse(field.contains(0));
        assertFalse(field.contains(1));
        assertFalse(field.contains(2));
        assertFalse(field.contains(3));
    }

    @Test
    public void finalWildcardWins() {
        parse("1-2,2,3,*", 1, 10);
        assertContainsRange(1, 10);
    }

    @Test
    public void initialWildcardWins() {
        parse("*,1-2,2,3", 1, 10);
        assertContainsRange(1, 10);
    }

    @Test
    public void multipleRanges() {
        parse("1-2,3-4", 1, 5);
        assertContains(1, 2, 3, 4);
    }

    @Test
    public void multipleNumbers() {
        parse("1,2,3", 1, 5);
        assertContains(1, 2, 3);
    }

    @Test
    public void danglingRange() {
        try {
            parse("1-", 0, 0);
            fail("Expected exception");
        } catch (IllegalStateException e) {
            assertEquals("Expected number", e.getMessage());
        }
    }

    @Test
    public void danglingSkip() {
        try {
            parse("1-2/", 0, 0);
            fail("Expected exception");
        } catch (IllegalStateException e) {
            assertEquals("Expected number", e.getMessage());
        }
    }

    @Test
    public void range() {
        parse("1-12", 1, 12);
        assertContainsRange(1, 12);
    }

    @Test
    public void wildcard() {
        parse("*", 1, 12);
        assertContainsRange(1, 12);
    }

    @Test
    public void skipRangeWithImplicitEnd() {
        parse("1/5", 1, 31);
        assertContains(1, 6, 11, 16, 21, 26, 31);
    }

    @Test
    public void oneBasedSkipRange() {
        parse("1-31/5", 1, 31);
        assertContains(1, 6, 11, 16, 21, 26, 31);
    }

    @Test
    public void zeroBasedSkipRange() {
        parse("0-20/5", 0, 59);
        assertContains(0, 5, 10, 15, 20);
    }

    @Test
    public void wildcardSkipRange() {
        parse("*/5", 0, 20);
        assertContains(0, 5, 10, 15, 20);
    }

    private void assertContains(int... numbers) {
        for (int number : numbers)
            assertTrue(field.contains(number));
    }

    private void assertContainsRange(int first, int last) {
        for (int number = first; number <= last; number++)
            assertContains(number);
    }

    private DefaultField parse(String s, int min, int max) {
        return field = DefaultField.parse(new Tokens(s), min, max);
    }
}
