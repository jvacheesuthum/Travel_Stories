package group22.travelstories;

import android.app.Activity;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.iwf.photopicker.PhotoPicker;

import static me.iwf.photopicker.PhotoPicker.REQUEST_CODE;

public class EditStoryActivity extends AppCompatActivity {

    private String newLocation;
    private int index;
    private RecyclerView mRecyclerView;
    ImageAdapter imageAdapter;

    private static int mColumnCount = 3;
    private static int mImageWidth;
    private static int mImageHeight;
    private ArrayList<String> photoPaths;
    private boolean deleting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        TimeLineEntry t = (TimeLineEntry) intent.getSerializableExtra("photos");
        System.out.println("GET location from intent: " + t.getLocationName());
        System.out.println("GET time from intent: " + t.getTime());
        final EditText name = (EditText) findViewById(R.id.locationname);
        TextView time = (TextView) findViewById(R.id.time);

        index = intent.getIntExtra("Index", 0);

        name.setText(t.getLocationName());
        name.setTextSize(25);
        name.setCursorVisible(false);

        newLocation = t.getLocationName();
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setCursorVisible(true);
            }
        });
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    newLocation = name.getText().toString();
                    name.setCursorVisible(false);
                }
                return false;
            }

        });

        Button upload = (Button) findViewById(R.id.addPhoto);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setShowCamera(true)
                        .setPhotoCount(100)
                        .setPreviewEnabled(false)
                        .start(EditStoryActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });

        final ToggleButton delete = (ToggleButton) findViewById(R.id.deletePhoto);
        delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    deleting = true;
                    imageAdapter.setDelete(true);
                    System.out.println("Checked Deleting");
                } else {
                    if (deleting) {
                        imageAdapter.deletePhotos();
                        imageAdapter.setDelete(false);
                        photoPaths = imageAdapter.getPhotoPaths();
                        imageAdapter.updateAdapter(photoPaths);
                    }
                }
            }
        });

        time.setText(t.getTime());
        time.setTextSize(15);

//        GridView gridview = (GridView) findViewById(R.id.gridview);
//        gridview.setAdapter(new ImageAdapter(this, t.photos, 30, 30));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mImageWidth = displayMetrics.widthPixels / mColumnCount;
        mImageHeight = mImageWidth;

        mRecyclerView = (RecyclerView) findViewById(R.id.gallery);
        GridLayoutManager layoutManager = new GridLayoutManager(this, mColumnCount);
        mRecyclerView.setLayoutManager(layoutManager);

        photoPaths = new ArrayList<String>();
        for (Photo p : t.photos) {
            photoPaths.add(p.getPath());
        }

        imageAdapter = new ImageAdapter(this, photoPaths, mImageWidth, mImageHeight);
        mRecyclerView.setAdapter(imageAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(EditStoryActivity.this, DisplayStoryActivity.class);
        switch(item.getItemId()) {
            case android.R.id.home:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                intent.putExtra("NewLocation", newLocation);
                intent.putExtra("Index", index);
                intent.putStringArrayListExtra("NewPhotos", photoPaths);
//        startActivityForResult(intent, Activity.RESULT_OK);

                setResult(DisplayStoryActivity.EDIT_STORY_ACTIVITY_REQUEST_CODE, intent);
                finish();
                super.onBackPressed();
                return true;
            case R.id.delete_entry:
                intent.putExtra("delete", true);
                intent.putExtra("Index", index);
                setResult(DisplayStoryActivity.ENTRY_FORM_ACTIVITY_REQUEST_CODE, intent);
//                startActivityForResult(intent, DISPLAY_ACTIVITY_REQUEST_CODE);
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
//        finish();

        Intent intent = new Intent(EditStoryActivity.this, DisplayStoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        intent.putExtra("NewLocation", newLocation);
        intent.putExtra("Index", index);
        intent.putStringArrayListExtra("NewPhotos", photoPaths);
//        startActivityForResult(intent, Activity.RESULT_OK);


        setResult(DisplayStoryActivity.EDIT_STORY_ACTIVITY_REQUEST_CODE, intent);
        finish();
        super.onBackPressed();
//        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK  && data != null) {

                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                for(String path : photos) {
                    if (!photoPaths.contains(path)) {
                        photoPaths.add(path);
                    }
                }
                imageAdapter.updateAdapter(photoPaths);

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong with uploading", Toast.LENGTH_LONG).show();
            System.out.println(e);
        }
    }
}
