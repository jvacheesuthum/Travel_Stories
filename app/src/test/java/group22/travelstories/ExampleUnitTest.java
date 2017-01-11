package group22.travelstories;

import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    GregorianCalendar start1 = new GregorianCalendar(2017, 1, 9, 16, 29);
    GregorianCalendar end1 = new GregorianCalendar(2017, 1, 9, 16, 30);
    GregorianCalendar start2 = new GregorianCalendar(2017, 1, 9, 16, 31);
    GregorianCalendar end2 = new GregorianCalendar(2017, 1, 9, 16, 32);

    Long initStart = 1501542000000L; //Date : Jan 08, 2017

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void populateList_isCorrect() throws Exception {
        TimeLineEntry e1 = new TimeLineEntry(null, start1, end1);
        TimeLineEntry e2 = new TimeLineEntry(null, start2, end2);
        ArrayList<TimeLineEntry> timeline = new ArrayList<>();
        timeline.add(e1);
        timeline.add(e2);
        Helper populator = new Helper();

    }
}