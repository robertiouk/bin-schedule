package core.schedule;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BinScheduleProviderImplTest {
    @Test
    public void testGetCurrentSchedule()
    {
        final BinScheduleProviderImpl scheduleProvider = new BinScheduleProviderImpl();

        final ZonedDateTime firstBin = ZonedDateTime.of(2018, 1, 16, 9, 0, 0, 0, ZoneId.of("GMT"));
        final ZonedDateTime firstRecycling = ZonedDateTime.of(2018, 1, 23, 9, 0, 0, 0, ZoneId.of("GMT"));
        final ZonedDateTime firstGarden =  ZonedDateTime.of(2018, 3, 6, 9, 0, 0, 0, ZoneId.of("GMT"));

        // Test rubbish
        BinSchedule actual = scheduleProvider.getCurrentBinSchedule(firstBin);
        assertEquals(1, actual.getCollectionType().size());
        assertEquals(CollectionType.GeneralRubbish, actual.getCollectionType().stream().findFirst().get());

        actual = scheduleProvider.getCurrentBinSchedule(ZonedDateTime.of(2018, 6, 20, 9, 0, 0, 0, ZoneId.of("GMT")));
        assertEquals(1, actual.getCollectionType().size());
        assertEquals(CollectionType.GeneralRubbish, actual.getCollectionType().stream().findFirst().get());

        actual = scheduleProvider.getCurrentBinSchedule(ZonedDateTime.of(2018, 1, 20, 9, 0, 0, 0, ZoneId.of("GMT")));
        assertEquals(1, actual.getCollectionType().size());
        assertEquals(CollectionType.GeneralRubbish, actual.getCollectionType().stream().findFirst().get());

        // Test recycling only
        actual = scheduleProvider.getCurrentBinSchedule(firstRecycling);
        assertEquals(1, actual.getCollectionType().size());
        assertEquals(CollectionType.Recycling, actual.getCollectionType().stream().findFirst().get());

        actual = scheduleProvider.getCurrentBinSchedule(ZonedDateTime.of(2018, 2, 23, 9, 0, 0, 0, ZoneId.of("GMT")));
        assertEquals(1, actual.getCollectionType().size());
        assertEquals(CollectionType.Recycling, actual.getCollectionType().stream().findFirst().get());

        // Test recycling and garden
        actual = scheduleProvider.getCurrentBinSchedule(firstGarden);
        assertEquals(2, actual.getCollectionType().size());
        assertTrue(actual.getCollectionType().contains(CollectionType.Recycling));
        assertTrue(actual.getCollectionType().contains(CollectionType.Garden));

        actual = scheduleProvider.getCurrentBinSchedule(ZonedDateTime.of(2018, 10, 17, 9, 0, 0, 0, ZoneId.of("GMT")));
        assertEquals(2, actual.getCollectionType().size());
        assertTrue(actual.getCollectionType().contains(CollectionType.Recycling));
        assertTrue(actual.getCollectionType().contains(CollectionType.Garden));

        actual = scheduleProvider.getCurrentBinSchedule(ZonedDateTime.of(2018, 10, 17, 9, 0, 0, 0, ZoneId.of("GMT")));
        assertEquals(2, actual.getCollectionType().size());
        assertTrue(actual.getCollectionType().contains(CollectionType.Recycling));
        assertTrue(actual.getCollectionType().contains(CollectionType.Garden));
    }

    @Test
    public void testGetNextSchedule() {
        final BinScheduleProviderImpl scheduleProvider = new BinScheduleProviderImpl();

        BinSchedule actual = scheduleProvider.getNextBinSchedule(ZonedDateTime.of(2018, 1, 20, 21, 14, 30, 0, ZoneId.of("GMT")));
        assertEquals(1, actual.getCollectionType().size());
        assertEquals(CollectionType.Recycling, actual.getCollectionType().stream().findFirst().get());

        actual = scheduleProvider.getNextBinSchedule(ZonedDateTime.of(2018, 1, 21, 23, 59, 59, 0, ZoneId.of("GMT")));
        assertEquals(1, actual.getCollectionType().size());
        assertEquals(CollectionType.Recycling, actual.getCollectionType().stream().findFirst().get());

        actual = scheduleProvider.getNextBinSchedule(ZonedDateTime.of(2018, 3, 1, 9, 0, 0, 0, ZoneId.of("GMT")));
        assertEquals(2, actual.getCollectionType().size());
        assertTrue(actual.getCollectionType().contains(CollectionType.Recycling));
        assertTrue(actual.getCollectionType().contains(CollectionType.Garden));
    }
}
