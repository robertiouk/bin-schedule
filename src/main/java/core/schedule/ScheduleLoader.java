package core.schedule;

import com.amazonaws.AmazonServiceException;

import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * Defines a class that provides a specific bin schedule.
 */
public interface ScheduleLoader {
    /**
     * Get the collection event date for a given post code and event type.
     * @param postcode the post code for the bin schedule
     * @param collectionEvent the collection event type
     * @return the collection event date
     * @throws AmazonServiceException thrown if an error occurs retrieving data from database
     */
    ZonedDateTime getCollectionEventDate(String postcode, CollectionEvent collectionEvent) throws AmazonServiceException;

    /**
     * Get the schedule exception dates.
     * @param postcode the post code for the bin schedule
     * @return the schedule exception dates.
     * @throws AmazonServiceException thrown if an error occurs retrieving data from database
     */
    Collection<ZonedDateTime> getScheduleExceptions(String postcode) throws AmazonServiceException;
}
