package core.schedule;

import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * Defines the data held for a bin core.schedule.
 */
public interface BinSchedule {
    /**
     * Get the core.schedule bin collection types.
     * @return the collection types
     */
    Collection<CollectionType> getCollectionType();

    /**
     * Get the core.schedule collection date.
     * @return the core.schedule collection date
     */
    ZonedDateTime getCollectionDate();
}
