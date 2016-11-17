package group22.travelstories;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static int RESULT_LOAD_IMAGE = 1;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = true;
    private ArrayList<TimeLineEntry> timeLine;
    TimeLineEntry currentTimeLineEntry;
    public final static String EXTRA_MESSAGE = "com.travelstories.timeline"; //dodgy restrictions

    Client TravelServerWSClient;

    // create a Pacific Standard Time time zone
    SimpleTimeZone pdt;

    private long thresholdDuration = 10 * 1000; // 5 minutes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // Create an instance of GoogleAPIClient.
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

    }

    private Photo getPhoto(Cursor cursor, int dateColumn) {
        int path = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
        int latitude = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LATITUDE);
        int longitude = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LONGITUDE);

        Long date = cursor.getLong(dateColumn);
        Date d = new Date(date);
        String p = cursor.getString(path);
        Double lat = cursor.getDouble(latitude);
        Double longi = cursor.getDouble(longitude);
        Photo photo = new Photo(p, d, lat, longi);
        return photo;
    }

    private Double calculateDistance(Photo photo, TimeLineEntry t) {
        Double photoLong = photo.longitude;
        Double photoLat = photo.latitude;
        Double entryLong = t.location.getLongitude();
        Double entryLat = t.location.getLatitude();

        int radius = 6371;
        //Haversine formula:
        // a = sin^2(latDiff/2) + cos(lat1)cos(lat2)sin^2(LongDiff/2)
        // c = 2 * theta of (x, y) converted to polar (r, theta)
        // distance = radius of sphere * c
        Double sinDistanceLong = Math.sin(Math.toRadians(entryLong - photoLong)/2);
        Double sinDistanceLat = Math.sin(Math.toRadians(entryLat - photoLat)/2);
        Double a = Math.pow(sinDistanceLat, 2) + Math.cos(Math.toRadians(photoLat))
                * Math.cos(Math.toRadians(entryLat)) * Math.pow(sinDistanceLong, 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = radius * c;

        return distance;
    }

    private void populateList() {
        if (timeLine.isEmpty()) return;
        String[] projection = {MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE,
                MediaStore.Images.ImageColumns.DATE_TAKEN};
        String selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " > ? AND " +
                MediaStore.Images.ImageColumns.DATE_TAKEN + " < ?";
        Long start = timeLine.get(0).start.getTimeInMillis();
        Long end = timeLine.get(timeLine.size() - 1).end.getTimeInMillis();
        String[] selectionArgs = {start.toString(), end.toString()};
        final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        cursor.moveToFirst();
        int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);

        int index = 0;
        TimeLineEntry prevEntry = null;
        TimeLineEntry currEntry = timeLine.get(index);


        do {
            ArrayList<Photo> photos = new ArrayList<>();
            currEntry = timeLine.get(index);
            if (prevEntry != null) {
                start = end;
                end = currEntry.start.getTimeInMillis();
                while (cursor.getLong(dateColumn) < end) {
                    Photo photo = getPhoto(cursor, dateColumn);
                    if (calculateDistance(photo, prevEntry) >
                            calculateDistance(photo, currEntry)) {
                        photos.add(photo);
                    } else {
                        prevEntry.photos.add(photo);
                    }
                    cursor.moveToNext();
                }
            }
            start = currEntry.start.getTimeInMillis();
            end = currEntry.end.getTimeInMillis();

            while (cursor.getLong(dateColumn) <= end) {
                photos.add(getPhoto(cursor, dateColumn));
                if (!cursor.moveToNext()) {
                    break;
                }
            }
            prevEntry = currEntry;
            index++;
        } while (cursor.moveToNext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            ImageView imageView = (ImageView) findViewById(R.id.imgView);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("googleApi on connected called");

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            System.out.println("---play service---");
//            System.out.println(String.valueOf(mLastLocation.getLatitude()));
//            System.out.println(String.valueOf(mLastLocation.getLongitude()));
//            System.out.println("---play service---");
////            GregorianCalendar currentTime = new GregorianCalendar(pdt);
////            currentTimeLineEntry = new TimeLineEntry(mLastLocation, currentTime, currentTime);
//        }
    }

    public void addLocationToInfoLayout(String message) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.info);
        TextView valueTV = new TextView(MainActivity.this);
        valueTV.setText("[" + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude() + "]" + message);
        valueTV.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.FILL_PARENT,
                Toolbar.LayoutParams.WRAP_CONTENT));

        ((LinearLayout) linearLayout).addView(valueTV);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        System.out.println("on start called");
        super.onStart();

        try {
            //TravelServerWSClient = new Client("localhost:1080");
            TravelServerWSClient = new Client("http://cloud-vm-46-251.doc.ic.ac.uk:1080", new SeeSummary(timeLine,this));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        try {
//            TravelServerWSClient.connectBlocking();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        mGoogleApiClient.connect();
        final ToggleButton trackToggle = (ToggleButton) findViewById(R.id.trackToggle);
        trackToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //toggle enabled - starts tracking
                    Hi x = new Hi();
                    x.say();
                    addLocationToInfoLayout("Most recent location");
                } else {
                    System.out.println("stops tracking");//toggle disabled - stops tracking
                    sendTimeLineLocation(TravelServerWSClient);
                    trackToggle.setText("See summary");
                    //seeSummary();
                }
            }
        });

    }

    private void startHiService(){
        startService(new Intent(this, HiService.class));
    }

    protected void onStop() {
        super.onStop();
        System.out.println("on stop called");
//        try {
//            TravelServerWSClient.closeBlocking();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        mGoogleApiClient.disconnect(); //do we remove this line to keep location updates after exitting app?
        startHiService();
    }

    @Override
    protected void onRestart(){
        System.out.println("on restart called");
        super.onRestart();
    }

    @Override
    protected void onDestroy(){
        System.out.println("on destroy called");
        super.onDestroy();
    }


    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    protected void startLocationUpdates() {
        System.out.println("starting location updates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void onLocationChanged(Location location) {
        System.out.println("location changed!");

        mLastLocation = location;
        addLocationToInfoLayout("no click");

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



    public void sendTimeLineLocation(Client wsc) {
        if (timeLine.isEmpty()) {
            //real thing
            /*
            System.out.println("timeline is empty");
            return;
            */
            // test thing
            System.out.println("timeline is empty");
            System.out.println("populating timeline list ...");
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));

            tempPopulateList();
            wsc.send("timeline_address:-0.126957,51.5194133");
            return;
        }
        String request = "timeline_address:";

        for (TimeLineEntry each : timeLine) {
            Location eachLocation = each.getLocation();
            request += eachLocation.getLatitude() + "," + eachLocation.getLongitude() + "@";
        }
//        request += "-0.1269566,51.5194133";
        System.out.println("request message is:*" + request + "*");
        wsc.send(request);
    }


    private void tempPopulateList() {
        if (timeLine.isEmpty()) return;
        String[] projection = {MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE,
                MediaStore.Images.ImageColumns.DATE_TAKEN};
        final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
        cursor.moveToFirst();
        int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);

        int index = 0;

        for (TimeLineEntry e : timeLine) {
            e.photos = new ArrayList<>();
        }

        do {
            System.out.println("Index: " + index);
            TimeLineEntry e = timeLine.get(index);
            Photo photo = getPhoto(cursor, dateColumn);
            e.photos.add(photo);
            index = (index + 1) % timeLine.size();
        } while (cursor.moveToNext());
    }

}


