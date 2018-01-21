package core.schedule;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * A concrete implementation of a bin core.schedule provider.
 */
public class BinScheduleProviderImpl implements BinScheduleProvider {
    private final static DayOfWeek COLLECTION_DAY = DayOfWeek.TUESDAY;
    private final ZonedDateTime FIRST_RUBBISH = ZonedDateTime.of(2018, 1, 16, 23, 59, 59, 0, ZoneId.of("GMT"));
    private final ZonedDateTime FIRST_GARDEN = ZonedDateTime.of(2018, 3, 6, 23, 59, 59, 0, ZoneId.of("GMT"));

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
        final int dayOffset = pivotDate.getDayOfWeek().getValue() - COLLECTION_DAY.getValue();

        final ZonedDateTime collectionDate = pivotDate.minus(dayOffset, ChronoUnit.DAYS);

        ZonedDateTime calcDate = FIRST_RUBBISH;
        boolean isRubbish = true;
        boolean isRecycling = false;
        boolean isGarden = false;
        while (calcDate.isBefore(collectionDate)) {
            calcDate = calcDate.plus(1, ChronoUnit.WEEKS);

            isRubbish = !isRubbish;
            isRecycling = !isRecycling;
            if (calcDate.isAfter(FIRST_GARDEN) || calcDate.isEqual(FIRST_GARDEN)) {
                isGarden = !isGarden;
            }
        }
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
