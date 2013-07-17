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

import static org.junit.Assert.assertTrue;

public class DayOfWeekFieldTest {
    private DayOfWeekField field;

    @Test
    public void keywords() {
        assertTrue(parse("SUN", false).contains(0));
        assertTrue(parse("MON", false).contains(1));
        assertTrue(parse("TUE", false).contains(2));
        assertTrue(parse("WED", false).contains(3));
        assertTrue(parse("THU", false).contains(4));
        assertTrue(parse("FRI", false).contains(5));
        assertTrue(parse("SAT", false).contains(6));
    }

    @Test
    public void oneBased() {
        assertTrue(parse("1", true).contains(0));
        assertTrue(parse("2", true).contains(1));
    }

    private DayOfWeekField parse(String s, boolean oneBased) {
        return field = DayOfWeekField.parse(new Tokens(s), oneBased);
    }
}
