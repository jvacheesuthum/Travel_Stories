package group22.travelstories;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class DisplayStoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SummaryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private View mFab;
    private ArrayList timeline;
    private BigInteger userid = new BigInteger("1");

    static final int EDIT_STORY_ACTIVITY_REQUEST_CODE = 1;
    static final int ENTRY_FORM_ACTIVITY_REQUEST_CODE = 2;
    //index = -1 -> called from Main
    //index >= 0 -> called from PreviousStoriesActivity
    private int index;
    private TimeLineEntry newEntry;
    private EditText title;

    Client TravelServerWSClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("DisplayStoryActivity OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_story);

        Toolbar toolbar = (Toolbar) findViewById(R.id.display_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
//        mFab = (FloatingActionButton) findViewById(R.id.fab);
//        mFab.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                shareStorySummary();
//            }
//        });
//
//        mAdd = (FloatingActionButton) findViewById(R.id.add);
//        mAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivityForResult(new Intent(DisplayStoryActivity.this, EntryFormActivity.class), ENTRY_FORM_ACTIVITY_REQUEST_CODE);
//            }
//        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        Intent intent = getIntent();
        title = (EditText) findViewById(R.id.edittext_title);
        String t = intent.getStringExtra("title");
        title.setCursorVisible(false);
        if (t != null) {
            title.setText(t);
        }
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setCursorVisible(true);
            }
        });
        title.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    title.setCursorVisible(false);
                }
                return false;
            }
        });


        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        char[] caller = intent.getCharArrayExtra("caller");
        String scaller = null;
        if (caller != null) {
            scaller = new String(caller);
        }

        if (caller != null && scaller.equals("Stories")) {
            timeline = intent.getParcelableArrayListExtra("timeline");
        } else {
            timeline = intent.getParcelableArrayListExtra(MainActivity.EXTRA_MESSAGE);
        }
        index = intent.getIntExtra("index", -1);
//        userid = new BigInteger(intent.getStringExtra("UserId"));

        // specify an adapter (see also next example)
        mAdapter = new SummaryAdapter(timeline, R.layout.cardview);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(DisplayStoryActivity.this, PreviousStoriesActivity.class);
        switch(item.getItemId()) {
            case android.R.id.home:
                System.out.println("---------------------------------action bar back in display pressed");
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                intent.putParcelableArrayListExtra("Timeline", timeline);
                System.out.println("Index during action bar: " + index);
                intent.putExtra("index", index);
                String title = ((EditText)findViewById(R.id.edittext_title)).getText().toString();
                intent.putExtra("title", title);
//        startActivityForResult(intent, Activity.RESULT_OK);
                if (index >= 0) {
                    setResult(PreviousStoriesActivity.DISPLAY_ACTIVITY_REQUEST_CODE, intent);
                    finish();
                } else {
                    finish();
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                super.onBackPressed();
                return true;
            case R.id.add_entry:
                startActivityForResult(new Intent(DisplayStoryActivity.this, EntryFormActivity.class), ENTRY_FORM_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.sharing:
                shareStorySummary();
                return true;
            case R.id.deleteTimeline:
                intent.putExtra("delete", true);
                intent.putExtra("index", index);
                if (index >= 0) {
                    setResult(PreviousStoriesActivity.DISPLAY_ACTIVITY_REQUEST_CODE, intent);
                    finish();
                } else {
                    finish();
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
//        finish();

        Intent intent = new Intent(DisplayStoryActivity.this, PreviousStoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        intent.putParcelableArrayListExtra("Timeline", timeline);
        intent.putExtra("index", index);
//        startActivityForResult(intent, Activity.RESULT_OK);

        if (index >= 0) {
            setResult(PreviousStoriesActivity.DISPLAY_ACTIVITY_REQUEST_CODE, intent);
            finish();
        } else {
            finish();
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        super.onBackPressed();

    }

    private void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void shareStorySummary(){
        makeToast("sharing");
        Gson gson = new Gson();
        List<ServerTimeLineEntry> toSend = new ArrayList<>();
        for(Object each : timeline){
            toSend.add(((TimeLineEntry) each).toServerTimeLineEntry(userid));
        }
        String timeline_json = gson.toJson(toSend);
        //for test purpose userId = 1
        int userId = 1;
        String request = "timeline_share:"+userId+"@"+timeline_json;
        System.out.println("sharing request:"+request);
        TravelServerWSClient.send(request);
        makeToast("sending photos");
        sendAllPhotos();
    }

    @Override
    public void onStart(){
        System.out.println("summary on start called");
        mAdapter.notifyDataSetChanged();
        super.onStart();
        try {
            TravelServerWSClient = new Client("http://cloud-vm-46-251.doc.ic.ac.uk:1080", new SeeSummary(this),new SeeShared(this));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            TravelServerWSClient.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop(){
        super.onStop();
        System.out.println("summary on stop called");
        try {
            TravelServerWSClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        System.out.println("++++++++++REQUEST CODE: " + requestCode);
        if (data == null) return;
        switch (requestCode) {
            case EDIT_STORY_ACTIVITY_REQUEST_CODE:
                // Make sure the request was successful
                try {
                    if (resultCode == RESULT_FIRST_USER) {
                        if (data.getBooleanExtra("delete", false)) {
                            timeline.remove(data.getIntExtra("Index", -1));
                            mAdapter.updateAdapter(null, -2);
                            break;
                        }
                        System.out.println("============================IN RESULT FIRST USER");
                        String newLocation = data.getStringExtra("NewLocation");
                        System.out.println("<1>: " + newLocation);
                        int index = data.getIntExtra("Index", 0);
                        System.out.println("<2>: " + index);
                        ArrayList<String> newPhotos = data.getStringArrayListExtra("NewPhotos");
                        ArrayList<Photo> entryPhotoPaths = new ArrayList<Photo>();

//                        for (Photo p : ((TimeLineEntry) timeline.get(index)).photos) {
//                            entryPhotoPaths.add(p.getPath());
//                        }

                        for (String path : newPhotos) {
                            ExifInterface e = new ExifInterface(path);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                            Date d = dateFormat.parse(e.getAttribute(ExifInterface.TAG_DATETIME));

                            float[] output = {0, 0};
                            e.getLatLong(output);
                            Double longitude = (double) output[0];
                            Double latitude = (double) output[1];

                            Photo photo = new Photo(path, d, latitude, longitude);
                            entryPhotoPaths.add(photo);
                        }
                        System.out.println("<3>: " + timeline.size() + " Index: " + index);
                        ((TimeLineEntry)timeline.get(index)).photos = entryPhotoPaths;
                        System.out.println("<4>");

                        ((TimeLineEntry) timeline.get(index)).setAddress(newLocation);
                        System.out.println("<5>");

                        mAdapter.updateAdapter(null, -2);
                    }
                    break;
                } catch(Exception e) {
                    System.out.println("Something went wrong in onActivityResult for EDIT_STORY_ACTIVITY_REQUEST_CODE in DisplayStoryActivity");
                    System.out.println("Exception: " + e);
                }
            case ENTRY_FORM_ACTIVITY_REQUEST_CODE:
                int index = 0;
                if ( data == null) break;
                String locationName = data.getStringExtra("Location");
                Location location = new Location("");
                System.out.println("===============LOCATION: " + location);
                ArrayList<String> photos = data.getStringArrayListExtra("Photos");

                ArrayList<Photo> entryPhotos = new ArrayList<Photo>();
                GregorianCalendar from = new GregorianCalendar(
                        data.getIntExtra("FromYear", 0),
                        data.getIntExtra("FromMonth", 0),
                        data.getIntExtra("FromDay", 0),
                        data.getIntExtra("FromHour", 0),
                        data.getIntExtra("FromMinute", 0)
                );
                GregorianCalendar end = new GregorianCalendar(
                        data.getIntExtra("EndYear", 0),
                        data.getIntExtra("EndMonth", 0),
                        data.getIntExtra("EndDay", 0),
                        data.getIntExtra("EndHour", 0),
                        data.getIntExtra("EndMinute", 0)
                );

                if (photos != null) {
                    try {

                        for (String path : photos) {
                            System.out.println("In Entryform on ActivityResult in DisplayStoryActivity");
                            ExifInterface e = new ExifInterface(path);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                            Date d = dateFormat.parse(e.getAttribute(ExifInterface.TAG_DATETIME));

                            float[] output = {0, 0};
                            e.getLatLong(output);
                            Double longitude = (double) output[0];
                            Double latitude = (double) output[1];
//                        location.setLatitude(latitude);
//                        location.setLongitude(longitude);

                            Photo photo = new Photo(path, d, latitude, longitude);
                            entryPhotos.add(photo);
                        }
                    } catch (Exception e) {
                        System.out.println("There is an exception in trying to extract photo info: " + e);
                        return;
                    }
                }
                newEntry = new TimeLineEntry(new Location(locationName), from, end);
                newEntry.locationName = locationName;
                newEntry.photos = entryPhotos;
                LatLng loc = getLocationFromAddress(locationName);
                if (loc != null ) {
                    newEntry.location.setLatitude(loc.latitude);
                    newEntry.location.setLongitude(loc.longitude);
                }
                newEntry.locationKey = new BigInteger("0");

                //for test purpose userId = 1
                int userId = 1;
//                String request = "get_location:" + locationName;
//                try {
//                    TravelServerWSClient.connectBlocking();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                TravelServerWSClient.send(request);
                int position = -2;
                if (timeline == null) {
                    System.out.println("=======================================");
                    timeline = new ArrayList();
                    position = addToCorrectIndex(newEntry);
                    System.out.println("Timeline size: " + timeline.size());
                } else {
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++");
                    position = addToCorrectIndex(newEntry);
                    System.out.println("Timeline size: " + timeline.size());
                }

                if (timeline == null) System.out.println("WHY IS IT STILL NULLLLLL");

//                mAdapter = new SummaryAdapter(timeline, R.layout.cardview);
//                mRecyclerView.setAdapter(mAdapter);
//                finish();
//                startActivity(getIntent());
                mAdapter.updateAdapter(newEntry, position);
                System.out.println("------------------------------------------");
                System.out.println("Timeline size: " + timeline.size());

                break;
        }
    }

    private int addToCorrectIndex(TimeLineEntry timeLineEntry) {
        System.out.println("Add to correct index called: " + timeline.size());
        GregorianCalendar start = timeLineEntry.start;
        GregorianCalendar end = timeLineEntry.end;
        GregorianCalendar newCal;
        if (timeline.isEmpty()) {
            timeline.add(timeLineEntry);
            return -1;
        }
        for (Object o : timeline) {
            System.out.println("In Loop: " + timeline.size());
            TimeLineEntry t = (TimeLineEntry) o;
            GregorianCalendar tStart = t.start;
            GregorianCalendar tEnd = t.end;
            int i = timeline.indexOf(o);
            System.out.println("Index i: " + i);
            boolean tSbeforeStart = tStart.compareTo(start) < 0; //true if tStart is before start
            boolean tEbeforeEnd = tEnd.compareTo(end) < 0; //true if tEnd before end
            boolean tSbeforeEnd = tStart.compareTo(end) < 0; //true if tStart before end
            if (tEnd.compareTo(start) < 0) continue; //true if tEnd before start
            if (!tSbeforeEnd) {
                System.out.println("Fits Perfectly");
                timeline.add(i, timeLineEntry);
                return i;
            }
            if (tSbeforeStart && tEbeforeEnd) {//t overlaps on top
                System.out.println("t overlaps on top");
                newCal = start;
                newCal.add(Calendar.SECOND, 0);
                ((TimeLineEntry)timeline.get(i)).end = newCal;

                if (timeline.size() == i + 1) {
                    System.out.println("RETURNED: " + timeline.size());
                    return i;
                }
                TimeLineEntry next = (TimeLineEntry)timeline.get(i + 1);
                if (next.start.compareTo(end) < 0) {//next entry overlaps as well
                    newCal = next.start;
                    newCal.add(Calendar.SECOND, 0);
                    timeLineEntry.end = newCal;
                }
                timeline.add(i + 1, timeLineEntry);
                return i + 1;
            } else if (tSbeforeStart) {//t includes new entry
                System.out.println("t includes new entry");
                long s = start.getTimeInMillis();
                long e = end.getTimeInMillis();
                long diff = s - e;
                long newS = tStart.getTimeInMillis() + diff;
                newCal = new GregorianCalendar();
                newCal.setTimeInMillis(newS);
                timeLineEntry.start = newCal;
                newCal = tStart;
                newCal.add(Calendar.SECOND, 0);
                timeLineEntry.end = newCal;
                timeline.add(i, timeLineEntry);
                return i;
            } else if (tEbeforeEnd) {//new entry includes t
                System.out.println("new entry includes t");
                newCal = tStart;
                newCal.add(Calendar.SECOND, 0);
                timeLineEntry.end = newCal;
                timeline.add(i, timeLineEntry);
                return i;
            } else {//t overlaps on bottom
                System.out.println("t overlaps on bottom");
                newCal = end;
                newCal.add(Calendar.SECOND, 0);
                ((TimeLineEntry)timeline.get(i)).start = newCal;
                timeline.add(i, timeLineEntry);
                return i;
            }
        }
        timeline.add(timeLineEntry);
        return -1;
    }

    public void sendAllPhotos(){
        List<String> all_paths = getAllPhotoPaths(timeline);
        int basetime = ((TimeLineEntry) timeline.get(0)).getStartTime();
        for(int i = 0; i < all_paths.size(); i++){
            String path = all_paths.get(i);
            try {
                sendPhoto(path, userid, basetime + i);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getAllPhotoPaths(List timeline){
        List<String> out = new ArrayList<String>();
        for(Object each : timeline){
            TimeLineEntry each_entry = (TimeLineEntry) each;
            for(String path : each_entry.getAllPhotoPath()){
                out.add(path);
            }
        }
        return out;
    }


    public void sendPhoto(String photo_path, BigInteger userid, int time) throws UnsupportedEncodingException {

        Bitmap img  = null;

        String fileName = userid.toString() + ":" + time;
        int name_length = fileName.length();
        byte[] lengthToBuffer = ByteBuffer.allocate(4).putInt(name_length).array();
        byte[] filenameToBuffer = fileName.getBytes("UTF-8");


        try {
            img = BitmapFactory.decodeFile(photo_path);
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            byteout.write(lengthToBuffer);
            byteout.write(filenameToBuffer);
            img.compress(Bitmap.CompressFormat.JPEG, 10, byteout);
            byteout.flush();
            byte[] imgbyte = byteout.toByteArray();

            TravelServerWSClient.send(imgbyte);
        }
        catch (IOException e) {
            e.printStackTrace();
        } {

        }
    }

    public LatLng getLocationFromAddress(String addr){

        if (addr == null) return null;
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(addr,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            System.out.println("retrieved from geocode ======== " + loc.toString());
            return loc;
        } catch (Exception e) {
            System.out.println("FAIL TO GET LOCATION FROM ADDRESS, using imperial as a default");
            return getLocationFromAddress("Imperial College London");
        }
    }

//    @Override
//    public void onResume() {
//        try {
//            TravelServerWSClient.connectBlocking();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


}
