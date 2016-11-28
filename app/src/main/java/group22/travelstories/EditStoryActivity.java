package group22.travelstories;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;

public class EditStoryActivity extends AppCompatActivity {

    private String newLocation;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        Intent intent = getIntent();
        TimeLineEntry t = (TimeLineEntry) intent.getSerializableExtra("photos");
        System.out.println("GET location from intent: " + t.getLocationName());
        System.out.println("GET time from intent: " + t.getTime());
        final EditText name = (EditText) findViewById(R.id.locationname);
        TextView time = (TextView) findViewById(R.id.time);

        index = intent.getIntExtra("Index", 0);

        name.setText(t.getLocationName());
        name.setTextSize(35);
        name.setEnabled(false);

        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    TextView newName = (TextView) findViewById(R.id.timeLine_name);
//                    if (newName == null) System.out.println("newName is NULL");
//                    System.out.println("WHAT IS newNAME??: " + newName);
//                    System.out.println("PRINTING NAME GET TEXT");
//                    System.out.println("HERE IT IS: " + name.getText());
//                    newName.setText(name.getText());
                    newLocation = name.getText().toString();
                    name.setEnabled(false);
                }
                return false;
            }

        });

        Button edit = (Button) findViewById(R.id.editPlace);
        edit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                name.setEnabled(true);
            }
        });

        time.setText(t.getTime());
        time.setTextSize(18);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, t.photos));


    }

    @Override
    public void onBackPressed() {
        System.out.println("==================================<<<<<1>>>>>>>>>>>>>>===================");
//        finish();
        System.out.println("==================================<<<<<2>>>>>>>>>>>>>>===================");

        Intent intent = new Intent(EditStoryActivity.this, DisplayStoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("NewLocation", newLocation);
        intent.putExtra("Index", index);
        System.out.println("==================================<<<<<3>>>>>>>>>>>>>>===================");
//        startActivityForResult(intent, Activity.RESULT_OK);
        System.out.println("==================================<<<<<3.111>>>>>>>>>>>>>>===================");

        setResult(1, intent);
        finish();
        super.onBackPressed();
//        super.onBackPressed();
    }
}
