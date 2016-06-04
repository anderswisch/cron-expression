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

import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class DateTimes {
        public static Iterable<Date> toDates(Iterable<ZonedDateTime> times) {
            return Iterables.transform(times, new Function<ZonedDateTime, Date>() {
                @Override
                public Date apply(ZonedDateTime input) {
                    return Date.from(input.toInstant());
                }
            });
        }

    public static ZonedDateTime midnight() {
        return now().truncatedTo(ChronoUnit.DAYS);
    }

    public static ZonedDateTime startOfHour() {
        return now().truncatedTo(ChronoUnit.HOURS);
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    public static ZonedDateTime lastOfMonth(ZonedDateTime t, DayOfWeek dayOfWeek) {
        ZonedDateTime day = t.with(TemporalAdjusters.lastDayOfMonth()).with(dayOfWeek);
        if (day.getMonth() != t.getMonth())
            day = day.minusWeeks(1);
        return day;
    }

    public static ZonedDateTime nthOfMonth(ZonedDateTime t, DayOfWeek dayOfWeek, int desiredNumber) {
        Month month = t.getMonth();
        t = t.withDayOfMonth(1).with(dayOfWeek);
        if (t.getMonth() != month)
            t = t.plusWeeks(1);
        int number = 1;
        while (number < desiredNumber && t.getMonth() == month) {
            number++;
            t = t.plusWeeks(1);
        }
        return t;
    }

    public static ZonedDateTime nearestWeekday(ZonedDateTime t) {
        if (t.getDayOfWeek() == DayOfWeek.SATURDAY)
            return t.minusDays(1);
        else if (t.getDayOfWeek() == DayOfWeek.SUNDAY)
            return t.plusDays(1);
        return t;
    }

    public static ZonedDateTime startOfYear() {
        return midnight().withDayOfYear(1);
    }
}
