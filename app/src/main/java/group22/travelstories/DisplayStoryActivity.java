package group22.travelstories;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.URISyntaxException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
//import android.widget.ListView;
//import android.widget.ArrayAdapter<T>;

public class DisplayStoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SummaryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mFab;
    private ArrayList timeline;
    static final int EDIT_STORY_ACTIVITY_REQUEST_CODE = 1;
    static final int ENTRY_FORM_ACTIVITY_REQUEST_CODE = 2;

    Client TravelServerWSClient;
    //private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("DisplayStoryActivity OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_story);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                shareStorySummary();
            }
        });

        FloatingActionButton mAdd = (FloatingActionButton) findViewById(R.id.add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(DisplayStoryActivity.this, EntryFormActivity.class), 2);
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        Intent intent = getIntent();
        timeline = intent.getParcelableArrayListExtra(MainActivity.EXTRA_MESSAGE);

        // specify an adapter (see also next example)
        mAdapter = new SummaryAdapter(timeline, R.layout.cardview);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void shareStorySummary(){
        makeToast("sharing");
        Gson gson = new Gson();
        List<ServerTimeLineEntry> toSend = new ArrayList<>();
        for(Object each : timeline){
            toSend.add(((TimeLineEntry) each).toServerTimeLineEntry());
        }
        String timeline_json = gson.toJson(toSend);
        //for test purpose userId = 1
        int userId = 1;
        String request = "timeline_share:"+userId+"@"+timeline_json;
        System.out.println("sharing request:"+request);
        TravelServerWSClient.send(request);
    }

    @Override
    public void onStart(){
        System.out.println("summary on start called");
        mAdapter.notifyDataSetChanged();
        super.onStart();
        try {
            TravelServerWSClient = new Client("http://cloud-vm-46-251.doc.ic.ac.uk:1080", null,null);
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
                        System.out.println("============================IN RESULT FIRST USER");
                        String newLocation = data.getStringExtra("NewLocation");
                        int index = data.getIntExtra("Index", 0);
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
                        ((TimeLineEntry)timeline.get(index)).photos = entryPhotoPaths;

                        ((TimeLineEntry) timeline.get(index)).setAddress(newLocation);

                        mAdapter.updateAdapter(null);
                    }
                    break;
                } catch(Exception e) {
                    System.out.println("Something went wrong in onActivityResult for EDIT_STORY_ACTIVITY_REQUEST_CODE in DisplayStoryActivity");
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
                        System.out.println("There is an exception in trying to extract photo info");
                    }
                }
                TimeLineEntry newEntry = new TimeLineEntry(null, from, end);
                newEntry.locationName = locationName;
                newEntry.photos = entryPhotos;

                //for test purpose userId = 1
                int userId = 1;
                String request = "get_location:" + locationName;

//                TravelServerWSClient.send(request);

                if (timeline == null) {
                    System.out.println("=======================================");
                    timeline = new ArrayList();
//                    timeline.add(newEntry);
                    System.out.println("Timeline size: " + timeline.size());
                } else {
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++");
//                    timeline.add(newEntry);
                    System.out.println("Timeline size: " + timeline.size());
                }

                if (timeline == null) System.out.println("WHY IS IT STILL NULLLLLL");

//                mAdapter = new SummaryAdapter(timeline, R.layout.cardview);
//                mRecyclerView.setAdapter(mAdapter);
//                finish();
//                startActivity(getIntent());
                mAdapter.updateAdapter(newEntry);
                System.out.println("------------------------------------------");
                System.out.println("Timeline size: " + timeline.size());
                break;
        }
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_display_story);
//
//        Intent intent = getIntent();
//        ArrayList timeline = intent.getParcelableArrayListExtra(MainActivity.EXTRA_MESSAGE);
//
//        //doing it programmatically - dirty
//        //should try ArrayAdapter later
//        LinearLayout summary = (LinearLayout) findViewById(R.id.summary);
//        for(int i=0; i<timeline.size(); i++){
//            //We create a Layout for every item
//            LinearLayout item = new LinearLayout(this);
//            item.setOrientation(LinearLayout.HORIZONTAL);
//
//            //A TextView to put the order (ie: 1.)
//            TextView number = new TextView(this);
//            number.setText(i+1 + ". ");
//            item.addView(number, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));
//
//            //TextView to put the value from the ArrayList
//            TextView info = new TextView(this);
//            info.setText(timeline.get(i).toString());
//            item.addView(info, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
//
//            //Add this layout to the main layout of the XML
//            summary.addView(item, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0));
//        }
//
////        ArrayAdapter<TimeLineEntry> adapter = new ArrayAdapter<TimeLineEntry>(this, android.R.layout.simple_list_item_1, timeline);
////        ListView listView = (ListView) findViewById(R.id.listview);
////        listView.setAdapter(adapter);
//
//
//    }

}
