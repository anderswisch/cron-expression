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

public class MonthField extends DefaultField {
    protected MonthField(Builder b) {
        super(b);
    }

    public static MonthField parse(Tokens s) {
        return new Builder().parse(s).build();
    }

    public static class Builder extends DefaultField.Builder {
        protected static final Keywords KEYWORDS = new Keywords();

        static {
            KEYWORDS.put("JAN", 1);
            KEYWORDS.put("FEB", 2);
            KEYWORDS.put("MAR", 3);
            KEYWORDS.put("APR", 4);
            KEYWORDS.put("MAY", 5);
            KEYWORDS.put("JUN", 6);
            KEYWORDS.put("JUL", 7);
            KEYWORDS.put("AUG", 8);
            KEYWORDS.put("SEP", 9);
            KEYWORDS.put("OCT", 10);
            KEYWORDS.put("NOV", 11);
            KEYWORDS.put("DEC", 12);
        }

        public Builder() {
            super(1, 12);
        }

        @Override
        protected Builder parse(Tokens tokens) {
            tokens.keywords(KEYWORDS);
            super.parse(tokens);
            tokens.reset();
            return this;
        }

        @Override
        public MonthField build() {
            return new MonthField(this);
        }
    }
}
