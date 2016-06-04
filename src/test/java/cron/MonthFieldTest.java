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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MonthFieldTest {
    private MonthField field;

    @Test
    public void keywords() {
        assertTrue(parse("JAN").contains(1));
        assertTrue(parse("FEB").contains(2));
        assertTrue(parse("MAR").contains(3));
        assertTrue(parse("APR").contains(4));
        assertTrue(parse("MAY").contains(5));
        assertTrue(parse("JUN").contains(6));
        assertTrue(parse("JUL").contains(7));
        assertTrue(parse("AUG").contains(8));
        assertTrue(parse("SEP").contains(9));
        assertTrue(parse("OCT").contains(10));
        assertTrue(parse("NOV").contains(11));
        assertTrue(parse("DEC").contains(12));
    }

    private DefaultField parse(String s) {
        return field = MonthField.parse(new Tokens(s));
    }
}
