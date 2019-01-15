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
    private final static ZonedDateTime FIRST_RUBBISH = ZonedDateTime.of(2018, 1, 16, 23, 59, 59, 0, ZoneId.of("GMT"));
    private final static ZonedDateTime FIRST_GARDEN = ZonedDateTime.of(2019, 3, 5, 23, 59, 59, 0, ZoneId.of("GMT"));
    private final static ZonedDateTime LAST_GARDEN = ZonedDateTime.of(2018, 11, 27, 23, 59, 59, 0, ZoneId.of("GMT"));
    private final static Set<ZonedDateTime> EXCEPTION_DATES = new HashSet<>();
    static {
        EXCEPTION_DATES.add(ZonedDateTime.of(2018, 12, 27, 23, 59, 59, 0, ZoneId.of("GMT")));
        EXCEPTION_DATES.add(ZonedDateTime.of(2019, 1, 3, 23, 59, 59, 0, ZoneId.of("GMT")));
        EXCEPTION_DATES.add(ZonedDateTime.of(2019, 1, 9, 23, 59, 59, 0, ZoneId.of("GMT")));
    }

    /** {@inheritDoc} */
    @Override
    public BinSchedule getCurrentBinSchedule(final ZonedDateTime today) {
        return getScheduleForWeek(today);
    }

    /** {@inheritDoc} */
    @Override
    public BinSchedule getNextBinSchedule(final ZonedDateTime today) {
        final ZonedDateTime nextWeek = today.plus(1, ChronoUnit.WEEKS);
        final int mondayDifference = nextWeek.getDayOfWeek().getValue() - 1;

        return getScheduleForWeek(nextWeek.minus(mondayDifference, ChronoUnit.DAYS));
    }

    private BinSchedule getScheduleForWeek(final ZonedDateTime pivotDate) {
        // Get the collection day for given week
        final TemporalField weekOfYear = WeekFields.of(Locale.ENGLISH).weekOfWeekBasedYear();
        final int givenWeek = pivotDate.get(weekOfYear);
        final DayOfWeek dayOfWeek = EXCEPTION_DATES.stream()
                .filter(d -> d.get(weekOfYear) == givenWeek)
                .findAny()
                .map(ZonedDateTime::getDayOfWeek)
                .orElse(COLLECTION_DAY);

        // Adjust given date to be the collection date for that week
        final int dayOffset = pivotDate.getDayOfWeek().getValue() - dayOfWeek.getValue();
        final ZonedDateTime collectionDate = pivotDate.minus(dayOffset, ChronoUnit.DAYS);

        boolean isRubbish = givenWeek % 2 == FIRST_RUBBISH.get(weekOfYear) % 2;
        boolean isRecycling = !isRubbish;
        boolean isGarden = !isRubbish &&
                collectionDate.getDayOfYear() >= FIRST_GARDEN.getDayOfYear() &&
                collectionDate.getDayOfYear() <= LAST_GARDEN.getDayOfYear();

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
