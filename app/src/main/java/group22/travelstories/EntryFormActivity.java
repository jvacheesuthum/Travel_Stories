package group22.travelstories;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.iwf.photopicker.PhotoPicker;

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
    private boolean deleting = false;

    //for search bar
    private Integer THRESHOLD = 2;
    private DelayAutoCompleteTextView geo_autocomplete;
    private ImageView geo_autocomplete_clear;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.form_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

//        final EditText name = (EditText) findViewById(R.id.newlocationname);
//        name.setCursorVisible(false);
//        name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                name.setCursorVisible(true);
//            }
//        });
//        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                name.setEnabled(true);
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    name.setCursorVisible(false);
//                    location = name.getText().toString();
////                    name.setTextColor(0);
//                }
//                return false;
//            }
//
//        });

        //location search bar
        geo_autocomplete_clear = (ImageView) findViewById(R.id.geo_autocomplete_clear);

        geo_autocomplete = (DelayAutoCompleteTextView) findViewById(R.id.geo_autocomplete);
        geo_autocomplete.setThreshold(THRESHOLD);
        geo_autocomplete.setAdapter(new GeoAutoCompleteAdapter(this)); // 'this' is Activity instance

        geo_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GeoSearchResult result = (GeoSearchResult) adapterView.getItemAtPosition(position);
                geo_autocomplete.setText(result.getAddress());
            }
        });

        geo_autocomplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    geo_autocomplete_clear.setVisibility(View.VISIBLE);
                    location = geo_autocomplete.getText().toString();
                }
                else {
                    geo_autocomplete_clear.setVisibility(View.GONE);
                }
            }
        });

        geo_autocomplete_clear.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                geo_autocomplete.setText("");
            }
        });


//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });

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
                        .setShowCamera(false)
                        .setPhotoCount(100)
                        .setPreviewEnabled(true)
                        .setPreviewEnabled(false)
                        .start(EntryFormActivity.this, PhotoPicker.REQUEST_CODE);

            }
        });

        final ToggleButton delete = (ToggleButton) findViewById(R.id.deleteEntry);
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
                        photos = imageAdapter.getPhotoPaths();
                        imageAdapter.updateAdapter(photos);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
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
                    for (String p : newPaths) {
                        if (!photos.contains(p)) {
                            photos.add(p);
                        }
                    }
                    imageAdapter.updateAdapter(photos);
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
            Calendar c = Calendar.getInstance();
            c.set(0,0,0, hourOfDay, minute);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            dateFormat.setTimeZone(c.getTimeZone());
            if (end) {
                endHour = hourOfDay;
                endMinute = minute;
                endTime.setText(dateFormat.format(c.getTime()));
            } else {
                fromHour = hourOfDay;
                fromMinute = minute;
                fromTime.setText(dateFormat.format(c.getTime()));
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
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setTimeZone(c.getTimeZone());
//            String date = day + "/" + month + "/" + year;
//            System.out.println("The Date is: " + date);
            if (end) {
                endYear = year;
                endMonth = month;
                endDay = day;
                endDate.setText(dateFormat.format(c.getTime()));
            } else {
                fromYear = year;
                fromMonth = month;
                fromDay = day;
                fromDate.setText(dateFormat.format(c.getTime()));
            }
        }

    }
}

