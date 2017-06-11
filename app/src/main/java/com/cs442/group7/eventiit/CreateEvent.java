package com.cs442.group7.eventiit;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateEvent extends BaseActivity implements TimePickerFragment.Listener, DatePickerFragment.Listener, VenueAddressSingleton.Listener {

    //set the user id of the person logged in here!! Later
    private String userEmail;
    private static final String TAG = CreateEvent.class.getSimpleName();

    DateTimeFormater eventStartDateTime;
    DateTimeFormater eventEndDateTime;
    DateTimeFormater eventRSVPDateTime;


    //0 for start , 1 for end and 2 for rsvp
    private int flagForTimeSet = 0;
    private int flagForDateSet = 0;

    private ProgressDialog pDialog;
    //    private static String url_create_event = "http://10.0.2.2:8888/PhpProject1/create_event.php";
    private static String url_create_event = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/create_event.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    private VenueAddressSingleton.VenueArrayList<VenueAddressSingleton.VenueAddressObject> venueAddressObjects;

    ArrayAdapter<String> spinnerAdapter;
    private List<String> spinnerArray;
    private Spinner create_event_venue_name;
    private Spinner create_event_category_name;

    private EditText create_event_location;

    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;
    protected LinearLayout optionsBarTop;
    protected LinearLayout optionsBarBottom;

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
        setContentView(R.layout.activity_create_event);

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        getSupportActionBar().setTitle("Events - Illinois Tech");

        loadPreferences();

        // Navigation Drawer Listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userEmail=super.getEmail();

        Button create_event_start_time = (Button) findViewById(R.id.create_event_start_time);
        Button create_event_end_time = (Button) findViewById(R.id.create_event_end_time);
        Button create_event_rsvp_by_time = (Button) findViewById(R.id.create_event_rsvp_by_time);

        Button create_event_start_date = (Button) findViewById(R.id.create_event_start_date);
        Button create_event_end_date = (Button) findViewById(R.id.create_event_end_date);
        Button create_event_rsvp_by_date = (Button) findViewById(R.id.create_event_rsvp_by_date);

        final EditText create_event_venue_other_detail =  (EditText)findViewById(R.id.create_event_venue_other_detail);

        create_event_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                flagForTimeSet = 1;
            }
        });

        create_event_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                flagForTimeSet = 2;
            }
        });

        create_event_rsvp_by_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                flagForTimeSet = 3;
            }
        });

        create_event_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                flagForDateSet = 1;
            }
        });

        create_event_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                flagForDateSet = 2;
            }
        });

        create_event_rsvp_by_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                flagForDateSet = 3;
            }
        });

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

        create_event_venue_name = (Spinner) findViewById(R.id.create_event_venue_name);
        create_event_venue_name.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        create_event_venue_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                create_event_location.setText(venueAddressObjects.get(position).venueAddress);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do something
            }
        });

        //Comment to be added SHYAM!!
        VenueAddressSingleton.getInstance(this).startJob();


        final EditText create_event_name = (EditText) findViewById(R.id.create_event_name);
        create_event_location = (EditText) findViewById(R.id.create_event_location);


        Button button_create_event = (Button) findViewById(R.id.button_create_event);
        button_create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("CreateEvent dateTime", eventStartDateTime.getSQLDateTime());

                if (validationCheck(create_event_name, create_event_location, create_event_venue_name)) {
                    new CreateNewEvent().execute(create_event_name.getText().toString(),
                            eventStartDateTime.getSQLDateTime(),
                            eventEndDateTime.getSQLDateTime(),((eventRSVPDateTime == null) ? null : eventRSVPDateTime.getSQLDate() + " " + eventRSVPDateTime.getSQLTime()),
                            String.valueOf(venueAddressObjects.getIndexByVenueName(create_event_venue_name.getSelectedItem().toString())),
                            create_event_category_name.getSelectedItem().toString(),create_event_venue_other_detail.getText().toString());

//                            create_event_location.getText().toString(),
//                            create_event_venue_name.getSelectedItem().toString());
                }
            }
        });

        List<String> spinnerArrayForCategory =  new ArrayList<String>();
        spinnerArrayForCategory.add("Computer Science");
        spinnerArrayForCategory.add("Festival");
        spinnerArrayForCategory.add("Deals");
        spinnerArrayForCategory.add("Sports");
        spinnerArrayForCategory.add("Music");
        spinnerArrayForCategory.add("Food");
        spinnerArrayForCategory.add("Arts");
        spinnerArrayForCategory.add("Science");
        spinnerArrayForCategory.add("Architecture");
        spinnerArrayForCategory.add("Career");
        spinnerArrayForCategory.add("International Students");
        spinnerArrayForCategory.add("Others");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArrayForCategory);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        create_event_category_name = (Spinner) findViewById(R.id.create_event_category_name);
        create_event_category_name.setAdapter(adapter);
/*        create_event_category_name = (Spinner) findViewById(R.id.create_event_category_name);
        create_event_category_name.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        create_event_category_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected event is " + venueAddressObjects.get(Integer.parseInt(String.valueOf(parent.getSelectedItemId()))), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do something
            }
        });*/

    }

    @Override
    public void onFetchVenueDetails(VenueAddressSingleton.VenueArrayList<VenueAddressSingleton.VenueAddressObject> venueAddressObjects) {

        this.venueAddressObjects = venueAddressObjects;

        for (VenueAddressSingleton.VenueAddressObject venueAddressObject : venueAddressObjects) {
            spinnerArray.add(venueAddressObject.venueName);
        }
        spinnerAdapter.notifyDataSetChanged();
    }


    class CreateNewEvent extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateEvent.this);
            pDialog.setMessage("Creating Event..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            String eventName = args[0];
            String eventStartDateTime = args[1];
            String eventEndDateTime = args[2];
            String eventRSVPBy = args[3];
            String venueID = args[4];
            String eventVenueOtherDetails = args[6];

            // Added by MJ -->
            String eventCategory = args[5];

//            String eventLocation = args[4];
//            String eventVenue = args[5];

            JSONParser jsonParser = new JSONParser();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("eventName", eventName));
//           params.add(new BasicNameValuePair("description", eventDescription));
//            params.add(new BasicNameValuePair("eventLocation", eventLocation));
//            if (!(eventVenue == null || eventVenue.trim().length() == 0)) {
//                params.add(new BasicNameValuePair("eventVenueName", eventVenue));
//            }

            if (!(eventRSVPBy == null)) {
                params.add(new BasicNameValuePair("eventRsvpBy", eventRSVPBy));
            }

            params.add(new BasicNameValuePair("eventStartDateTime", eventStartDateTime));
            params.add(new BasicNameValuePair("eventEndDateTime", eventEndDateTime));
            params.add(new BasicNameValuePair("eventCreatedBy", userEmail));
            params.add(new BasicNameValuePair("eventVenueID", venueID));
            params.add(new BasicNameValuePair("eventVenueOtherDetails", eventVenueOtherDetails));

//            Log.e(TAG, "event Venue ID"+venueID+eventVenueOtherDetails);


            // Added by MJ to add category in event_category table via query inside create_event.php
            params.add(new BasicNameValuePair("eventCategory", eventCategory));



            // getting JSON Object
            // Note that create product url accepts POST method

            JSONObject json = jsonParser.makeHttpRequest(url_create_event, "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
//                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
//                    startActivity(i);

                    // closing this screen
                    Log.d("Success", "Data Inserted");
                    finish();
                } else {
                    // failed to create event
                    Log.d("Failure", "Data Not Inserted !!! BAD");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.options_menu, menu);

        menu.removeItem(R.id.menu_plus_for_new_event);
        menu.removeItem(R.id.menu_messages);

        return super.onCreateOptionsMenu(menu);

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
                if (eventStartDateTime == null)
                    eventStartDateTime = new DateTimeFormater();
                eventStartDateTime.setTime(hourOfDay, minute);
                Button create_event_start_time = (Button) findViewById(R.id.create_event_start_time);
                create_event_start_time.setText(eventStartDateTime.getTextViewTime());
                break;

            case 2:
                if (eventEndDateTime == null)
                    eventEndDateTime = new DateTimeFormater();
                eventEndDateTime.setTime(hourOfDay, minute);
                Button create_event_end_time = (Button) findViewById(R.id.create_event_end_time);
                create_event_end_time.setText(eventEndDateTime.getTextViewTime());
                break;

            case 3:
                if (eventRSVPDateTime == null)
                    eventRSVPDateTime = new DateTimeFormater();
                eventRSVPDateTime.setTime(hourOfDay, minute);
                Button create_event_rsvp_by_time = (Button) findViewById(R.id.create_event_rsvp_by_time);
                create_event_rsvp_by_time.setText(eventRSVPDateTime.getTextViewTime());
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
                if (eventStartDateTime == null)
                    eventStartDateTime = new DateTimeFormater();
                eventStartDateTime.setDate(year, month, day);
                Button create_event_start_date = (Button) findViewById(R.id.create_event_start_date);
                create_event_start_date.setText(eventStartDateTime.getTextViewDate());

                break;

            case 2:
                if (eventEndDateTime == null)
                    eventEndDateTime = new DateTimeFormater();
                eventEndDateTime.setDate(year, month, day);
                Button create_event_end_date = (Button) findViewById(R.id.create_event_end_date);
                create_event_end_date.setText(eventEndDateTime.getTextViewDate());
                break;

            case 3:
                if (eventRSVPDateTime == null)
                    eventRSVPDateTime = new DateTimeFormater();
                eventRSVPDateTime.setDate(year, month, day);
                Button create_event_rsvp_by_date = (Button) findViewById(R.id.create_event_rsvp_by_date);
                create_event_rsvp_by_date.setText(eventRSVPDateTime.getTextViewDate());

                break;
        }

        flagForDateSet = 0;
    }

    // Testing left
    private boolean validationCheck(EditText create_event_name, EditText create_event_location, Spinner create_event_venue_name) {

        ShowDialogueCreateEvent showDialogue;

        if (create_event_name.getText().toString().trim().length() == 0) {
            showDialogue = new ShowDialogueCreateEvent();
            Bundle bundle = new Bundle();
            bundle.putString("title", "Validation Error");
            bundle.putString("message", "Event Name cannot be empty");
            showDialogue.setArguments(bundle);
            showDialogue.show(getFragmentManager(), "ValiationFragment");
            return false;

        } else if (create_event_name.getText().toString().trim().length() > 100) {//Make 20 managable from config!!!{
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
        if (create_event_venue_name.getSelectedItem() != null) {
            String selected = create_event_venue_name.getSelectedItem().toString();
//            Toast.makeText(CreateEvent.this, "Selected " + selected, Toast.LENGTH_SHORT).show();
        } else {
            // Tell User to fill in the required fields (all)
            return false;
        }

        return true;

    }


}
