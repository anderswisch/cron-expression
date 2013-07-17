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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class CronScheduleTest {
    private CronSchedule schedule;
    private ScheduledExecutorService executor;

    @Before
    public void before() {
        executor = Executors.newScheduledThreadPool(1);
        schedule = new CronSchedule(executor);
    }

    @Test
    public void runThroughExecutor() throws Exception {
        schedule = new CronSchedule(executor, true);
        final AtomicBoolean run = new AtomicBoolean(false);
        schedule.add(CronExpression.parser().withSecondsField(true).parse("* * * * * *"), new Runnable() {
            @Override
            public void run() {
                run.set(true);
            }
        });
        assertFalse(run.get());
        schedule.start();
        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        assertTrue(run.get());
    }

    @Test
    public void removeOne() throws Exception {
        final Multiset<String> counts = HashMultiset.create();
        Runnable a = new Runnable() {
            @Override
            public void run() {
                counts.add("a");
            }
        };
        Runnable b = new Runnable() {
            @Override
            public void run() {
                counts.add("b");
            }
        };
        CronExpression expression = CronExpression.parse("* * * * *");
        schedule.add(expression, a);
        schedule.add(expression, b);
        runAndWait();
        assertEquals(1, counts.count("a"));
        assertEquals(1, counts.count("b"));
        schedule.remove(expression, a);
        runAndWait();
        assertEquals(1, counts.count("a"));
        assertEquals(2, counts.count("b"));
    }

    @Test
    public void removeAllForExpression() throws Exception {
        final Multiset<String> counts = HashMultiset.create();
        Runnable a = new Runnable() {
            @Override
            public void run() {
                counts.add("a");
            }
        };
        Runnable b = new Runnable() {
            @Override
            public void run() {
                counts.add("b");
            }
        };
        CronExpression expression = CronExpression.parse("* * * * *");
        schedule.add(expression, a);
        schedule.add(expression, b);
        runAndWait();
        assertEquals(1, counts.count("a"));
        assertEquals(1, counts.count("b"));
        schedule.remove(expression);
        runAndWait();
        assertEquals(1, counts.count("a"));
        assertEquals(1, counts.count("b"));
    }

    @After
    public void after() {
        if (schedule != null)
            schedule.stop();
        if (executor != null)
            executor.shutdownNow();
    }

    private void runAndWait() throws InterruptedException {
        schedule.run();
        Thread.sleep(10);
    }
}
