package group22.travelstories;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.media.TimedMetaData;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

import static me.iwf.photopicker.PhotoPicker.REQUEST_CODE;

/**
 * Created by Felix on 27/11/2016.
 */

public class EntryFormActivity extends AppCompatActivity {

    static TextView fromDate;
    static TextView fromTime;
    static TextView endDate;
    static TextView endTime;
    private String location;
    private ArrayList<String> photos;
    private static int fromYear;
    private static int fromMonth;
    private static int fromDay;
    private static int fromHour;
    private static int fromMinute;

    private static int endYear;
    private static int endMonth;
    private static int endDay;
    private static int endHour;
    private static int endMinute;
    static boolean end;

    private RecyclerView mRecyclerView;
    ImageAdapter imageAdapter;

    private static int mColumnCount = 3;
    private static int mImageWidth;
    private static int mImageHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_form);

        final EditText name = (EditText) findViewById(R.id.newlocationname);

        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                name.setEnabled(true);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    name.setEnabled(false);
                    location = name.getText().toString();
//                    name.setTextColor(0);
                }
                return false;
            }

        });

        end = false;
        fromDate = (TextView) findViewById(R.id.editFromDate);
        fromTime = (TextView) findViewById(R.id.editFromTime);
        endDate = (TextView) findViewById(R.id.editEndDate);
        endTime = (TextView) findViewById(R.id.editEndTime);

        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
                end = false;
            }
        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
                end = false;
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
                end = true;
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
                end = true;
            }
        });

        Button add = (Button) findViewById(R.id.addEntry);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntryFormActivity.this, DisplayStoryActivity.class);

                intent.putExtra("Location", location);
//                GregorianCalendar from = new GregorianCalendar(fromYear, fromMonth, fromDay, fromHour, fromMinute);
//                GregorianCalendar end = new GregorianCalendar(endYear, endMonth, endDay, endHour, endMinute);
                intent.putStringArrayListExtra("Photos", photos);
                intent.putExtra("FromYear", fromYear);
                intent.putExtra("FromMonth", fromMonth);
                intent.putExtra("FromDay", fromDay);
                intent.putExtra("FromHour", fromHour);
                intent.putExtra("FromMinute", fromMinute);
                intent.putExtra("EndYear", endYear);
                intent.putExtra("EndMonth", endMonth);
                intent.putExtra("EndDay", endDay);
                intent.putExtra("EndHour", endHour);
                intent.putExtra("EndMinute", endMinute);
                setResult(DisplayStoryActivity.ENTRY_FORM_ACTIVITY_REQUEST_CODE, intent);

                finish();
            }
        });

        Button upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
                PhotoPicker.builder()
                        .setShowCamera(true)
                        .setPhotoCount(100)
                        .setPreviewEnabled(false)
                        .start(EntryFormActivity.this, PhotoPicker.REQUEST_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK  && data != null) {
//                Uri selectedImage = data.getData();
//                ClipData clipData = data.getClipData();
                if (photos != null) {
                    ArrayList<String> newPaths = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    imageAdapter.updateAdapter(newPaths);
                } else {
                    photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    mImageWidth = displayMetrics.widthPixels / mColumnCount;
                    mImageHeight = mImageWidth;

                    mRecyclerView = (RecyclerView) findViewById(R.id.uploadGallery);
                    GridLayoutManager layoutManager = new GridLayoutManager(this, mColumnCount);
                    mRecyclerView.setLayoutManager(layoutManager);
                    imageAdapter = new ImageAdapter(this, photos, mImageWidth, mImageHeight);
                    mRecyclerView.setAdapter(imageAdapter);
                }

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong with uploading", Toast.LENGTH_LONG).show();
            System.out.println(e);
        }
    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFrag();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFrag();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class TimePickerFrag extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time = hourOfDay + ":" + minute;
            System.out.println("The Time is: " + time);
            if (end) {
                endHour = hourOfDay;
                endMinute = minute;
                endTime.setText(time);
            } else {
                fromHour = hourOfDay;
                fromMinute = minute;
                fromTime.setText(time);
            }

        }
    }


    public static class DatePickerFrag extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = day + "/" + month + "/" + year;
            System.out.println("The Date is: " + date);
            if (end) {
                endYear = year;
                endMonth = month;
                endDay = day;
                endDate.setText(date);
            } else {
                fromYear = year;
                fromMonth = month;
                fromDay = day;
                fromDate.setText(date);
            }
        }

    }
}

