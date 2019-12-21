package core.schedule;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.*;

public class ScheduleLoaderImplTest {
    private ScheduleLoaderImpl loader;

    @Before
    public void setUp() {
        loader = new ScheduleLoaderImpl();
    }

    @Test
    public void getCollectionEventDate() {
        final ZonedDateTime firstBinDate = loader.getCollectionEventDate("test",
                CollectionEvent.FirstRubbish);
        final ZonedDateTime firstGardenDate = loader.getCollectionEventDate("test",
                CollectionEvent.FirstGarden);
        final ZonedDateTime lastGardenDate = loader.getCollectionEventDate("test",
                CollectionEvent.LastGarden);

        final ZonedDateTime expectedFirstBinDate = ZonedDateTime.of(1983, 8, 25, 23,
                59, 59, 0, ZoneId.of("Z"));
        final ZonedDateTime expectedFirstGardenDate = ZonedDateTime.of(1949, 5, 1, 23,
                59, 59, 0, ZoneId.of("Z"));
        final ZonedDateTime expectedLastGardenDate = ZonedDateTime.of(1982, 2, 10, 23,
                59, 59, 0, ZoneId.of("Z"));

        assertEquals(expectedFirstBinDate, firstBinDate);
        assertEquals(expectedFirstGardenDate, firstGardenDate);
        assertEquals(expectedLastGardenDate, lastGardenDate);
    }

    @Test
    public void getScheduleExceptions() {
        final Collection<ZonedDateTime> actual = loader.getScheduleExceptions("test");

        assertEquals(2, actual.size());

        final ZonedDateTime expected1 = ZonedDateTime.of(1980, 9, 3, 23,
                59, 59, 0, ZoneId.of("Z"));
        final ZonedDateTime expected2 = ZonedDateTime.of(1955, 5, 7, 23,
                59, 59, 0, ZoneId.of("Z"));
        assertTrue(actual.contains(expected1));
        assertTrue(actual.contains(expected2));
    }
}