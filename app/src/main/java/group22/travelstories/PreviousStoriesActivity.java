package group22.travelstories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Felix on 30/12/2016.
 */

public class PreviousStoriesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PreviousStoriesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList stories;
    static final int DISPLAY_ACTIVITY_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("OnCreate in PreviousStoriesActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_stories);
        mRecyclerView = (RecyclerView) findViewById(R.id.previous_stories_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        stories = new ArrayList();

        // specify an adapter (see also next example)
        mAdapter = new PreviousStoriesAdapter(stories);
        mRecyclerView.setAdapter(mAdapter);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        System.out.println("========================OnActivityResult in PrevStoriesAct");
////        super.onActivityResult(requestCode, resultCode, data);
//        // Check which request we're responding to
//        if (data == null) return;
//        switch (requestCode) {
//            case DISPLAY_ACTIVITY_REQUEST_CODE:
//                // Make sure the request was successful
//                try {
//                    if (resultCode == RESULT_FIRST_USER) {
//                        System.out.println("++++++++++++++++++++OnActivityResult in PreviousStoriesActivity TRY");
//                        ArrayList timeline = data.getParcelableArrayListExtra("Timeline");
//                        int index = data.getIntExtra("index", -2);
//                        if (index == -2) {
//                            System.out.println("Can't get index in intent!");
//                            return;
//                        } else if (index == -1) {
//                            System.out.println("New story!");
//                            stories.add(timeline);
//                            mAdapter.updateAdapter(timeline);
//                        } else {
//                            System.out.println("Update old story!");
//                            stories.set(index, timeline);
//                        }
//
//                    }
//                    break;
//                } catch(Exception e) {
//                    System.out.println("Something went wrong in onActivityResult for EDIT_STORY_ACTIVITY_REQUEST_CODE in DisplayStoryActivity");
//                    System.out.println("Exception: " + e);
//                    break;
//                }
//            default:
//                System.out.println("Request Code does not match in PreviousStoriesActivity");
//                break;
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("OnRESUME called");
        Intent intent = getIntent();
        ArrayList timeline = intent.getParcelableArrayListExtra("Timeline");
        int index = intent.getIntExtra("index", -2);

        if (index == -2) {
            System.out.println("Can't get index in intent!");
            return;
        } else if (index == -1) {
            System.out.println("New story!");
            stories.add(timeline);
            mAdapter.updateAdapter(timeline);
        } else {
            System.out.println("Update old story!");
            stories.set(index, timeline);
        }
    }
}
