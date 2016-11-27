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


    public ServerTimeLineEntry(ArrayList<String> photos, String locationname, BigInteger location, GregorianCalendar start, GregorianCalendar end){
        this.photos = photos;
        this.locationName = locationname;
        this.location = location;
        this.start = start;
        this.end  = end;
    }
}


