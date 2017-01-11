package group22.travelstories;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by vasin on 17/11/2016.
 */

public class ServerTimeLineEntry implements Serializable{
    public final ArrayList<String> photos;
    public final String locationName;
    public final BigInteger location;
    public final GregorianCalendar start;
    public final GregorianCalendar end;
    public final double lng;
    public final double lat;


    public ServerTimeLineEntry(ArrayList<String> photos, String locationname, BigInteger location, GregorianCalendar start, GregorianCalendar end){
        this.photos = photos;
        this.locationName = locationname;
        this.location = location;
        this.start = start;
        this.end  = end;
        lng = 0;
        lat = 0;
    }

    public ServerTimeLineEntry(ArrayList<String> photos, String locationname, GregorianCalendar start, GregorianCalendar end, double lng, double lat){
        this.photos = photos;
        this.locationName = locationname;
        this.location = new BigInteger("0");
        this.start = start;
        this.end  = end;
        this.lng = lng;
        this.lat = lat;
    }
}


