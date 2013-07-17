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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Date;

public class DateTimes {
    public static Iterable<Date> toDates(Iterable<DateTime> times) {
        return Iterables.transform(times, new Function<DateTime, Date>() {
            @Override
            public Date apply(DateTime input) {
                return input.toDate();
            }
        });
    }

    public static DateTime midnight() {
        return now().withTimeAtStartOfDay();
    }

    public static DateTime startOfHour() {
        return now().withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
    }

    public static DateTime now() {
        return new DateTime();
    }

    public static DateTime lastOfMonth(DateTime t, int dayOfWeek) {
        DateTime friday = t.dayOfMonth().withMaximumValue().withDayOfWeek(dayOfWeek);
        if (friday.getMonthOfYear() != t.getMonthOfYear())
            friday = friday.minusWeeks(1);
        return friday;
    }

    public static DateTime nthOfMonth(DateTime t, int dayOfWeek, int desiredNumber) {
        int month = t.getMonthOfYear();
        t = t.withDayOfMonth(1).withDayOfWeek(dayOfWeek);
        if (t.getMonthOfYear() != month)
            t = t.plusWeeks(1);
        int number = 1;
        while (number < desiredNumber && t.getMonthOfYear() == month) {
            number++;
            t = t.plusWeeks(1);
        }
        return t;
    }

    public static DateTime nearestWeekday(DateTime t) {
        if (t.getDayOfWeek() == DateTimeConstants.SATURDAY)
            return t.minusDays(1);
        else if (t.getDayOfWeek() == DateTimeConstants.SUNDAY)
            return t.plusDays(1);
        return t;
    }

    public static DateTime startOfYear() {
        return midnight().withDayOfYear(1);
    }
}
