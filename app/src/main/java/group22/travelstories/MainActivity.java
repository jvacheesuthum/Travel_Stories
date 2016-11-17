package group22.travelstories;

import android.Manifest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;
import android.widget.ToggleButton;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private List<TimeLineEntry> timeLine;
    public final static String EXTRA_MESSAGE = "com.travelstories.timeline"; //dodgy restrictions
    Long initStart;

    Client TravelServerWSClient;

    TravelLocationService mService;
    boolean mBound = false;
    private boolean isTracking;
    SeeSummary mSeeSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        timeLine = new ArrayList<>();

        initStart = System.currentTimeMillis();



        ToggleButton trackToggle = (ToggleButton) findViewById(R.id.trackToggle);
        trackToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //toggle enabled - starts tracking
                    Hi x = new Hi();
                    x.say();
                    //addLocationToInfoLayout("Most recent location");
                } else {
                    System.out.println("stops tracking");
                    //toggle disabled - stops tracking
                }
            }
        });

        Button mapToggle = (Button) findViewById(R.id.mapToggle);
        mapToggle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showMap();
            }
            });
        };

//        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
//        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                Intent i = new Intent(
//                        Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
//            }
//        });




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
//        Long start = timeLine.get(0).start.getTimeInMillis();
//        Long end = timeLine.get(timeLine.size() - 1).end.getTimeInMillis();
        Long start = initStart;
        Long end = System.currentTimeMillis();

        String[] selectionArgs = {start.toString(), end.toString()};
        final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        cursor.moveToFirst();
        int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);

        while(cursor.moveToNext()) {
            System.out.println("Curosr: " + cursor.getLong(dateColumn));
        }
        cursor.moveToFirst();

        for (TimeLineEntry e : timeLine) {
            e.photos = new ArrayList<>();
        }

        int index = 0;
        TimeLineEntry prevEntry = null;
        TimeLineEntry currEntry;

        for (TimeLineEntry e : timeLine) {
            System.out.println("TimeLine Time: " + e.getTime());
        }

        if (!cursor.moveToNext()) return;
        cursor.moveToFirst();

        int count = cursor.getCount();

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
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
            }
            start = currEntry.start.getTimeInMillis();
            end = currEntry.end.getTimeInMillis();

            while (cursor.getLong(dateColumn) <= end) {
                photos.add(getPhoto(cursor, dateColumn));

                if (!cursor.moveToNext()) {
                    break;
                }
//                cursor.moveToPrevious();
            }
            currEntry.photos = photos;
            prevEntry = currEntry;
            index++;
            if (cursor.isLast()) {
                break;
            }
        } while (index != timeLine.size());

        if (!cursor.isLast() && !cursor.isAfterLast()) {
            System.out.println("In last if");
            do {
                System.out.println("IN LAST LOOP: " + cursor.getLong(dateColumn));
                currEntry.photos.add(getPhoto(cursor, dateColumn));
                if (!cursor.moveToNext()) {
                    break;
                }
                cursor.moveToPrevious();
            } while (cursor.moveToNext());
        }

        cursor.close();
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
            //Intent settings = new Intent(this, SettingsActivity.class);
            //startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void myBindService(){
        Intent intent = new Intent(this, TravelLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    protected void onStart() {
        System.out.println("on start called");
        super.onStart();
        mSeeSummary = new SeeSummary(this);
        if(isTracking){
            Intent intent = new Intent(this, TravelLocationService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

        try {
            TravelServerWSClient = new Client("http://cloud-vm-46-251.doc.ic.ac.uk:1080", mSeeSummary);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            TravelServerWSClient.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final ToggleButton trackToggle = (ToggleButton) findViewById(R.id.trackToggle);
        trackToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){ // Let's go case //
                    //toggle enabled - starts tracking
                    startTravelLocationService();
                    myBindService();
                    isTracking = true;
                } else {  // That's it case//
                    timeLine = getTimeLineFromTravelLocationService();
                    mSeeSummary.setTimeLine(timeLine);
                    stopTravelLocationService();
                    isTracking = false;
                    if(timeLine == null) {
                        System.out.println("getTimeLineFromTravelLocationService not yet implemented");
                    } else {
                        System.out.println("got a timeline from service");
                        for(TimeLineEntry each : timeLine){
                            System.out.println(each.getTime());
                        }
                    }
                    sendTimeLineLocation(TravelServerWSClient);
                    trackToggle.setText("See summary");
                }
            }
        });

    }

    private void startTravelLocationService(){
        startService(new Intent(this, TravelLocationService.class));
    }

    private void stopTravelLocationService(){
        stopService(new Intent(this, TravelLocationService.class));
    }

    protected void onStop() {
        super.onStop();
        System.out.println("on stop called");
        if(isTracking){
            // Unbind from the service
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
        }
        try {
            TravelServerWSClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));
///            timeLine.add(new TimeLineEntry(mLastLocation, new GregorianCalendar(pdt), new GregorianCalendar(pdt)));

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

        System.out.println("POPULATE LISTTTTTTTTTTTTTTTTTTTTT");
        populateList();

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


    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute) */
    public List<TimeLineEntry> getTimeLineFromTravelLocationService() {
        List<TimeLineEntry> out = null;
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            out = mService.getTimeLineList();
            Toast.makeText(this, "get timeline frm serv", Toast.LENGTH_SHORT).show();
        }
        return out;
    }

    public void showMap(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

    }

    public void mapShow(View view){


    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TravelLocationService.LocalBinder binder = (TravelLocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}


