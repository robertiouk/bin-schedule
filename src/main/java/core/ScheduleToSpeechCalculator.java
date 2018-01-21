package core;

import core.schedule.BinSchedule;

import java.time.ZonedDateTime;

/**
 * Defines a class that converts the speech text for a bin schedule
 * for a given day.
 */
public interface ScheduleToSpeechCalculator {
    /**
     * Get the bin schedule text.
     * @param today today's date
     * @return the schedule in speech format
     */
    String getBinScheduleText(ZonedDateTime today);

    /**
     * Get the bin schedule text for next week.
     * @param today today's date
     * @return the schedule in speech format
     */
    String getNextBinScheduleText(ZonedDateTime today);
}
