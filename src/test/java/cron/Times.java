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

import com.google.common.collect.ImmutableSortedSet;
import org.joda.time.DateTime;

import java.util.Iterator;
import java.util.NavigableSet;

public class Times {
    public final Integers
            seconds,
            minutes,
            hours,
            months,
            daysOfWeek,
            years,
            daysOfMonth;

    public Times() {
        seconds = new Integers();
        minutes = new Integers();
        hours = new Integers();
        months = new Integers();
        daysOfWeek = new Integers();
        years = new Integers();
        daysOfMonth = new Integers();
    }

    public NavigableSet<DateTime> dateTimes() {
        if (seconds.isEmpty())
            seconds.withRange(0, 1);
        if (minutes.isEmpty())
            minutes.withRange(0, 1);
        if (hours.isEmpty())
            hours.withRange(0, 1);
        if (months.isEmpty())
            months.withRange(1, 2);
        if (years.isEmpty()) {
            int thisYear = new DateTime().getYear();
            years.withRange(thisYear, thisYear + 1);
        }
        ImmutableSortedSet.Builder<DateTime> builder = ImmutableSortedSet.naturalOrder();
        for (int second : seconds) {
            for (int minute : minutes) {
                for (int hour : hours) {
                    for (int month : months) {
                        for (int year : years) {
                            DateTime base = new DateTime()
                                    .withTimeAtStartOfDay()
                                    .withSecondOfMinute(second)
                                    .withMinuteOfHour(minute)
                                    .withHourOfDay(hour)
                                    .withMonthOfYear(month)
                                    .withDayOfMonth(1)
                                    .withYear(year);
                            if (!daysOfWeek.isEmpty() && !daysOfMonth.isEmpty()) {
                                addDaysOfWeek(builder, base);
                                addDaysOfMonth(builder, base);
                            } else if (!daysOfWeek.isEmpty()) {
                                addDaysOfWeek(builder, base);
                            } else if (!daysOfMonth.isEmpty()) {
                                addDaysOfMonth(builder, base);
                            } else {
                                builder.add(base);
                            }
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    private void addDaysOfWeek(ImmutableSortedSet.Builder<DateTime> builder, DateTime base) {
        int month = base.getMonthOfYear();
        Iterator<Integer> iterator = daysOfWeek.iterator();
        base = base.withDayOfWeek(iterator.next());
        if (base.getMonthOfYear() != month)
            base = base.plusWeeks(1);
        do {
            builder.add(base);
            base = base.plusWeeks(1);
        } while (base.getMonthOfYear() == month);
    }

    private void addDaysOfMonth(ImmutableSortedSet.Builder<DateTime> builder, DateTime base) {
        for (int day : daysOfMonth)
            if (day <= base.dayOfMonth().getMaximumValue())
                builder.add(base.withDayOfMonth(day));
    }
}
