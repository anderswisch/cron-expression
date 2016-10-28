# cron-expression

This project contains code to parse `cron` expressions and build corresponding Java objects. It provides a small
interface for checking whether a `cron` expression matches a `java.time.ZonedDateTime` object. It provides code for
integrating with `java.util.concurrent`, though using this is optional and it might be more straightforward to roll
your own.

It might come in handy if you're already using (or don't mind using) Java8, [Guava](http://code.google.com/p/guava-libraries/),
want to handle `cron` expressions, and don't want a full-blown scheduling library like [Quartz](http://quartz-scheduler.org).

## Motivation

* support POSIX cron syntax
    * day of week 0-6 (with 0=Sunday)
    * both day of week and day of month may be specified in a single expression
    * etc.
* support syntax of other common implementations
    * ranges, e.g. `1-10` which is equivalent to `1,2,3,4,5,6,7,8,9,10`
    * ranges with skipped numbers, e.g. `1-10/2` which is equivalent to `1,3,5,7,9`
    * wildcard ranges, e.g. `*/5`
    * aliases:

Alias     | Description                                                           | Equivalent
----------|-----------------------------------------------------------------------|-------------
@reboot   | Run at startup                                                        |
@yearly   | Run once a year at midnight in the morning of January 1               | `0 0 1 1 *`
@annually | (same as @yearly)                                                     |
@monthly  | Run once a month at midnight in the morning of the first of the month | `0 0 1 * *`
@weekly   | Run once a week at midnight in the morning of Sunday                  | `0 0 * * 0`
@daily    | Run once a day at midnight                                            | `0 0 * * *`
@midnight | (same as @daily)                                                      |
@hourly   | Run once an hour at the beginning of the hour                         | `0 * * * *`

* support [Quartz](http://quartz-scheduler.org) syntax
    * day of week 1-7 (with 1=Sunday)
    * either day of month or day of week but not both
    * nth day of week of month
    * last day of week of month
    * last day of month
    * nearest weekday of month
    * etc.
* support scenarios that [Quartz](http://quartz-scheduler.org) doesn't
    * multiple nth day of week of month, for example:
        * `0 0 * * 1#2,5#3` (at midnight on the second Monday and third Friday of every month)
    * multiple last day of week of month, for example:
        * `0 0 * * 1L,5L` (at midnight on the last Friday and last Monday of every month)
* minimal support for `java.util.concurrent`
* represent `cron` expressions as immutable objects
* be as fast or faster than other implementations, such as [Quartz](http://quartz-scheduler.org)
* use less memory than other implementations, such as [Quartz](http://quartz-scheduler.org)

## Requirements

* Java 8
* [Guava](http://code.google.com/p/guava-libraries/)

## Examples

### Normal

```java
ZonedDateTime time = ZonedDateTime.now().withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
assertTrue(CronExpression.parse("0 0 1 1 *").matches(time));
assertTrue(CronExpression.parse("@yearly").matches(time));
assertTrue(CronExpression.parse("@annually").matches(time));
assertTrue(CronExpression.yearly().matches(time));
```

### Quartz-like

```java
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
```

You can find more examples in the unit tests.
