package com.cs442.group7.eventiit;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cs442.group7.eventiit.app.AppControllerSingleton;
import com.cs442.group7.eventiit.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shyam on 10/28/16.
 */

public class MyHostingActivity extends BaseActivity {

    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    ArrayList<EventItemModel> eventList;

    private ProgressDialog pDialog;

    private static String url = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/get_my_hosting_events.php";
    private static final String TAG = MyHostingActivity.class.getSimpleName();
    private static String userEmail;

    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;
    protected LinearLayout optionsBarTop;
    protected LinearLayout optionsBarBottom;

    @Override
    public void setContentView(int layoutResID) {
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);    // The base layout
        subActivityContent = (RelativeLayout) fullLayout.findViewById(R.id.content_frame);          // The frame layout where the activity content is placed.
        optionsBarTop = (LinearLayout) fullLayout.findViewById(R.id.options_bar_top);
//        optionsBarTop.setVisibility(View.GONE);
        optionsBarBottom = (LinearLayout) fullLayout.findViewById(R.id.options_bar_bottom);
//        optionsBarBottom.setVisibility(View.GONE);
        getLayoutInflater().inflate(layoutResID, subActivityContent, true);                         // Places the activity layout inside the activity content frame.
        super.setContentView(fullLayout);                                                           // Sets the content view as the merged layouts.
    }

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // enable transitions
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_view_my_hosting);


        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        setTitle("My Hosted Events");

        loadPreferences();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_home_screen);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, my_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation Drawer Listener
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Added Shyam
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
        top_past_events.setOnClickListener(this);

        //Added By Shyam
        final Button top_my_hosting_events = (Button) findViewById(R.id.top_my_hosting);
//        top_my_hosting_events.setOnClickListener(this);

        final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);


//                .setBackgroundColor(Color.parseColor("#B2DFDB"));

                top_my_hosting_events.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });

        //finished Shyam



        // Bottom Options Bar Button - Events is clicked
        Button bottom_events = (Button) findViewById(R.id.bottom_events);
        bottom_events.setTextColor(Color.parseColor("#FFFFFF"));
//        bottom_events.setOnClickListener(this);

        // Bottom Options Bar Button - Map is clicked
        Button bottom_map = (Button) findViewById(R.id.bottom_map);
        bottom_map.setOnClickListener(this);

        Button bottom_profile = (Button) findViewById(R.id.bottom_profile);
        bottom_profile.setOnClickListener(this);

        userEmail=super.getEmail();

        mRecyclerView = (RecyclerView) findViewById(R.id.view_my_hosting);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        eventList = new ArrayList<EventItemModel>();
        mAdapter = new MyHostingAdapter(eventList, this,
                AppControllerSingleton.getInstance().getImageLoader(),R.layout.grid_cardview_my_hosting);

        // use a linear layout manager
        mLayoutManager= new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);


//        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
//        pDialog.setMessage("Loading...");
//        pDialog.show();

        //Load from online database
//        readFromDB();

    }

    private void readFromDB() {
        pDialog.show();

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject("{\"userEmail\":\"" + MyHostingActivity.userEmail + "\"}");
            Log.d(TAG,"JSON Object = "+jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Creating volley request obj
        JsonObjectRequest eventReq = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        hidePDialog();

                        // Parsing json
                        try {
                            int success = response.getInt("success");
                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("events");
                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject obj = ja.getJSONObject(i);
                                    EventItemModel eventItem = new EventItemModel
                                            (obj.getString("eventID"),obj.getString("eventName"),
                                                    obj.getString("eventImageUrl"),
                                                    obj.getString("eventStartDateTime"),
                                                    obj.getString("eventEndDateTime"),
                                                    obj.getString("eventRsvpBy"),
                                                    obj.getString("eventVenueID"),
                                                    obj.getString("eventVenueOtherDetails"),
                                                    obj.getString("eventDescription"));

                                    Log.d(TAG, obj.getString("eventName"));
                                    // adding eventitem to event array
                                    eventList.add(eventItem);
                                }//for loop ends
                            }//if ends
                            else if(success==0){
                                Log.d(TAG, "Error fetching data");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }//try catch ends

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        mAdapter.notifyDataSetChanged();

                    }
                }
                , new Response.ErrorListener()

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

    public void onSomeButtonClicked(View view) {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setExitTransition(new Explode());
//        }
//
//        Intent intent = new Intent(this, MyOtherActivity.class);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            startActivity(intent,
//                    ActivityOptions
//                            .makeSceneTransitionAnimation(this).toBundle());
//        }


    }
    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

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
    protected void onResume()
    {
        super.onResume();
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        readFromDB();
//        Toast.makeText(this,"ONRESumE", Toast.LENGTH_SHORT);
//        Log.e(TAG,"onresume")

    }

    @Override
    protected void onRestart() {
//        adapter.notifyDataSetChanged();
        // To reload
        finish();
        startActivity(getIntent());

        loadPreferences();
        super.onRestart();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    //Testing left Shyam.!
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    //Added by shyam.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);


        if (!SessionManager.isHawkUser()) {
            menu.removeItem(R.id.menu_plus_for_new_event);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();

            nav_Menu.findItem(R.id.nav_my_hosted_events).setVisible(false);
/*
            menu.removeItem(R.id.menu_my_hosted_events);
*/
        }

        return super.onCreateOptionsMenu(menu);

    }


}