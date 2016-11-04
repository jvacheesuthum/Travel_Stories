package group22.travelstories;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
//import android.widget.ListView;
//import android.widget.ArrayAdapter<T>;

public class DisplayStoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_story);

        Intent intent = getIntent();
        ArrayList timeline = intent.getParcelableArrayListExtra(MainActivity.EXTRA_MESSAGE);

        //doing it programmatically - dirty
        //should try ArrayAdapter later
        LinearLayout llMain = (LinearLayout) findViewById(R.id.summary);
        for(int i=0; i<timeline.size(); i++){
            //We create a Layout for every item
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);

            //A TextView to put the order (ie: 1.)
            TextView tv1 = new TextView(this);
            tv1.setText(i+1 + ". ");

            ll.addView(tv1, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));

            //TextView to put the value from the ArrayList
            TextView tv2 = new TextView(this);
            tv2.setText(timeline.get(i).toString());

            ll.addView(tv2, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));

            //Add this layout to the main layout of the XML
            llMain.addView(ll, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0));
        }


    }
}
