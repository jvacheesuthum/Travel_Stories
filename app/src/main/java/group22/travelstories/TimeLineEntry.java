package group22.travelstories;

import android.location.Location;
import android.os.Parcelable;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;


/**
 * Created by vasin on 01/11/2016.
 */

public class TimeLineEntry implements Serializable{

    ArrayList<Photo> photos;
    String locationName;
    transient Location location; //<------ temp fix
    GregorianCalendar start;
    GregorianCalendar end;

    BigInteger locationKey = new BigInteger("1");

    public TimeLineEntry(Location l, GregorianCalendar start, GregorianCalendar end){
        location = l;
        this.start = start;
        this.end  = end;;
    }

    public boolean nearLocation(Location location) {
        int radius = 2; // radius in 100 meters

        return ( Math.abs(location.getLongitude() - this.location.getLongitude()) < 0.00001 * radius) &&
                ( Math.abs(location.getLatitude() - this.location.getLatitude()) < 0.00001 * radius);
    }

    public void updatesEndTime(GregorianCalendar gregorianCalendar) {
        end = gregorianCalendar;
    }

    public long getDuration() {
        return end.getTimeInMillis() - start.getTimeInMillis();
    }


    public Location getLocation(){
        return location;
    }
    public void setAddress(String name){
        locationName = name;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getTime(){
//        return start.getTime().toString() + " - " + end.getTime().toString();

        Date s = start.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm EEE, dd MMM yyyy");
        Date e = end.getTime();
        return dateFormat.format(s) + " - " + dateFormat.format(e);

    }

    public List<String> getAllPhotoPath(){
        List<String> out = new ArrayList<>();
        for(Photo each : photos){
            out.add(each.getPath());
        }
        return out;
    }

    public ServerTimeLineEntry toServerTimeLineEntry(){
        return new ServerTimeLineEntry(null, locationName, locationKey, start, end);
    }

    public int getStartTime(){
        return (int) start.getTimeInMillis()/1000;
    }

    public void setPlaceId(String id) {
        locationKey = new BigInteger(id);
    }
}
