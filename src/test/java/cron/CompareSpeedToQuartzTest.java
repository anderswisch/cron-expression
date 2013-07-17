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

import com.google.common.base.Stopwatch;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class CompareSpeedToQuartzTest extends CompareBehaviorToQuartzTest {
    @Override
    protected void check(final Iterable<DateTime> times) throws ParseException {
        final Iterable<Date> dates = DateTimes.toDates(times);
        final CronExpression local = quartzLike.parse(string);
        final org.quartz.CronExpression quartz = new org.quartz.CronExpression(string);
        final int trials = 25;
        final Stopwatch clock = new Stopwatch().start();
        for (int i = 0; i < trials; i++)
            for (DateTime time : times)
                local.matches(time);
        final long localNano = clock.elapsed(TimeUnit.NANOSECONDS);
        clock.reset().start();
        for (int i = 0; i < trials; i++)
            for (Date date : dates)
                quartz.isSatisfiedBy(date);
        final long quartzNano = clock.elapsed(TimeUnit.NANOSECONDS);
        final boolean lessThanOrEqual = localNano <= quartzNano;
        System.out.printf(
                "%-80s %-60s local %8.2fms %6s Quartz %8.2fms\n",
                nameOfTestMethod(),
                string,
                localNano / 1000000d,
                (lessThanOrEqual ? "<=" : ">"),
                quartzNano / 1000000d
        );
        assertTrue(
                "We took longer for expression '" + string + "'; " + localNano + " > " + quartzNano,
                lessThanOrEqual
        );
    }

    private String nameOfTestMethod() {
        try {
            throw new Exception();
        } catch (Exception e) {
            String method = null;
            Iterator<StackTraceElement> trace = Arrays.asList(e.getStackTrace()).iterator();
            StackTraceElement element = trace.next();
            while (getClass().getName().equals(element.getClassName()))
                element = trace.next();
            String parentClassName = getClass().getSuperclass().getName();
            while (element.getClassName().equals(parentClassName)) {
                method = element.getMethodName();
                element = trace.next();
            }
            return method;
        }
    }
}
