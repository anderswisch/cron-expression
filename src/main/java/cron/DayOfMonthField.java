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

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class DayOfMonthField extends DefaultField {
    private final boolean lastDay, nearestWeekday, unspecified;

    private DayOfMonthField(Builder b) {
        super(b);
        lastDay = b.lastDay;
        nearestWeekday = b.nearestWeekday;
        unspecified = b.unspecified;
    }

    public boolean isUnspecified() {
        return unspecified;
    }

    public boolean matches(DateTime time) {
        if (unspecified)
            return true;
        final int dayOfMonth = time.getDayOfMonth();
        if (lastDay) {
            return dayOfMonth == time.dayOfMonth().withMaximumValue().getDayOfMonth();
        } else if (nearestWeekday) {
            int dayOfWeek = time.getDayOfWeek();
            if ((dayOfWeek == DateTimeConstants.MONDAY && contains(time.minusDays(1).getDayOfMonth()))
                    || (dayOfWeek == DateTimeConstants.FRIDAY && contains(time.plusDays(1).getDayOfMonth()))) {
                return true;
            }
        }
        return contains(dayOfMonth);
    }

    public static DayOfMonthField parse(Tokens s) {
        return new Builder().parse(s).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DayOfMonthField that = (DayOfMonthField) o;
        return lastDay == that.lastDay && nearestWeekday == that.nearestWeekday;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (lastDay ? 1 : 0);
        result = 31 * result + (nearestWeekday ? 1 : 0);
        return result;
    }

    public static class Builder extends DefaultField.Builder {
        private boolean lastDay, nearestWeekday, unspecified;

        public Builder() {
            super(1, 31);
        }

        @Override
        public DayOfMonthField build() {
            return new DayOfMonthField(this);
        }

        @Override
        protected Builder parse(Tokens tokens) {
            super.parse(tokens);
            return this;
        }

        @Override
        protected boolean parseValue(Tokens tokens, Token token, int first, int last) {
            if (token == Token.MATCH_ONE) {
                unspecified = true;
                return false;
            } else if (token == Token.LAST) {
                lastDay = true;
                return false;
            } else {
                return super.parseValue(tokens, token, first, last);
            }
        }

        @Override
        protected boolean parseNumber(Tokens tokens, Token token, int first, int last) {
            if (token == Token.WEEKDAY) {
                add(first);
                nearestWeekday = true;
                return false;
            } else {
                return super.parseNumber(tokens, token, first, last);
            }
        }
    }
}
