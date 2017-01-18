package group22.travelstories;

import android.location.Location;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Time;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LocalUnitTest {

    Location l = Mockito.mock(Location.class);


    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void calculateDistance1_isCorrect() throws Exception {
        Helper helper = new Helper();
        double originLongitude = 51.507326;
        double originLatitude = -0.127746;

        double desLongitude = 46.227118;
        double desLatitude = 2.213583;

//        l.setLatitude(desLatitude);
//        l.setLongitude(desLongitude);
        TimeLineEntry e = new TimeLineEntry(l, null, null);
        Photo p = new Photo(null, null, originLatitude,  originLongitude);

        when(l.getLongitude()).thenReturn(desLongitude);
        when(l.getLatitude()).thenReturn(desLatitude);

        double distance = helper.calculateDistance(p, e);

        System.out.println("Distance: " + distance);
        assertEquals(642.1, distance, 0.1);
    }

    @Test
    public void calculateDistance2_isCorrect() throws Exception {
        Helper helper = new Helper();
        double originLongitude = 114.109458;
        double originLatitude = -22.396463;
        double desLongitude = 126.990204;
        double desLatitude = 37.552014;

//        l.setLatitude(desLatitude);
//        l.setLongitude(desLongitude);
        TimeLineEntry e = new TimeLineEntry(l, null, null);
        Photo p = new Photo(null, null, originLatitude,  originLongitude);

        when(l.getLongitude()).thenReturn(desLongitude);
        when(l.getLatitude()).thenReturn(desLatitude);

        double distance = helper.calculateDistance(p, e);

        System.out.println("Distance: " + distance);
        assertEquals(6801, distance, 0.1);
    }

    @Test
    public void nearLocation_isCorrect() throws Exception {
        double originLongitude = 114.109458;
        double originLatitude = -22.396463;
        double desLongitude = 114.109440;
        double desLatitude = -22.396463;
        TimeLineEntry e = new TimeLineEntry(l, null, null);
        Location l2 = Mockito.mock(Location.class);

        when(l.getLongitude()).thenReturn(desLongitude);
        when(l.getLatitude()).thenReturn(desLatitude);
        when(l2.getLongitude()).thenReturn(originLongitude);
        when(l2.getLatitude()).thenReturn(originLatitude);

        System.out.println("L Lat: " + l.getLatitude());
        System.out.println("L Long: " + l.getLongitude());
        System.out.println("L2 Lat: " + l2.getLatitude());
        System.out.println("L2 Long: " + l2.getLongitude());

        boolean b = e.nearLocation(l2);

        assertTrue(b);
    }

    @Test
    public void nearLocation2_isCorrect() throws Exception {
        double originLongitude = 114.109458;
        double originLatitude = -22.396463;
        double desLongitude = 126.990204;
        double desLatitude = 37.552014;
        TimeLineEntry e = new TimeLineEntry(l, null, null);
        Location l2 = Mockito.mock(Location.class);

        when(l.getLongitude()).thenReturn(desLongitude);
        when(l.getLatitude()).thenReturn(desLatitude);
        when(l2.getLongitude()).thenReturn(originLongitude);
        when(l2.getLatitude()).thenReturn(originLatitude);
        boolean b = e.nearLocation(l2);

        assertFalse(b);
    }

}