package group22.travelstories;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by vasin on 17/11/2016.
 */

public class ServerTimeLineEntry implements Serializable{
    public final ArrayList<String> photos;
    public final String locationName;
    public final BigInteger location;
    public final String start;
    public final String end;


    public ServerTimeLineEntry(ArrayList<String> photos, String locationname, BigInteger location, String start, String end){
        this.photos = photos;
        this.locationName = locationname;
        this.location = location;
        this.start = start;
        this.end  = end;
    }
}


