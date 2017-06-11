package com.cs442.group7.eventiit;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.cs442.group7.eventiit.app.AppControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class EditEvent extends BaseActivity implements TimePickerFragment.Listener, DatePickerFragment.Listener, VenueAddressSingleton.Listener {


    private ProgressDialog pDialog;
    private Button edit_event_start_time;
    private Button edit_event_end_time;
    private Button edit_event_rsvp_by_time;
    private Button edit_event_start_date;
    private Button edit_event_end_date;
    private Button edit_event_rsvp_by_date;
    private EditText edit_event_name;
    private EditText edit_event_location;
    private EditText edit_event_venue_other_details;
    private TextView edit_event_description;
    private Spinner edit_event_venue_name;
    private int venueID;
    private List<String> spinnerArray;// Update this kind user input method. Shyam
    private Button button_edit_event;
    private Button edit_event_add_image_text;

    private NetworkImageView edit_event_image;

    private DateTimeFormater eventStartDateTime;
    private DateTimeFormater eventEndDateTime;
    private DateTimeFormater eventRSVPDateTime;
    //0 for start , 1 for end and 2 for rsvp
    private int flagForTimeSet = 0;
    private int flagForDateSet = 0;

    private static String url_edit_event = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/get_event_edit_details.php";
    private static String edit_event_url = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/edit_event_with_image_upload.php";
    //    private static String url_edit_event = "http://10.0.2.2:8888/PhpProject1/get_event_edit_details.php";
    private static final String TAG = EditEvent.class.getSimpleName();

    private String event_image_url;
    // for image upload purpose
    private Bitmap bitmap;

    private static String event_ID;

    private int PICK_IMAGE_REQUEST = 1;


    private boolean flag_get_data_from_server = true;
    private boolean flag_image_updated_for_upload = false;


    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;
    protected LinearLayout optionsBarTop;
    protected LinearLayout optionsBarBottom;

    private VenueAddressSingleton.VenueArrayList<VenueAddressSingleton.VenueAddressObject> venueAddressObjects;

    ArrayAdapter<String> spinnerAdapter;

    @Override
    public void setContentView(int layoutResID) {
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);      // The base layout
        subActivityContent = (RelativeLayout) fullLayout.findViewById(R.id.content_frame);          // The frame layout where the activity content is placed.
        optionsBarTop = (LinearLayout) fullLayout.findViewById(R.id.options_bar_top);
        optionsBarTop.setVisibility(View.GONE);
        optionsBarBottom = (LinearLayout) fullLayout.findViewById(R.id.options_bar_bottom);
        optionsBarBottom.setVisibility(View.GONE);
        getLayoutInflater().inflate(layoutResID, subActivityContent, true);                         // Places the activity layout inside the activity content frame.
        super.setContentView(fullLayout);                                                           // Sets the content view as the merged layouts.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        loadPreferences();

        // Navigation Drawer Listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        getSupportActionBar().setTitle("Events - Illinois Tech");

        event_ID = getIntent().getExtras().getString("event_ID");
        Intent searchIntent = getIntent();
        if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {

            String query = searchIntent.getStringExtra(SearchManager.QUERY);

            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

        }

        // Populate spinner for location
        spinnerArray = new ArrayList<String>();


         spinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerArray) {

            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(18);

                return v;

            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setGravity(Gravity.CENTER);

                return v;

            }

        };


        //initialize view variables
        edit_event_start_time = (Button) findViewById(R.id.edit_event_start_time);
        edit_event_end_time = (Button) findViewById(R.id.edit_event_end_time);
        edit_event_rsvp_by_time = (Button) findViewById(R.id.edit_event_rsvp_by_time);
        edit_event_start_date = (Button) findViewById(R.id.edit_event_start_date);
        edit_event_end_date = (Button) findViewById(R.id.edit_event_end_date);
        edit_event_rsvp_by_date = (Button) findViewById(R.id.edit_event_rsvp_by_date);
        edit_event_name = (EditText) findViewById(R.id.edit_event_name);
        edit_event_image = (NetworkImageView) findViewById(R.id.edit_event_image);
        edit_event_venue_other_details = (EditText)findViewById(R.id.edit_event_venue_other_detail);

        edit_event_location = (EditText) findViewById(R.id.edit_event_location);

        edit_event_venue_name = (Spinner) findViewById(R.id.edit_event_venue_name);
        edit_event_venue_name.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        edit_event_venue_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edit_event_location.setText(venueAddressObjects.get(position).venueAddress);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do something
            }
        });

        //Comment to be added SHYAM!!
        VenueAddressSingleton.getInstance(this).startJob();


        button_edit_event = (Button) findViewById(R.id.button_edit_event);
        button_edit_event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(validate()) {
                    uploadEditedEvent();
                }
            }
        });

        edit_event_add_image_text = (Button) findViewById(R.id.edit_event_add_image_text);
        edit_event_add_image_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        edit_event_description = (TextView) findViewById(R.id.edit_event_add_description);
        edit_event_description.setEllipsize(TextUtils.TruncateAt.END);

        edit_event_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "description clicked", Toast.LENGTH_SHORT).show();
                DescriptionDialogueFragment descriptionFragment =
                        DescriptionDialogueFragment.newInstance(edit_event_description.getText().toString());
//                Log.d(TAG,"descrption "+edit_event_description.getText().toString() );
                FragmentManager fm = getFragmentManager();
                descriptionFragment.show(fm, "fragment_add_description");

            }
        });

        edit_event_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                flagForTimeSet = 1;
            }
        });

        edit_event_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                flagForTimeSet = 2;
            }
        });

        edit_event_rsvp_by_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                flagForTimeSet = 3;
            }
        });

        edit_event_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                flagForDateSet = 1;
            }
        });

        edit_event_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                flagForDateSet = 2;
            }
        });

        edit_event_rsvp_by_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                flagForDateSet = 3;
            }
        });


        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        //Load from online database
        if (flag_get_data_from_server)
            fillEventDetails();

        // don't fetch data from server next time onCreate.
        flag_get_data_from_server = false;

    }


    private void uploadEditedEvent() {

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        // Using volley String request to send data this time.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, edit_event_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(EditEvent.this, "Event Updated", Toast.LENGTH_LONG).show();

                        AppControllerSingleton.getInstance().getLruBitmapCache().remove("#W0#H0"+event_image_url);

                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        loading.dismiss();
                        VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
//                        hidePDialog();
//                        Showing toast
//                        Toast.makeText(EditEvent.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                java.util.Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters

                params.put("edit_event_name", edit_event_name.getText().toString().trim());

                if (flag_image_updated_for_upload) {
                    String image = getStringImage(bitmap);
                    params.put("edit_event_image", image);
                }
                if (!(edit_event_venue_name.getSelectedItem().toString() == null ||
                        edit_event_venue_name.getSelectedItem().toString().length() == 0)) {
                    params.put("edit_event_venue_name", edit_event_venue_name.getSelectedItem().toString());
                }

                //Special
                params.put("edit_event_venue_ID", String.valueOf(venueAddressObjects.getIndexByVenueName(edit_event_venue_name.getSelectedItem().toString())));

//                spinnerArray.indexOf(venueAddressObjects.get(venueID).venueName);
                if (!(edit_event_description.getText().toString() == null ||
                        edit_event_description.getText().toString().length() == 0)) {
                    params.put("edit_event_description", edit_event_description.getText().toString());
                }

                if (!(eventRSVPDateTime == null)) {
                    params.put("edit_event_rsvp_by", eventRSVPDateTime.getSQLDateTime());
                }

                params.put("edit_event_location", edit_event_location.getText().toString().trim());
                params.put("edit_event_start_date_time", eventStartDateTime.getSQLDateTime());
                params.put("edit_event_end_date_time", eventEndDateTime.getSQLDateTime());
                params.put("edit_event_ID", event_ID);
                params.put("edit_event_venue_other_details", edit_event_venue_other_details.getText().toString());
//                Toast.makeText(EditEvent.this,edit_event_venue_other_details.getText().toString(),Toast.LENGTH_SHORT);
                Log.e(TAG,edit_event_venue_other_details.getText().toString());
                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = AppControllerSingleton.getInstance().getRequestQueue();

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    //fetches event details and updates the views in the activity.
    private void fillEventDetails() {

        pDialog.show();
        // Creating volley request obj
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject("{\"eventID\":\"" + event_ID + "\"}");
//            Log.d(TAG,"JSON Object = "+jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest eventReq = new JsonObjectRequest(Request.Method.POST, url_edit_event, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                hidePDialog();

                // Parsing json
                try {
                    int success = response.getInt("success");
                    if (success == 1) {

                        JSONArray ja = response.getJSONArray("eventDetails");
                        JSONObject obj = ja.getJSONObject(0);
                        Log.d(TAG, "object" + obj.toString());
//                        Log.d(TAG, "event Name = " + obj.getString("eventName"));
                        edit_event_name.setText(obj.getString("eventName"));
                        venueID = obj.getInt("eventVenueID");
                        edit_event_location.setText(venueAddressObjects.getObjectByID(venueID).venueAddress);
//                        edit_event_venue_name.setSelection(spinnerArray.indexOf(obj.getString("eventVenueName")), true);
                        edit_event_venue_name.setSelection(spinnerArray.indexOf(venueAddressObjects.getObjectByID(venueID).venueName), true);


                        //intantiate DateTimeFormater class. It will contain the time and date information
                        eventStartDateTime = new DateTimeFormater();
                        eventEndDateTime = new DateTimeFormater();


                        String eventStartDate = obj.getString("eventStartDateTime").substring(0, 10);
                        String eventStartTime = obj.getString("eventStartDateTime").substring(11, 16);
                        String eventEndDate = obj.getString("eventEndDateTime").substring(0, 10);
                        String eventEndTime = obj.getString("eventEndDateTime").substring(11, 16);

                        eventStartDateTime.setDate(Integer.parseInt(eventStartDate.split("-")[0]),
                                Integer.parseInt(eventStartDate.split("-")[1]),
                                Integer.parseInt(eventStartDate.split("-")[2]));
                        eventStartDateTime.setTime(Integer.parseInt(eventStartTime.split(":")[0]),
                                Integer.parseInt(eventStartTime.split(":")[1]));
                        eventEndDateTime.setDate(Integer.parseInt(eventEndDate.split("-")[0]),
                                Integer.parseInt(eventEndDate.split("-")[1]),
                                Integer.parseInt(eventEndDate.split("-")[2]));
                        eventEndDateTime.setTime(Integer.parseInt(eventEndTime.split(":")[0]),
                                Integer.parseInt(eventEndTime.split(":")[1]));

                        edit_event_start_time.setText(eventStartDateTime.getTextViewTime());
                        edit_event_end_time.setText(eventEndDateTime.getTextViewTime());
                        edit_event_start_date.setText(eventStartDateTime.getTextViewDate());
                        edit_event_end_date.setText(eventEndDateTime.getTextViewDate());

//                        Toast.makeText(this,obj.getString(""))
                        edit_event_venue_other_details.setText(obj.optString("eventVenueOtherDetails"));


                        if (!obj.isNull("eventRsvpBy")) {
                            String eventRSVPDate = obj.getString("eventRsvpBy").substring(0, 10);
                            String eventRSVPTime = obj.getString("eventRsvpBy").substring(11, 16);
                            eventRSVPDateTime = new DateTimeFormater();
                            eventRSVPDateTime.setDate(Integer.parseInt(eventRSVPDate.split("-")[0]),
                                    Integer.parseInt(eventRSVPDate.split("-")[1]),
                                    Integer.parseInt(eventRSVPDate.split("-")[2]));
                            eventRSVPDateTime.setTime(Integer.parseInt(eventRSVPTime.split(":")[0]),
                                    Integer.parseInt(eventRSVPTime.split(":")[1]));
                            edit_event_rsvp_by_time.setText(eventRSVPDateTime.getTextViewTime());
                            edit_event_rsvp_by_date.setText(eventRSVPDateTime.getTextViewDate());
                        }


                        if (!obj.isNull("eventDescription")) {
                            edit_event_description.setText(obj.getString("eventDescription"));
                        }

                        if (!obj.isNull("eventImageUrl")) {
                            event_image_url = obj.getString("eventImageUrl");
//                            Log.d(TAG, "event URL = " + obj.getString("eventImageUrl"));
                        }

                        //fetching image from url
//                        edit_event_image.setDefaultImageResId(R.drawable.ic_tool_bar_search);
                        if (event_image_url != null) {
                            // implement a better url checking and also test errors. Shyam!

                            edit_event_image.setImageUrl(event_image_url, AppControllerSingleton.getInstance().getImageLoader());


//
                        }
                        Log.d(TAG,"SNAPSHOT"+(AppControllerSingleton.getInstance().getLruBitmapCache().snapshot().keySet()));


                    }//if ends
                } catch (JSONException e) {
                    e.printStackTrace();
                }//try catch ends

            }//onResonse Ends
        }, new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });


        eventReq.setTag(TAG);
        // Adding request to request queue
        AppControllerSingleton.getInstance().addToRequestQueue(eventReq);


    }

    private void showTimePickerDialog(View v) {

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setListener(this);
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void showDatePickerDialog(View v) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setListener(this);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void setTime(int hourOfDay, int minute) {
//        String timeString = hourOfDay+" : "+minute;


        switch (flagForTimeSet) {

            case 1:

                eventStartDateTime.setTime(hourOfDay, minute);
                edit_event_start_time.setText(eventStartDateTime.getTextViewTime());
                break;

            case 2:

                eventEndDateTime.setTime(hourOfDay, minute);
                edit_event_end_time.setText(eventEndDateTime.getTextViewTime());
                break;

            case 3:
                if (eventRSVPDateTime == null)
                    eventRSVPDateTime = new DateTimeFormater();
                eventRSVPDateTime.setTime(hourOfDay, minute);

                edit_event_rsvp_by_time.setText(eventRSVPDateTime.getTextViewTime());
                break;
        }

        flagForTimeSet = 0;
    }


    @Override
    public void setDate(int year, int month, int day) {
        //month needs to be increased by 1 as it start by 0;
        month++;

        switch (flagForDateSet) {

            case 1:
                eventStartDateTime.setDate(year, month, day);
                edit_event_start_date.setText(eventStartDateTime.getTextViewDate());

                break;

            case 2:
                eventEndDateTime.setDate(year, month, day);
                edit_event_end_date.setText(eventEndDateTime.getTextViewDate());
                break;

            case 3:
                if (eventRSVPDateTime == null)
                    eventRSVPDateTime = new DateTimeFormater();
                eventRSVPDateTime.setDate(year, month, day);
                edit_event_rsvp_by_date.setText(eventRSVPDateTime.getTextViewDate());
                break;
        }

        flagForDateSet = 0;
    }


    //Validates data when Update button Clicked.
    private boolean validate() {
        ShowDialogueCreateEvent showDialogue;

        if (edit_event_name.getText().toString().trim().length() == 0) {
            showDialogue = new ShowDialogueCreateEvent();
            Bundle bundle = new Bundle();
            bundle.putString("title", "Validation Error");
            bundle.putString("message", "Event Name cannot be empty");
            showDialogue.setArguments(bundle);
            showDialogue.show(getFragmentManager(), "ValiationFragment");
            return false;

        } else if (edit_event_name.getText().toString().trim().length() > 100) {//Make 20 managable from config!!!{
            showDialogue = new ShowDialogueCreateEvent();
            Bundle bundle = new Bundle();
            bundle.putString("title", "Validation Error");
            bundle.putString("message", "Event Name cannot be longer than 100 characters");
            showDialogue.setArguments(bundle);
            showDialogue.show(getFragmentManager(), "ValiationFragment");
            return false;
        }

        if (!(eventStartDateTime.isSet() || eventEndDateTime.isSet())) {
            //if date or time have not been initializes then generate error
            showDialogue = new ShowDialogueCreateEvent();
            Bundle bundle = new Bundle();
            bundle.putString("title", "Validation Error");
            bundle.putString("message", "Specify Event timings");
            showDialogue.setArguments(bundle);
            showDialogue.show(getFragmentManager(), "ValiationFragment");
            return false;
        }
        if (eventStartDateTime.getCalenderInstance().before(Calendar.getInstance())) {
            showDialogue = new ShowDialogueCreateEvent();
            Bundle bundle = new Bundle();
            bundle.putString("title", "Validation Error");
            bundle.putString("message", "Event start time should be after Current Time");
            showDialogue.setArguments(bundle);
            showDialogue.show(getFragmentManager(), "ValiationFragment");
            return false;
        }
        if (eventEndDateTime.getCalenderInstance().before(eventStartDateTime.getCalenderInstance())) {
            showDialogue = new ShowDialogueCreateEvent();
            Bundle bundle = new Bundle();
            bundle.putString("title", "Validation Error");
            bundle.putString("message", "Event End Time should be after Start Time");
            showDialogue.setArguments(bundle);
            showDialogue.show(getFragmentManager(), "ValiationFragment");
            return false;
        }

        if (eventRSVPDateTime != null
                && (eventRSVPDateTime.getCalenderInstance().after(eventStartDateTime.getCalenderInstance())
                || Calendar.getInstance().after(eventRSVPDateTime.getCalenderInstance()))){

            showDialogue = new ShowDialogueCreateEvent();
            Bundle bundle = new Bundle();
            bundle.putString("title", "Validation Error");
            bundle.putString("message", "Event RSVP Time ("+eventRSVPDateTime.getSQLDateTime()+") should be before Start Time ("+eventStartDateTime.getSQLDateTime()+
                    ") and after Current Time ("+(new DateTimeFormater(Calendar.getInstance())).getSQLDateTime()+")");
            showDialogue.setArguments(bundle);
            showDialogue.show(getFragmentManager(), "ValiationFragment");
            return false;
        }
        //better to check on text view or remove data if user choses to cancel rsvp time. Shyam
        if (eventRSVPDateTime != null && !eventRSVPDateTime.isSet()) {
            showDialogue = new ShowDialogueCreateEvent();
            Bundle bundle = new Bundle();
            bundle.putString("title", "Validation Error");
            bundle.putString("message", "Event RSVP Time and Date should be set");
            showDialogue.setArguments(bundle);
            showDialogue.show(getFragmentManager(), "ValiationFragment");
            return false;
        }

        // Check if spinner was selected
        if (edit_event_venue_name.getSelectedItem() != null) {
            String selected = edit_event_venue_name.getSelectedItem().toString();
//            Toast.makeText(EditEvent.this, "Selected " + selected, Toast.LENGTH_SHORT).show();
        } else {
            // Tell User to fill in the required fields (all)
            return false;
        }

        return true;
    }

    // hide the progress dialogue;
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onFetchVenueDetails(VenueAddressSingleton.VenueArrayList<VenueAddressSingleton.VenueAddressObject> venueAddressObjects) {
        // When venue details finshes fetching
        this.venueAddressObjects = venueAddressObjects;

//        for (String key : venueAddressObjects.keySet()) {
        for (VenueAddressSingleton.VenueAddressObject venueAddressObject : this.venueAddressObjects)

        {
//            Log.d(TAG, "Spinner value" + venueAddressObject.venueName);
            spinnerArray.add(venueAddressObject.venueName);
        }
        spinnerAdapter.notifyDataSetChanged();
    }

    public static class DescriptionDialogueFragment extends DialogFragment {

        String descrption;

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        static DescriptionDialogueFragment newInstance(String description) {
            DescriptionDialogueFragment f = new DescriptionDialogueFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("description", description);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            int mNum = 6;

            // Pick a style based on the num.
            int style = DialogFragment.STYLE_NORMAL, theme = 0;
            switch ((mNum - 1) % 6) {
                case 1:
                    style = DialogFragment.STYLE_NO_TITLE;
                    break;
                case 2:
                    style = DialogFragment.STYLE_NO_FRAME;
                    break;
                case 3:
                    style = DialogFragment.STYLE_NO_INPUT;
                    break;
                case 4:
                    style = DialogFragment.STYLE_NORMAL;
                    break;
                case 5:
                    style = DialogFragment.STYLE_NORMAL;
                    break;
                case 6:
                    style = DialogFragment.STYLE_NO_TITLE;
                    break;
                case 7:
                    style = DialogFragment.STYLE_NO_FRAME;
                    break;
                case 8:
                    style = DialogFragment.STYLE_NORMAL;
                    break;
            }
            switch ((mNum - 1) % 6) {
                case 4:
                    theme = android.R.style.Theme_Holo;
                    break;
                case 5:
                    theme = android.R.style.Theme_Holo_Light_Dialog;
                    break;
                case 6:
                    theme = android.R.style.Theme_Holo_Light;
                    break;
                case 7:
                    theme = android.R.style.Theme_Holo_Light_Panel;
                    break;
                case 8:
                    theme = android.R.style.Theme_Holo_Light;
                    break;
            }
            setStyle(style, theme);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            getDialog().setTitle("Add Description");
            View view = inflater.inflate(R.layout.fragment_edit_description, container);

            final EditText edit_event_description = (EditText) view.findViewById(R.id.edit_event_description);
            descrption = getArguments().getString("description");
            if (descrption != null)
                edit_event_description.setText(descrption);

            // Watch for button clicks.
            Button button_add = (Button) view.findViewById(R.id.edit_event_description_button_add);
            button_add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // When button is clicked, call up to owning activity.
                    ((EditEvent) getActivity()).onDescriptionAdded(edit_event_description.getText().toString());
                    dismiss();
                }
            });
            Button button_cancel = (Button) view.findViewById(R.id.edit_event_description_button_cancel);
            button_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

            return view;
        }


    }

    // called when the dialogue fragment adds the descrption.
    private void onDescriptionAdded(String description) {
        edit_event_description.setText(description);
    }

    //called #1 when image upload intent returns.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                flag_image_updated_for_upload = true;
                //Setting the Bitmap to ImageView

                edit_event_image.setImageBitmap(bitmap);
//                edit_event_image.invalidate();



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // convert image to string before sending it to server
    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        menu.removeItem(R.id.menu_plus_for_new_event);
        menu.removeItem(R.id.menu_messages);

        return super.onCreateOptionsMenu(menu);

        }
}

