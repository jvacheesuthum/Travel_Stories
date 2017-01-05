package group22.travelstories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Felix on 30/12/2016.
 */

public class PreviousStoriesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PreviousStoriesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList stories;
    private int index = -2;
    static final int DISPLAY_ACTIVITY_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("OnCreate in PreviousStoriesActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_stories);
        mRecyclerView = (RecyclerView) findViewById(R.id.previous_stories_recycler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.stories_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Your Stories");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        stories = new ArrayList();
//        FileInputStream inputStream;
//        try {
//            inputStream = openFileInput("Stories");
//            ObjectInputStream oInputStream = new ObjectInputStream(inputStream);
//            stories = (ArrayList) oInputStream.readObject();
//            oInputStream.close();
//        } catch (Exception e) {
//            System.out.println("Something went wrong with reading from internal storage");
//        }
//        File dir = getFilesDir();
//        File file = new File(dir, "Stories");
//        boolean deleted = file.delete();

        // specify an adapter (see also next example)
        mAdapter = new PreviousStoriesAdapter(stories);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("OnOptionsItemsSelected Called in PrevStories");
        System.out.println("ID: " + item.getItemId());
        System.out.println("Add story ID: " + R.id.add_story);
        switch(item.getItemId()) {
            case R.id.add_story:
                System.out.println("Add story pressed");
                Intent intent = new Intent(PreviousStoriesActivity.this, MainActivity.class);

//                startActivityForResult(intent, DISPLAY_ACTIVITY_REQUEST_CODE);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("========================OnActivityResult in PrevStoriesAct");
//        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (data == null) return;
        switch (requestCode) {
            case DISPLAY_ACTIVITY_REQUEST_CODE:
                // Make sure the request was successful
                System.out.println("HEREER IN CASE:" + resultCode);
                System.out.println("RESULT_FIRST_USER: " + RESULT_FIRST_USER);
                System.out.println("RESULT_OK: " + RESULT_OK);
                try {
                    System.out.println("++++++++++++++++++++OnActivityResult in PreviousStoriesActivity TRY");
                    ArrayList timeline = data.getParcelableArrayListExtra("Timeline");
                    index = data.getIntExtra("index", -2);
                    if (index == -2) {
                        System.out.println("Can't get index in intent!");
                        return;
                    } else if (index == -1) {
                        System.out.println("New story!");
                        stories.add(timeline);
                        index = -3;
                        mAdapter.updateAdapter(timeline);
                    } else {
                        System.out.println("Update old story!");
                        stories.set(index, timeline);
                        index = -3;
                        mAdapter.updateAdapter(null);
                    }


                    break;
                } catch(Exception e) {
                    System.out.println("Something went wrong in onActivityResult for EDIT_STORY_ACTIVITY_REQUEST_CODE in DisplayStoryActivity");
                    System.out.println("Exception: " + e);
                    break;
                }
            default:
                System.out.println("Request Code does not match in PreviousStoriesActivity");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("OnRESUME called");
        if (index == -3) {
            index = -2;
            return;
        }
        Intent intent = getIntent();
        ArrayList timeline = intent.getParcelableArrayListExtra("Timeline");
        if (timeline == null) System.out.println("-------------------timeline is nulllll");
//        if (timeline.isEmpty()) System.out.println("--------------------timeline is empty");
        index = intent.getIntExtra("index", -2);

        if (index == -2) {
            System.out.println("Can't get index in intent!");
            return;
        } else if (index == -1) {
            System.out.println("New story!");
            stories.add(timeline);
            index = -2;
            mAdapter.updateAdapter(timeline);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //now getIntent() should always return the last received intent
    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        FileOutputStream outputStream;
//        try {
//            outputStream = openFileOutput("Stories", Context.MODE_PRIVATE);
//            ObjectOutputStream ooutputstream = new ObjectOutputStream(outputStream);
//            ooutputstream.writeObject(stories);
//            ooutputstream.close();
//        } catch (Exception e ) {
//            System.out.println("Something went wrong with saving to internal storage");
//        }
//    }


    @Override
    public void onDestroy(){
        System.out.println("OnDestroy for PrevActivity Called");
        super.onDestroy();
    }
}
