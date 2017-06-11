package com.cs442.group7.eventiit;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cs442.group7.eventiit.app.AppControllerSingleton;
import com.cs442.group7.eventiit.app.Config;
import com.cs442.group7.eventiit.helper.SessionManager;
import com.cs442.group7.eventiit.util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mayureshjakhotia on 11/18/16.
 */

public class PastEvents extends BaseActivity implements View.OnClickListener,UploadDownloadUtil.GenericListener {

    // Log tag and android volley request object tag
    private static final String TAG = HomeScreen.class.getSimpleName();
    //    private String url = "http://10.0.2.2:8888/PhpProject1/get_all_records.php";
    private static String url = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/get_past_events.php";

    public static ArrayList<EventItemModel> eventItemModelArrayList = new ArrayList<EventItemModel>();

    private ProgressDialog pDialog;

    private ListViewAdapter adapter;

    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public void setContentView(int layoutResID) {
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);  // The base layout
        subActivityContent = (RelativeLayout) fullLayout.findViewById(R.id.content_frame);            // The frame layout where the activity content is placed.
        getLayoutInflater().inflate(layoutResID, subActivityContent, true);            // Places the activity layout inside the activity content frame.
        super.setContentView(fullLayout);                                                       // Sets the content view as the merged layouts.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        loadPreferences();

        Intent searchIntent = getIntent();
        if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {

            String query = searchIntent.getStringExtra(SearchManager.QUERY);

            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_home_screen);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, my_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation Drawer Listener
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Top Options Bar Button - Featured Events is clicked
        Button top_featured_events = (Button) findViewById(R.id.top_featured_events);
        top_featured_events.setOnClickListener(this);

        // Top Options Bar Button - All Events is clicked
        final Button top_all_events = (Button) findViewById(R.id.top_all_events);
        top_all_events.setOnClickListener(this);

        // Top Options Bar Button - Registered Events is clicked
        Button top_registered_events = (Button) findViewById(R.id.top_registered_events);
        top_registered_events.setOnClickListener(this);

        // Top Options Bar Button - Past Events is clicked
        final Button top_past_events = (Button) findViewById(R.id.top_past_events);
//        top_past_events.setOnClickListener(this);

        // Bottom Options Bar Button - Map is clicked
        Button bottom_map = (Button) findViewById(R.id.bottom_map);
        bottom_map.setOnClickListener(this);

        Button bottom_profile = (Button) findViewById(R.id.bottom_profile);
        bottom_profile.setOnClickListener(this);

        Button bottom_events = (Button) findViewById(R.id.bottom_events);
        bottom_events.setTextColor(Color.parseColor("#FFFFFF"));

        //Added By Shyam
        // Updated by MJ to work with non-Hawk users
        final Button top_my_hosting_events = (Button) findViewById(R.id.top_my_hosting);
        if (SessionManager.isHawkUser()) {
            top_my_hosting_events.setOnClickListener(this);

        }
        else {
            top_my_hosting_events.setVisibility(View.GONE);
        }

        final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);

/*
                top_past_events.setBackgroundColor(Color.parseColor("#B2DFDB"));
*/
                top_past_events.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });

        ListView listView = (ListView) findViewById(R.id.listview_this_week);
        adapter = new ListViewAdapter(this, R.layout.custom_listview_events, eventItemModelArrayList, AppControllerSingleton.getInstance().getImageLoader());
        listView.setAdapter(adapter);

        //Load from online database
        readFromDB();

        // To get event details on clicking it (call implemented in ListViewAdapter)
        adapter.onClick(listView);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications to firebase server...
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), " " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

    }

    private void readFromDB() {
        eventItemModelArrayList.clear();
        UploadDownloadUtil util= new UploadDownloadUtil(this);
        Map<String, String> param = new HashMap<>();
        param.put("userEmail", SessionManager.getUserEmail());
        String[] returnKeys = {"eventID","eventName","eventImageUrl","eventStartDateTime","eventEndDateTime","eventRsvpBy", "eventVenueID","eventVenueOtherDetails","eventDescription","isUserRsvped"};


        util.uploadDownloadGenericData(param,returnKeys,"events",this,url,null);
    }

    @Override
    public void onTaskComplete(boolean success, List<Map<String, String>> response, boolean error, String passThroughParam) {

        if(success) {

            for(Map<String, String> eventParam : response) {
                EventItemModel eventItem = new EventItemModel(eventParam.get("eventID"),eventParam.get("eventName"),eventParam.get("eventImageUrl"),
                        eventParam.get("eventStartDateTime"),eventParam.get("eventEndDateTime"),eventParam.get("eventRsvpBy"),
                        eventParam.get("eventVenueID"),eventParam.get("eventVenueOtherDetails"),eventParam.get("eventDescription"));
                eventItem.setUserRsvped(eventParam.get("isUserRsvped"));
                eventItemModelArrayList.add(eventItem);
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);


        if (!SessionManager.isHawkUser()) {
            menu.removeItem(R.id.menu_plus_for_new_event);


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();

            nav_Menu.findItem(R.id.nav_my_hosted_events).setVisible(false);

        }

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //Delete all the pending request on Stop.
        if (AppControllerSingleton.getInstance() != null) {
            AppControllerSingleton.getInstance().cancelPendingRequests("TAG");
        }
        hidePDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    protected void onRestart() {
//        adapter.notifyDataSetChanged();
        loadPreferences();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver NOT NEEDED Though
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        //important
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        //registering receiver from backgorund.. (this keyword!!!)

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
