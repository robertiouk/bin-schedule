package core.schedule;

import java.time.ZonedDateTime;

/**
 * Defines a class that provides bin schedules.
 */
public interface BinScheduleProvider {
    /**
     * Gets the current bin schedule for this week.
     * @param today today's date
     * @return the bin schedule for this week
     */
    BinSchedule getCurrentBinSchedule(final ZonedDateTime today);

    /**
     * Gets next weeks bin schedule
     * @param today today's date
     * @return the bin schedule for next week
     */
    BinSchedule getNextBinSchedule(final ZonedDateTime today);
}
