package cron;

import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

/**
 * Examples used in the Readme file.
 */
public class ReadmeTest {

    @Test
    public void normal() {
        ZonedDateTime time = ZonedDateTime.now().withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
        assertTrue(CronExpression.parse("0 0 1 1 *").matches(time));
        assertTrue(CronExpression.parse("@yearly").matches(time));
        assertTrue(CronExpression.parse("@annually").matches(time));
        assertTrue(CronExpression.yearly().matches(time));
    }
    
    @Test
    public void quartzLike() {
        CronExpression expression = CronExpression.parser()
            .withSecondsField(true)
            .withOneBasedDayOfWeek(true)
            .allowBothDayFields(false)
            .parse("0 15 10 L * ?");
        ZonedDateTime time = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            .withYear(2013)
            .withMonth(1)
            .withDayOfMonth(31)
            .withHour(10)
            .withMinute(15);
        assertTrue(expression.matches(time));
    }

}
