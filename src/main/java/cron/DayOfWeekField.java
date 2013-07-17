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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.joda.time.DateTime;

import java.util.Set;

public class DayOfWeekField extends DefaultField {
    public static final long MILLISECONDS_PER_WEEK = 604800000l;
    private final Multimap<Integer, Integer> nth;
    private final Set<Integer> last;
    private final boolean hasNth, hasLast, unspecified;

    private DayOfWeekField(Builder b) {
        super(b);
        this.nth = b.nth.build();
        hasNth = !nth.isEmpty();
        this.last = b.last.build();
        hasLast = !last.isEmpty();
        unspecified = b.unspecified;
    }

    public boolean isUnspecified() {
        return unspecified;
    }

    public boolean matches(DateTime time) {
        if (unspecified)
            return true;
        final int dayOfWeek = time.getDayOfWeek();
        int number = number(dayOfWeek);
        if (hasLast) {
            return last.contains(number) && time.getMonthOfYear() != time.plusWeeks(1).getMonthOfYear();
        } else if (hasNth) {
            for (int possibleMatch : nth.get(number)) {
                DateTime midnight = time.withTimeAtStartOfDay();
                DateTime first = midnight.withDayOfMonth(1).withDayOfWeek(dayOfWeek);
                if (first.getMonthOfYear() != time.getMonthOfYear())
                    first = first.plusWeeks(1);
                DateTime tomorrow = midnight.plusDays(1);
                int weekNumber = 1 + (int) ((tomorrow.getMillis() - first.getMillis()) / MILLISECONDS_PER_WEEK);
                if (possibleMatch == weekNumber)
                    return true;
            }
        }
        return contains(number);
    }

    private int number(int dayOfWeek) {
        return dayOfWeek % 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DayOfWeekField that = (DayOfWeekField) o;
        if (hasLast != that.hasLast) return false;
        if (hasNth != that.hasNth) return false;
        if (!last.equals(that.last)) return false;
        if (!nth.equals(that.nth)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + nth.hashCode();
        result = 31 * result + last.hashCode();
        result = 31 * result + (hasNth ? 1 : 0);
        result = 31 * result + (hasLast ? 1 : 0);
        return result;
    }

    public static DayOfWeekField parse(Tokens s, boolean oneBased) {
        return new Builder(oneBased).parse(s).build();
    }

    public static class Builder extends DefaultField.Builder {
        protected static final Keywords KEYWORDS = new Keywords();

        static {
            KEYWORDS.put("SUN", 0);
            KEYWORDS.put("MON", 1);
            KEYWORDS.put("TUE", 2);
            KEYWORDS.put("WED", 3);
            KEYWORDS.put("THU", 4);
            KEYWORDS.put("FRI", 5);
            KEYWORDS.put("SAT", 6);
        }

        private boolean oneBased, unspecified;
        private final ImmutableSet.Builder<Integer> last;
        private final ImmutableMultimap.Builder<Integer, Integer> nth;

        public Builder(boolean oneBased) {
            super(0, 6);
            this.oneBased = oneBased;
            last = ImmutableSet.builder();
            nth = ImmutableMultimap.builder();
        }

        @Override
        protected Builder parse(Tokens tokens) {
            tokens.keywords(KEYWORDS);
            if (oneBased)
                tokens.offset(1);
            super.parse(tokens);
            tokens.reset();
            return this;
        }

        @Override
        protected boolean parseValue(Tokens tokens, Token token, int first, int last) {
            if (token == Token.MATCH_ONE) {
                unspecified = true;
                return false;
            } else {
                return super.parseValue(tokens, token, first, last);
            }
        }

        @Override
        protected boolean parseNumber(Tokens tokens, Token token, int first, int last) {
            if (token == Token.LAST) {
                this.last.add(first);
            } else if (token == Token.NTH) {
                int number = nextNumber(tokens);
                if (oneBased)
                    number += 1;
                nth.put(first, number);
            } else {
                return super.parseNumber(tokens, token, first, last);
            }
            return false;
        }

        @Override
        public DayOfWeekField build() {
            return new DayOfWeekField(this);
        }
    }
}
