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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cron.DateTimes.toDates;
import static org.junit.Assert.assertTrue;

public class CompareBehaviorToQuartzTest {
    public static final CronExpression.Parser quartzLike = CronExpression.parser()
            .withSecondsField(true)
            .withOneBasedDayOfWeek(true)
            .allowBothDayFields(false);
    private static final String timeFormatString = "s m H d M E yyyy";
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(timeFormatString);
    private final DateFormat dateFormat = new SimpleDateFormat(timeFormatString);

    protected String string;
    private Times expected;

    @Before
    public void before() {
        expected = new Times();
    }

    @Test
    public void complex() throws Exception {
        string = "0/5 14,18,3-39,52 * ? JAN,MAR,SEP MON-FRI 2002-2010";
        expected.seconds.with(0);
        expected.minutes.with(52).withRange(3, 39);
        expected.months.with(1, 3, 9);
        expected.daysOfWeek.withRange(1, 5);
        expected.years.withRange(2002, 2010);
        check();
    }

    @Test
    public void at_noon_every_day() throws Exception {
        string = "0 0 12 * * ?";
        expected.seconds.with(0);
        expected.minutes.with(0);
        expected.hours.with(12);
        check();
    }

    @Test
    public void at_10_15am_every_day1() throws Exception {
        string = "0 15 10 ? * *";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        check();
    }

    @Test
    public void at_10_15am_every_day2() throws Exception {
        string = "0 15 10 * * ?";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        check();
    }

    @Test
    public void at_10_15am_every_day3() throws Exception {
        string = "0 15 10 * * ? *";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        check();
    }


    @Test
    public void at_10_15am_every_day_in_2005() throws Exception {
        string = "0 15 10 * * ? 2005";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        expected.years.with(2005);
        check();
    }

    @Test
    public void every_minute_of_2pm() throws Exception {
        string = "0 * 14 * * ?";
        expected.seconds.with(0);
        expected.hours.with(14);
        check();
    }

    @Test
    public void every_5_minutes_of_2pm() throws Exception {
        string = "0 0/5 14 * * ?";
        expected.seconds.with(0);
        expected.minutes.withRange(0, 59, 5);
        expected.hours.with(14);
        check();
    }

    @Test
    public void every_5_minutes_of_2pm_and_6pm() throws Exception {
        string = "0 0/5 14,18 * * ?";
        expected.seconds.with(0);
        expected.minutes.withRange(0, 59, 5);
        expected.hours.with(14, 18);
        check();
    }

    @Test
    public void first_5_minutes_of_2pm() throws Exception {
        string = "0 0-5 14 * * ?";
        expected.seconds.with(0);
        expected.minutes.withRange(0, 5);
        expected.hours.with(14);
        check();
    }

    @Test
    public void at_2_10pm_and_2_44pm_every_wednesday_in_march() throws Exception {
        string = "0 10,44 14 ? 3 WED";
        expected.seconds.with(0);
        expected.minutes.with(10, 44);
        expected.hours.with(14);
        expected.months.with(3);
        expected.daysOfWeek.with(3);
        check();
    }

    @Test
    public void at_10_15am_every_weekday() throws Exception {
        string = "0 15 10 ? * MON-FRI";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        expected.daysOfWeek.withRange(1, 5);
        check();
    }

    @Test
    public void at_10_15am_on_the_15th_of_every_month() throws Exception {
        string = "0 15 10 15 * ?";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        expected.daysOfMonth.with(15);
        check();
    }

    @Test
    public void at_10_15am_on_the_last_day_of_every_month() throws Exception {
        string = "0 15 10 L * ?";
        List<DateTime> times = new ArrayList<>();
        DateTime t = new DateTime().withDayOfYear(1).withTime(10, 15, 0, 0);
        int year = t.getYear();
        while (t.getYear() == year) {
            times.add(t.dayOfMonth().withMaximumValue());
            t = t.plusMonths(1);
        }
        check(times);
    }

    @Test
    public void at_10_15am_on_the_last_friday_of_every_month() throws Exception {
        string = "0 15 10 ? * 6L";
        List<DateTime> times = new ArrayList<>();
        DateTime t = new DateTime().withDayOfYear(1).withTime(10, 15, 0, 0);
        int year = t.getYear();
        while (t.getYear() == year) {
            times.add(DateTimes.lastOfMonth(t, DateTimeConstants.FRIDAY));
            t = t.plusMonths(1);
        }
        check(times);
    }

    @Test
    public void at_10_15am_on_the_last_friday_of_every_month_during_2002_through_2005() throws Exception {
        string = "0 15 10 ? * 6L 2002-2005";
        List<DateTime> times = new ArrayList<>();
        for (int year = 2002; year <= 2005; year++) {
            DateTime t = new DateTime().withYear(year).withDayOfYear(1).withTime(10, 15, 0, 0);
            while (t.getYear() == year) {
                times.add(DateTimes.lastOfMonth(t, DateTimeConstants.FRIDAY));
                t = t.plusMonths(1);
            }
        }
        check(times);
    }


    @Test@Ignore // TODO let's see if we can make this more reliably faster than the respective quartz run 
    public void at_10_15am_on_the_third_friday_of_every_month() throws Exception {
        string = "0 15 10 ? * 6#3";
        List<DateTime> times = new ArrayList<>();
        DateTime t = new DateTime().withDayOfYear(1).withTime(10, 15, 0, 0);
        int year = t.getYear();
        while (t.getYear() == year) {
            times.add(DateTimes.nthOfMonth(t, DateTimeConstants.FRIDAY, 3));
            t = t.plusMonths(1);
        }
        check(times);
    }

    @Test
    public void at_noon_every_5_days_every_month_starting_on_the_first_day_of_the_month() throws Exception {
        string = "0 0 12 1/5 * ?";
        expected.seconds.with(0);
        expected.minutes.with(0);
        expected.hours.with(12);
        expected.daysOfMonth.withRange(1, 31, 5);
        check();
    }

    @Test
    public void november_11th_at_11_11am() throws Exception {
        string = "0 11 11 11 11 ?";
        expected.seconds.with(0);
        expected.minutes.with(11);
        expected.hours.with(11);
        expected.daysOfMonth.with(11);
        expected.months.with(11);
        check();
    }

    private void check() throws ParseException {
        check(expected.dateTimes());
    }

    protected void check(Iterable<DateTime> times) throws ParseException {
        checkLocalImplementation(times);
        checkQuartzImplementation(toDates(times));
    }

    private void checkQuartzImplementation(Iterable<Date> times) throws ParseException {
        org.quartz.CronExpression quartz = new org.quartz.CronExpression(string);
        for (Date time : times)
            assertTrue(dateFormat.format(time).toUpperCase() + " doesn't match expression: " + string, quartz.isSatisfiedBy(time));
    }

    private void checkLocalImplementation(Iterable<DateTime> times) {
        CronExpression expr = quartzLike.parse(string);
        for (DateTime time : times)
            assertTrue(dateTimeFormat.print(time).toUpperCase() + " doesn't match expression: " + string, expr.matches(time));
    }
}
