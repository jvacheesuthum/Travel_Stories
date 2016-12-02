package group22.travelstories;

import java.math.BigInteger;

/**
 * Created by vasin on 17/11/2016.
 */

public class Place {
    private BigInteger key;
    private String name;
    private double latitude;
    private double longitude;
    private int popularity;

    public String toString(){
        return "key:" + key + " name:" + name + " popularity:" + popularity;
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return latitude + ", " + longitude;
    }
}
