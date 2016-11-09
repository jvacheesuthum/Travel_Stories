package group22.travelstories;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
//import android.widget.ListView;
//import android.widget.ArrayAdapter<T>;

public class DisplayStoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_story);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        Intent intent = getIntent();
        ArrayList timeline = intent.getParcelableArrayListExtra(MainActivity.EXTRA_MESSAGE);

        // specify an adapter (see also next example)
        mAdapter = new SummaryAdapter(timeline);
        mRecyclerView.setAdapter(mAdapter);


    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_display_story);
//
//        rv = (RecyclerView) findViewById(R.id.rv);
//
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        rv.setLayoutManager(llm);
//        rv.setHasFixedSize(true);
//        // specify an adapter (see also next example)
//        Intent intent = getIntent();
//        ArrayList timeline = intent.getParcelableArrayListExtra(MainActivity.EXTRA_MESSAGE);
//        SummaryAdapter mAdapter = new SummaryAdapter(timeline);
//        rv.setAdapter(mAdapter);
//    }





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
