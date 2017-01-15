package group22.travelstories;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vasin on 16/11/2016.
 */

public class TravelLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private final IBinder mBinder = new LocalBinder();

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LocationRequest mLocationRequest;
    private ArrayList<TimeLineEntry> timeLine;
    TimeLineEntry currentTimeLineEntry;

    public static SimpleTimeZone pdt;
    //private long thresholdDuration = 3 * 60 * 1000; // 3 minutes
    private long thresholdDuration = 5 * 1000; // 5 seconds

    private Timer mTimer = null;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        TravelLocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TravelLocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate(){
        System.out.println("service starts in service");
        super.onCreate();
    }

//    @Override
//    public void onCreate(){
//        super.onCreate();
//        // Create an instance of GoogleAPIClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//        createLocationRequest();
//        timeLine = new ArrayList<>();
//        // get the supported ids for GMT-08:00 (Pacific Standard Time)
//        String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
//        // if no ids were returned, something is wrong. get out.
//        if (ids.length == 0)
//            System.exit(0);
//        // create a Pacific Standard Time time zone
//        pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
//        // set up rules for Daylight Saving Time
//        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
//        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
//    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 5 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
//                .setInterval(60 * 1000)        // 60 seconds, in milliseconds
//                .setFastestInterval(30 * 1000); // 30 second, in milliseconds
    }

    public ArrayList<TimeLineEntry> getTimeLineList(){
        stopSelf();
        return this.timeLine;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent,flags,startId);
        System.out.println("TracvelLocaitonService starts!");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        timeLine = new ArrayList<>();
        // get the supported ids for GMT-08:00 (Pacific Standard Time)
        String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
        // if no ids were returned, something is wrong. get out.
        if (ids.length == 0)
            System.exit(0);
        // create a Pacific Standard Time time zone
        pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
        // set up rules for Daylight Saving Time
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

        MainActivity.startTime = new GregorianCalendar(pdt);

        mGoogleApiClient.connect();

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        System.out.println("TravelLocationService destroyed!");
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("googleApi on connected called");

        startLocationUpdates();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("PERMISSION CHECK fails at onConnected function");
            return;
        }
    }

    protected void startLocationUpdates() {
        System.out.println("starting location updates");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("location changed!");

        mLastLocation = location;

        //first time when the app starts -> startLocationUpdates called -> new location received
        // -> onLocationChanged triggered.
        if(currentTimeLineEntry == null) {
            GregorianCalendar currentTime = new GregorianCalendar(pdt);
            currentTimeLineEntry = new TimeLineEntry(mLastLocation, currentTime, currentTime);
            return;
        }

        GregorianCalendar currentTime = new GregorianCalendar(pdt);
        if(currentTimeLineEntry.nearLocation(mLastLocation)){
            System.out.println("it's near; time: " + currentTime.getTime());
            currentTimeLineEntry.updatesEndTime(currentTime);

        } else {
            System.out.println("it's far : duration "+ currentTimeLineEntry.getDuration());
            System.out.println("threshold duration " + thresholdDuration);
            if(currentTimeLineEntry.getDuration() > thresholdDuration){
                timeLine.add(currentTimeLineEntry);
                System.out.println("add, size" + timeLine.size());
            }
            currentTimeLineEntry = new TimeLineEntry(mLastLocation, currentTime, currentTime);
        }
        //check if location changed
    }


}
