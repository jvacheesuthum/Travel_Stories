package group22.travelstories;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by vasin on 11/01/2017.
 */
public class SharedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);

        Intent intent = getIntent();
        final String triptoken = intent.getStringExtra("triptoken");
        TextView token = (TextView) findViewById(R.id.triptoken);
        //TODO change back to triptoken
        token.setText("29");

        final Button button = (Button) findViewById(R.id.weblink);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("cloud-vm-46-251.doc.ic.ac.uk:8081/map.html?token=1");
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i);
                } catch (Exception e) {}
        }

        });
    }

}

