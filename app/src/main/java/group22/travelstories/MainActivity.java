package group22.travelstories;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    CallbackManager callbackManager;

    ShareDialog shareDialog;

    Intent serviceIntent;

    private static int RESULT_LOAD_IMAGE = 1;
    private ArrayList<TimeLineEntry> timeLine;
    public final static String EXTRA_MESSAGE = "com.travelstories.timeline";
    Long initStart;

    public Client TravelServerWSClient;

    TravelLocationService mService;
    boolean mBound = false;
    private boolean isTracking;
    SeeSummary mSeeSummary;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private ArrayList<LatLng> points; //for tracing, invisible markers every location change
    private boolean firstRun;
    Polyline line;

    private BigInteger userid = new BigInteger("1");
    public static GregorianCalendar startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("MainActivity onCreate Called");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        timeLine = new ArrayList<>();
        initStart = System.currentTimeMillis();

        //from mapsactivity----------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        points = new ArrayList<LatLng>();
        firstRun = true;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MainActivity.this, PreviousStoriesActivity.class);
                finish();
                startActivity(intent);
                super.onBackPressed();
                return true;
            default:
                return false;
        }
    }


    private void myBindService(){
        Intent intent = new Intent(this, TravelLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
            TravelServerWSClient = new Client("http://cloud-vm-46-251.doc.ic.ac.uk:1080", mSeeSummary, new SeeSuggestions(this));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            TravelServerWSClient.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        View previousButton = findViewById(R.id.previous_stories);
        previousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreviousStoriesActivity.class);
                startActivity(intent);
            }
        });

        View suggestButton = findViewById(R.id.suggestion);
        suggestButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                requestNearBySuggestions(TravelServerWSClient);
            }
        });


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
                    timeLine =getTimeLineFromTravelLocationService();
                    mSeeSummary.setTimeLine(timeLine);
                    mSeeSummary.setUserId(userid);
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
                    //sendLocationTrace(TravelServerWSClient);
                    trackToggle.setText("See summary");
                }
            }
        });

    }

    private void startTravelLocationService(){
        serviceIntent = new Intent(this, TravelLocationService.class);
        startService(serviceIntent);
    }

    private void stopTravelLocationService(){
        stopService(serviceIntent);
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
//        try {
//            TravelServerWSClient.closeBlocking();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //TODO send location trace
    }

    @Override
    protected void onRestart(){
        System.out.println("on restart called");
        super.onRestart();
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
                try {
            TravelServerWSClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void uploadPhotoBitmaps() {

        List<ImageToUpload> toSend = new ArrayList<>();

        for (TimeLineEntry entry : timeLine) {
            for (Photo photo : entry.photos) {
                Bitmap decoded = BitmapFactory.decodeFile(photo.getPath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                decoded.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                String compressedPhoto = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                toSend.add(new ImageToUpload("", compressedPhoto)); //empty photo name for now?

            }
        }

        makeToast("uploading images to server");
        Gson gson = new Gson();
        String images_json = gson.toJson(toSend);
        int userId = 1;
        String request = "images_taken:"+userId+"@"+images_json;
        TravelServerWSClient.send(request);

    }
//separate class if needed - only structure
    /*private class UploadImage extends AsyncTask<Void, Void, Void>{

        Bitmap image;
        String name;
        String path;

        public UploadImage(String path, String name){
            this.path = path;
            this.name = name;
            this.image = BitmapFactory.decodeFile(path);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            String compressedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

            ArrayList<ImageToUpload> toSend = new ArrayList<>();
            toSend.add(new ImageToUpload(name, compressedImage));
            makeToast("uploading map trace to server");
            Gson gson = new Gson();
            String images_json = gson.toJson(toSend);
            int userId = 1;
            String request = "images_taken:"+userId+"@"+images_json;
            TravelServerWSClient.send(request);
            return null;

        }
    }*/

//------------------------------------------- methods reimplemented from mapsactivity

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Service and track user location on map
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng){

                MarkerOptions options = new MarkerOptions().position(latLng);
                options.title(getAddrFromLatLng(latLng));

                options.icon(BitmapDescriptorFactory.defaultMarker());
                mMap.addMarker(options.draggable(true));
                //TODO need to set marker drag event later in order for it to change loaction along with the drag
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;

        //Place current location marker and move camera (only for first location detected)
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (firstRun) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Starting Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            firstRun = false;
        }

        //stop location updates DON'T DELETE THIS COMMENT
        /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/
        points.add(latLng);
        redrawLine(latLng);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }



    public void requestNearBySuggestions(Client wsc){
        wsc.send("nearby_place:"+"-0.126,51.519,1");
    }



    public void sendTimeLineLocation(Client wsc) {
        System.out.println("==========================sendTimeLineLocation Called");
        if (timeLine.isEmpty()) {
//            /*
//            System.out.println("timeline is empty");
//            return;
//            */
//            // test thing
//            System.out.println("timeline is empty");
//            System.out.println("populating timeline list ...");
//
//            wsc.send("timeline_address:-0.126957,51.5194133");
//
//            startActivity(new Intent(MainActivity.this, DisplayStoryActivity.class));
//
//            ///

//            return;


            TimeLineEntry last_entry = new TimeLineEntry(mLastLocation, startTime,new GregorianCalendar(TravelLocationService.pdt) );
            timeLine.add(last_entry);

            //uncomment below for testing photos
//            timeLine = new ArrayList<>();
//            Intent i = new Intent();
//            i.putParcelableArrayListExtra("timeline", timeLine);
//            i.putExtra("caller", "Stories");
            startActivity(new Intent(MainActivity.this, DisplayStoryActivity.class));
            return;
            //------------------------------------
        }

        String request = "timeline_address:";

        for (TimeLineEntry each : timeLine) {

            Location eachLocation = each.getLocation();
            request += eachLocation.getLongitude() + "," + eachLocation.getLatitude() + "@";

        }
//        request += "-0.1269566,51.5194133";
        System.out.println("request message is:*" + request + "*");

        timeLine = Helper.populateList(timeLine, initStart, this);


        wsc.send(request);

    }


    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute) */
    public ArrayList<TimeLineEntry> getTimeLineFromTravelLocationService() {
        ArrayList<TimeLineEntry> out = null;
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            out = mService.getTimeLineList();
            Toast.makeText(this, "get timeline frm serv", Toast.LENGTH_SHORT).show();
        }
        return out;
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

    private String getAddrFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "address undefined";
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
        }
        return address;
    }

    private void redrawLine(LatLng currentLocation){

        //clear previously drawn line if not null
        if (line != null){
            line.remove();
        }

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        mMap.addMarker(new MarkerOptions().position(currentLocation).visible(false)); //add Marker in current position
        line = mMap.addPolyline(options); //add Polyline
    }


    public void sendLocationTrace(Client wsc) {
        System.out.println("sendLocationTrace called");
        if (points.isEmpty()) {
            System.out.println("locationtrace 1");
            points.add(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLatitude()));
        }
        System.out.println("locationtrace 2");
        String mapTrace = "";
        for (LatLng l : points) {
            mapTrace += "/" + l.longitude + "," + l.latitude;
        }
        String request = "Final_map_trace:"+userid.toString()+"@"+mapTrace;
        System.out.println("uploading map coordinates");
        System.out.println(request);
        wsc.send(request);
        System.out.println("map coords uploaded : "+request);

    }

    @Override
    public void onNewIntent(Intent newintent){
        super.onNewIntent(newintent);
        // getIntent() should always return the most recent
        setIntent(newintent);
    }

    @Override
    public void onResume() {
        System.out.println("onResume called");
        super.onResume();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SeeSuggestions.SUGGESTION_MARKER) {
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Showing this location", Toast.LENGTH_SHORT).show();
                String suggestion = data.getStringExtra("latlong");
                mMap.addMarker(new MarkerOptions()
                        .position(getLatLngFromString(suggestion))
                        .title(data.getStringExtra("name")));
            }
        }
    }

    /* utils */
    public LatLng getLatLngFromString(String address){
        String[] latlng = address.split(", ", 2);
        Double lat = Double.parseDouble(latlng[0]);
        Double longi = Double.parseDouble(latlng[1]);
        return new LatLng(lat, longi);
    }
}








