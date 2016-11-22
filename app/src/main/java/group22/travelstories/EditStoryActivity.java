package group22.travelstories;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import java.sql.Time;

public class EditStoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        Intent intent = getIntent();
        TimeLineEntry t = (TimeLineEntry) intent.getSerializableExtra("photos");
        System.out.println("GET location from intent: " + t.getLocationName());
        System.out.println("GET time from intent: " + t.getTime());
        TextView name = (TextView) findViewById(R.id.locationname);
        TextView time = (TextView) findViewById(R.id.time);

        name.setText(t.getLocationName());
        name.setTextSize(35);
        time.setText(t.getTime());
        time.setTextSize(18);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, t.photos));


    }
}
