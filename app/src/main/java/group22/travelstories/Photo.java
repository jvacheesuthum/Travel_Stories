package group22.travelstories;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Felix on 03/11/2016.
 */

public class Photo implements Serializable{
    String path;
    Date date;
    Double latitude;
    Double longitude;

    public Photo(String path, Date date, Double latitude, Double longitude) {
        this.path = path;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Path: " + path + "\nDate: " + date.toString() + "\nLat: " + latitude + "\nLong: " + longitude;
    }

}
