package com.cs442.group7.eventiit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.cs442.group7.eventiit.app.AppControllerSingleton;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewEvent extends BaseActivity implements VenueAddressSingleton.Listener,UploadDownloadUtil.Listener {

    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;
    protected LinearLayout optionsBarTop;
    protected LinearLayout optionsBarBottom;

    private NetworkImageView view_event_image;
    private TextView view_event_name;
    private TextView view_event_start_date;
    private TextView view_event_start_time;
    private TextView view_event_end_date;
    private TextView view_event_end_time;
    private TextView view_event_rsvp_by_date;
    private TextView view_event_rsvp_by_time;
    private TextView view_event_venue_name;
    private TextView view_event_other_details;
    private TextView view_event_description;
    private TextView view_event_address;

    private static Button button_rsvp_event;
    private static Button button_past_event_title;
    private FloatingActionButton button_add_to_calender;

    String rsvpURL = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/update_rsvp.php";
    static String rsvpStatusUrl = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/is_user_rsvp_in_event.php";

    private static boolean isUserRsvped;

    EventItemModel eim;

    private int position;

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
        setContentView(R.layout.activity_view_event);



        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        loadPreferences();

//        VenueAddressSingleton.getInstance(this).startJob();

        // Navigation Drawer Listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        eim = (EventItemModel) getIntent().getSerializableExtra("EventDetails");
        position  = getIntent().getIntExtra("position",-1);

        //initialize view variables
        view_event_image = (NetworkImageView) findViewById(R.id.view_event_image);
        view_event_name = (TextView) findViewById(R.id.view_event_name);
        view_event_start_date = (TextView) findViewById(R.id.view_event_start_date);
        view_event_start_time = (TextView) findViewById(R.id.view_event_start_time);
        view_event_end_date = (TextView) findViewById(R.id.view_event_end_date);
        view_event_end_time = (TextView) findViewById(R.id.view_event_end_time);
        view_event_rsvp_by_date = (TextView) findViewById(R.id.view_event_rsvp_by_date);
        view_event_rsvp_by_time = (TextView) findViewById(R.id.view_event_rsvp_by_time);
        view_event_venue_name = (TextView) findViewById(R.id.view_event_venue_name);
        view_event_other_details = (TextView) findViewById(R.id.view_event_other_details);
        view_event_description = (TextView) findViewById(R.id.view_event_description);
        view_event_address = (TextView) findViewById(R.id.view_event_venue_address);

        isUserRsvped = eim.isUserRsvped();
        button_rsvp_event = (Button) findViewById(R.id.button_rsvp_event);
        button_past_event_title = (Button) findViewById(R.id.button_past_event_title);


        // To change RSVP button or hide it to show event is over in case of Past Events
//        button_past_event_title.setVisibility(View.GONE);
        if (!(getIntent().getStringExtra("isPastEvent") == null)) {

            button_rsvp_event.setVisibility(View.GONE);
            button_past_event_title.setBackgroundColor(Color.parseColor("#3F51B5"));
            button_past_event_title.setTextColor(Color.WHITE);
        }
        else if(eim.getEventRSVPDateTime()!=null&&eim.getEventRSVPDateTime().before(Calendar.getInstance())){

            button_rsvp_event.setVisibility(View.GONE);
            button_past_event_title.setText("RSVP Date Passed.");
            button_past_event_title.setBackgroundColor(Color.parseColor("#3F51B5"));
            button_past_event_title.setTextColor(Color.WHITE);

            GetRsvpStatus getRsvpStatus = new GetRsvpStatus();
            getRsvpStatus.startJob(super.getEmail(),eim.getEventID(), true);
        }
        else
        {
            button_past_event_title.setVisibility(View.GONE);
            GetRsvpStatus getRsvpStatus = new GetRsvpStatus();
            getRsvpStatus.startJob(super.getEmail(),eim.getEventID(),false);
        }
//        else if (!isUserRsvped) {
//            button_rsvp_event.setBackgroundColor(Color.WHITE);
//            button_rsvp_event.setText("RSVP To Event");
//            button_rsvp_event.setTextColor(Color.parseColor("#3F51B5"));
//            button_past_event_title.setVisibility(View.GONE);
//        } else {
//            button_rsvp_event.setBackgroundColor(Color.parseColor("#3F51B5"));
//            button_rsvp_event.setText("You have RSVPed");
//            button_rsvp_event.setTextColor(Color.WHITE);
//            button_past_event_title.setVisibility(View.GONE);
//        }


        if (!eim.getEventIconURL().trim().equalsIgnoreCase("null")) {
            view_event_image.setImageUrl(eim.getEventIconURL(), AppControllerSingleton.getInstance().getImageLoader());
        }
        view_event_name.setText(eim.getEventName());

        view_event_start_date.setText(eim.getStartDateFormatted());
        view_event_start_time.setText(eim.getEventStartTime());

        view_event_end_date.setText(eim.getEndDateFormatted());
        view_event_end_time.setText(eim.getEventEndTime());

        if (!eim.getRSVPDateFormatted().trim().equalsIgnoreCase("false")) {
            view_event_rsvp_by_date.setText(eim.getRSVPDateFormatted());
            view_event_rsvp_by_time.setText(eim.getRSVPTime());
        }


        if (!eim.getEventVenueOtherDetails().trim().equalsIgnoreCase("")) {
            view_event_other_details.setText(eim.getEventVenueOtherDetails());
        }
        else {
            view_event_other_details.setText("(No other details available.)");
        }

        if (!eim.getEventDescription().trim().equalsIgnoreCase("null")) {
            view_event_description.setText(eim.getEventDescription());
        }
        else {
            view_event_description.setText("No Description Provided For This Event.");
        }

        // RSVP Button
        button_rsvp_event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UploadDownloadUtil  uploadDownloadUtil= new UploadDownloadUtil(ViewEvent.this);
                uploadDownloadUtil.uploadRsvpState(!isUserRsvped,ViewEvent.super.getEmail(),eim.getEventID(),ViewEvent.this,rsvpURL);
            }
        });

        button_add_to_calender = (FloatingActionButton) findViewById(R.id.button_add_to_calender);
        button_add_to_calender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");

                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eim.getEventStartDateTime().getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,eim.getEventEndDateTime().getTimeInMillis());
//                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                intent.putExtra(CalendarContract.Events.TITLE, eim.getEventName());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, eim.getEventDescription());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eim.getEventVenueOtherDetails());
                intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, "America/Chicago");

                startActivity(intent);
            }
        });



//        Toast.makeText(this,"Somthing went Wrong",Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        menu.removeItem(R.id.menu_plus_for_new_event);
        menu.removeItem(R.id.menu_messages);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onRestart() {
        loadPreferences();
        super.onRestart();
    }

    @Override
    protected void onStart() {
        VenueAddressSingleton.getInstance(this).startJob();
        super.onStart();
    }


    @Override
    public void onFetchVenueDetails(VenueAddressSingleton.VenueArrayList<VenueAddressSingleton.VenueAddressObject> venueAddressObjects) {
        view_event_venue_name.setText(venueAddressObjects.getObjectByID(eim.getEventVenueID()).venueName);
        view_event_address.setText(venueAddressObjects.getObjectByID(eim.getEventVenueID()).venueAddress);
    }


    @Override
    public void onTaskComplete(boolean success) {
        if(success){
            isUserRsvped = !isUserRsvped;

//            Toast.makeText(this,String.valueOf(isUserRsvped),Toast.LENGTH_SHORT).show();
//            eim.setUserRsvped(String.valueOf(isUserRsvped));


            //VERY BAD Design Needed to refactor!!!!!!.
            // Changed by MJ to use from HomeScreen
//            HomeScreen.eventItemModelArrayList.get(position).setUserRsvped(String.valueOf(isUserRsvped));


            if (!isUserRsvped) {
                button_rsvp_event.setBackgroundColor(Color.WHITE);
                button_rsvp_event.setText("RSVP To Event");
                button_rsvp_event.setTextColor(Color.parseColor("#3F51B5"));
//             button_rsvp_event.setBackgroundColor(Color.WHITE);
                //Do Something
            } else {
                button_rsvp_event.setBackgroundColor(Color.parseColor("#3F51B5"));
                button_rsvp_event.setText("You have RSVPed");
                button_rsvp_event.setTextColor(Color.WHITE);
            }

        }
        else {
            Toast.makeText(this,"Something went Wrong",Toast.LENGTH_SHORT).show();
        }
    }

    private  static class GetRsvpStatus implements UploadDownloadUtil.GenericListener {

        public void startJob(String userEmail, String eventID, boolean rsvpDatePassed) {
            UploadDownloadUtil uploadDownloadUtil = new UploadDownloadUtil(this);
            Map<String, String> param = new HashMap<>();
            param.put("userEmail", userEmail);
            param.put("eventID", eventID);
            String[] returnKeys = {"isUserRsvp"};
//            Log.e("ViewEvent", "userEmail "+userEmail + " evenId "+eventID);
            uploadDownloadUtil.uploadDownloadGenericData(param, returnKeys, "userRsvp", null, rsvpStatusUrl, String.valueOf(rsvpDatePassed));

        }

        @Override
        public void onTaskComplete(boolean success, List<Map<String, String>> response, boolean error, String passThroughParam) {
            if (success) {
//                Log.e("ViewEvent", "userRsvp "+response.get(0).get("isUserRsvp")+ "passThrughParam " +passThroughParam);
                if (response.size()!=0&&response.get(0).get("isUserRsvp").equals("1"))
                    isUserRsvped = true;
                else isUserRsvped = false;

                    if (!isUserRsvped) {
                        button_rsvp_event.setBackgroundColor(Color.WHITE);
                        button_rsvp_event.setText("RSVP To Event");
                        button_rsvp_event.setTextColor(Color.parseColor("#3F51B5"));


                    } else {
                        button_rsvp_event.setBackgroundColor(Color.parseColor("#3F51B5"));
                        button_rsvp_event.setText("You have RSVPed");
                        button_rsvp_event.setTextColor(Color.WHITE);
                        if(passThroughParam.equalsIgnoreCase("true")) {
                            button_past_event_title.setText("RSVP Date Passed. You have Registered for the Event");
//
                        }

                    }
            }
        }
    }
}
