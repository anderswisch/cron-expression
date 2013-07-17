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
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static cron.DateTimes.nthOfMonth;
import static org.junit.Assert.*;

public class WhatQuartzDoesNotSupport {
    @Test
    public void multipleNthDayOfWeek() throws Exception {
        try {
            org.quartz.CronExpression quartz = new org.quartz.CronExpression("0 0 0 ? * 6#3,4#1,3#2");
            List<DateTime> times = new ArrayList<>();
            DateTime t = new DateTime().withDayOfYear(1).withTimeAtStartOfDay();
            int year = t.getYear();
            while (t.getYear() == year) {
                times.add(nthOfMonth(t, DateTimeConstants.FRIDAY, 3));
                times.add(nthOfMonth(t, DateTimeConstants.TUESDAY, 2));
                t = t.plusMonths(1);
            }
            for (DateTime time : times) {
                boolean satisfied = quartz.isSatisfiedBy(time.toDate());
                if (time.getDayOfWeek() == DateTimeConstants.TUESDAY) {
                    // Earlier versions of Quartz only picked up the last one
                    assertTrue(satisfied);
                } else {
                    assertFalse(satisfied);
                }
            }
        } catch (ParseException e) {
            assertEquals("Support for specifying multiple \"nth\" days is not imlemented.", e.getMessage());
        }
    }

    @Test
    public void multipleLastDayOfWeek() throws Exception {
        try {
            new org.quartz.CronExpression("0 0 0 ? * 6L,4L,3L");
            fail("Expected exception");
        } catch (ParseException e) {
            assertEquals("Support for specifying 'L' with other days of the week is not implemented", e.getMessage());
        }
    }
}
