package core;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

public class BinDaySpeechletTest {
    @Test
    public void testGetBinScheduleText() {
        final BinDaySpeechlet speechlet = new BinDaySpeechlet();

        String expected = "This weeks collection was on Tuesday and was General Rubbish";
        assertEquals(expected, speechlet.getBinScheduleText(ZonedDateTime.of(2018, 1, 20, 20, 42, 30, 0, ZoneId.of("GMT"))));

        expected = "This weeks collection will be on Tuesday and will be General Rubbish";
        assertEquals(expected, speechlet.getBinScheduleText(ZonedDateTime.of(2018, 1, 15, 20, 42, 30, 0, ZoneId.of("GMT"))));

        expected = "This weeks collection was on Tuesday and was Recycling and Garden";
        assertEquals(expected, speechlet.getBinScheduleText(ZonedDateTime.of(2018, 10, 17, 9, 0, 0, 0, ZoneId.of("GMT"))));
    }

    @Test
    public void testGetNextBinScheduleText() {
        final BinDaySpeechlet speechlet = new BinDaySpeechlet();

        String expected = "Next weeks collection will be on Tuesday and will be Recycling";
        assertEquals(expected, speechlet.getNextBinScheduleText(ZonedDateTime.of(2018, 1, 20, 20, 42, 30, 0, ZoneId.of("GMT"))));
    }
}
