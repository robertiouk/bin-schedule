package core.schedule;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * A concrete implementation of a bin core.schedule provider.
 */
public class BinScheduleProviderImpl implements BinScheduleProvider {
    private final static DayOfWeek COLLECTION_DAY = DayOfWeek.TUESDAY;
    private static final String POSTCODE = "mk438ht";
    private final Collection<ZonedDateTime> exceptionDates;
    private final ZonedDateTime firstRubbish;
    private final ZonedDateTime firstGarden;
    private final ZonedDateTime lastGarden;

    /**
     * Default Constructor.
     */
    public BinScheduleProviderImpl() {
        System.out.println("Get stuff from DB....");
        final ScheduleLoader scheduleLoader = new ScheduleLoaderImpl();
        exceptionDates = scheduleLoader.getScheduleExceptions(POSTCODE);
        firstRubbish = scheduleLoader.getCollectionEventDate(POSTCODE, CollectionEvent.FirstRubbish);
        firstGarden = scheduleLoader.getCollectionEventDate(POSTCODE, CollectionEvent.FirstGarden);
        lastGarden = scheduleLoader.getCollectionEventDate(POSTCODE, CollectionEvent.LastGarden);
    }

    @Override
    public BinSchedule getCurrentBinSchedule(final ZonedDateTime today) {
        return getScheduleForWeek(today);
    }

    @Override
    public BinSchedule getNextBinSchedule(final ZonedDateTime today) {
        final ZonedDateTime nextWeek = today.plus(1, ChronoUnit.WEEKS);
        final int mondayDifference = nextWeek.getDayOfWeek().getValue() - 1;

        return getScheduleForWeek(nextWeek.minus(mondayDifference, ChronoUnit.DAYS));
    }

    private BinSchedule getScheduleForWeek(final ZonedDateTime pivotDate) {
        System.out.println("Getting the schedule for week " + pivotDate);
        // Get the collection day for given week
        final TemporalField weekOfYear = WeekFields.of(Locale.UK).weekOfWeekBasedYear();
        final int givenWeek = pivotDate.get(weekOfYear);
        final DayOfWeek dayOfWeek = exceptionDates.stream()
                .filter(d -> d.get(weekOfYear) == givenWeek)
                .findAny()
                .map(ZonedDateTime::getDayOfWeek)
                .orElse(COLLECTION_DAY);

        // Adjust given date to be the collection date for that week
        final int dayOffset = pivotDate.getDayOfWeek().getValue() - dayOfWeek.getValue();
        final ZonedDateTime collectionDate = pivotDate.minus(dayOffset, ChronoUnit.DAYS);

        boolean isRubbish = givenWeek % 2 == firstRubbish.get(weekOfYear) % 2;
        boolean isRecycling = !isRubbish;
        boolean isGarden = !isRubbish &&
                collectionDate.getDayOfYear() >= firstGarden.getDayOfYear() &&
                collectionDate.getDayOfYear() <= lastGarden.getDayOfYear();

        final Set<CollectionType> collectionTypes = new HashSet<>();
        if (isRubbish) {
            collectionTypes.add(CollectionType.GeneralRubbish);
        }
        if (isRecycling) {
            collectionTypes.add(CollectionType.Recycling);
        }
        if (isGarden) {
            collectionTypes.add(CollectionType.Garden);
        }

        return new BinSchedule() {
            /** {@inheritDoc} */
            @Override
            public Collection<CollectionType> getCollectionType() {
                return collectionTypes;
            }
            /** {@inheritDoc} */
            @Override
            public ZonedDateTime getCollectionDate() {
                return collectionDate;
            }
        };
    }
}
