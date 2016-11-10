package group22.travelstories;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by vasin on 01/11/2016.
 */

public class TimeLineEntry implements Serializable{

    ArrayList<Photo> photos;
    String locationName;
    transient Location location; //<------ temp fix
    GregorianCalendar start;
    GregorianCalendar end;


    public TimeLineEntry(Location l, GregorianCalendar start, GregorianCalendar end){
        location = l;
        this.start = start;
        this.end  = end;
        this.locationName = "default location";
    }

    public boolean nearLocation(Location location) {
        int radius = 2; // radius in 100 meters

        return ( Math.abs(location.getLongitude() - this.location.getLongitude()) < 0.001 * radius) &&
                ( Math.abs(location.getLatitude() - this.location.getLatitude()) < 0.001 * radius);
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
        return start.getTime().toString() + " - " + end.getTime().toString();
    }
}
