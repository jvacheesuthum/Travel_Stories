package group22.travelstories;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;
import java.net.URL;

/**
 * Created by vasin on 11/01/2017.
 */
public class SharedActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    ShareDialog shareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        Intent intent = getIntent();
        final String triptoken = intent.getStringExtra("triptoken");
        TextView token = (TextView) findViewById(R.id.triptoken);
        token.setText(triptoken);

        final Button button = (Button) findViewById(R.id.weblink);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //try {
                    Uri uri = Uri.parse("http://cloud-vm-46-251.doc.ic.ac.uk:8081/map.html?token="+triptoken);
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i);
                //} catch (Exception e) {}
        }

        });

        final Button fbutton = (Button) findViewById(R.id.facebookshare);
        fbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {

                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("My Journey")
                            .setContentUrl(Uri.parse("http://cloud-vm-46-251.doc.ic.ac.uk:8081/map.html?token="+triptoken))
                            .setImageUrl(Uri.parse("http://www.doc.ic.ac.uk/~vw214/treek.png"))
                            .build();



                    shareDialog.show(linkContent);
                }
            }

        });
            }
    }


