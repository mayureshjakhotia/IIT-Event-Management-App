package com.cs442.group7.eventiit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cs442.group7.eventiit.helper.SessionManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static VenueAddressSingleton.VenueArrayList venueArrayList;

    private ArrayList<EventItemModel> eventItemModelArrayList;
    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;
    HashMap<String, LatLng> popukated = new HashMap<String, LatLng>();

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
        setContentView(R.layout.activity_map);

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        loadPreferences();

        venueArrayList = HomeScreen.venueArrayList;
        eventItemModelArrayList = HomeScreen.eventItemModelArrayList;


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
        top_past_events.setOnClickListener(this);

        // Bottom Options Bar Button - Map is clicked
        Button bottom_map = (Button) findViewById(R.id.bottom_map);
        bottom_map.setTextColor(Color.parseColor("#FFFFFF"));


        Button bottom_profile = (Button) findViewById(R.id.bottom_profile);
        bottom_profile.setOnClickListener(this);

        Button bottom_events = (Button) findViewById(R.id.bottom_events);
        bottom_events.setOnClickListener(this);

        // Updated by MJ to work with non-Hawk users
        final Button top_my_hosting_events = (Button) findViewById(R.id.top_my_hosting);
        if (SessionManager.isHawkUser()) {
            top_my_hosting_events.setOnClickListener(this);

        }
        else {
            top_my_hosting_events.setVisibility(View.GONE);
        }

       /* // Bottom Options Bar Button - Events is clicked
        Button bottom_events = (Button) findViewById(R.id.bottom_events);
        bottom_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent featuredEventsIntent = new Intent(MapsActivity.this, HomeScreen.class);
                startActivity(featuredEventsIntent);
            }
        });

        // Bottom Options Bar Button - Map is clicked
        Button bottom_profile = (Button) findViewById(R.id.bottom_profile);
        bottom_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(MapsActivity.this, Profile.class);
                startActivity(profileIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_map);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, my_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation Drawer Listener
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_featured_events) {
                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_all_events) {

                    Toast.makeText(getApplicationContext(), "All Events clicked", Toast.LENGTH_SHORT).show();

                }
                else if (id == R.id.nav_registered_events) {

                    Toast.makeText(getApplicationContext(), "Registered Events clicked", Toast.LENGTH_SHORT).show();

                }
                else if (id == R.id.nav_past_events) {

                    Toast.makeText(getApplicationContext(), "Past Events clicked", Toast.LENGTH_SHORT).show();

                }
                else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_settings) {
                    Intent intent = new Intent(getApplicationContext(), AppSettings.class);
                    startActivity(intent);

                }
                else if (id == R.id.nav_share) {

                    Toast.makeText(getApplicationContext(), "Share option clicked", Toast.LENGTH_SHORT).show();

                }
                else if (id == R.id.nav_report_issue) {

                    Toast.makeText(getApplicationContext(), "Report Issue clicked", Toast.LENGTH_SHORT).show();

                }
*//*
                DrawerLayout mDrawerLayout;
                mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_home_screen);
                mDrawerLayout.closeDrawers();*//*

                return true;
            }
        });
*/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the com.cs442.group7.eventiit.app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera.
        HashMap<String, LatLng> hm = new HashMap<String, LatLng>();
        hm.put("abc", new LatLng(41.833652, -87.628294));
        hm.put("abb", new LatLng(41.833668, -87.628294));
        hm.put("def",new LatLng(-36,156));
        //LatLng sydney = new LatLng(-37.05, 158.05);
        HashSet<String> se = new HashSet<String>();


        for (int i = 0; i < eventItemModelArrayList.size(); i++) {

            //Toast.makeText(this, "Event start time is : "+eventItemModelArrayList.get(i).getEventStartDateTime()
            // +"\n End time is : "+eventItemModelArrayList.get(i).getEventEndDateTime(), Toast.LENGTH_SHORT).show();
            Double lat=Double.parseDouble(venueArrayList.getObjectByID(eventItemModelArrayList.get(i).getEventVenueID()).venueLatitude); //= Double.parseDouble(venueArrayList.getObjectByID(eventItemModelArrayList.get(i).getEventVenueID()).venueLatitude);
            Double lon = Double.parseDouble(venueArrayList.getObjectByID(eventItemModelArrayList.get(i).getEventVenueID()).venueLongitude);
            String title = venueArrayList.getObjectByID(eventItemModelArrayList.get(i).getEventVenueID()).venueName;//= Double.parseDouble(venueArrayList.getObjectByID(eventItemModelArrayList.get(i).getEventVenueID()).venueLongitude);
             /* mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(eventItemModelArrayList.get(i).getEventName()).icon(BitmapDescriptorFactory
                                         .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
*/
//            Toast.makeText(this, "First : "+venueArrayList.size(), Toast.LENGTH_SHORT).show();
            //           Toast.makeText(this, "First : "+eventItemModelArrayList.size(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Venue is : "+venueArrayList.getNameByIndex(i), Toast.LENGTH_SHORT).show();
            if(se.contains(title)){
                lat=lat+0.00009;
                lon=lon+0.00009                                                                                             ;
                if (Calendar.getInstance().after(eventItemModelArrayList.get(i).getEventStartDateTime()) && eventItemModelArrayList.get(i).getEventEndDateTime().after(Calendar.getInstance())
                /*Calendar.getInstance().before(eventItemModelArrayList.get(i).getEventEndDateTime())*/ ){


                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(eventItemModelArrayList.get(i).getEventName()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }else if(eventItemModelArrayList.get(i).getEventEndDateTime().after(Calendar.getInstance()/*Calendar.getInstance().before(eventItemModelArrayList.get(i).getEventStartDateTime())*/)){


                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(eventItemModelArrayList.get(i).getEventName()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
            }else{
                if (Calendar.getInstance().after(eventItemModelArrayList.get(i).getEventStartDateTime()) && eventItemModelArrayList.get(i).getEventEndDateTime().after(Calendar.getInstance())
                /*Calendar.getInstance().before(eventItemModelArrayList.get(i).getEventEndDateTime())*/ ){

                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(eventItemModelArrayList.get(i).getEventName()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }else if(eventItemModelArrayList.get(i).getEventEndDateTime().after(Calendar.getInstance()/*Calendar.getInstance().before(eventItemModelArrayList.get(i).getEventStartDateTime())*/)){


                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(eventItemModelArrayList.get(i).getEventName()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
                se.add(title);
            }

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
    protected void onRestart() {
        loadPreferences();
        super.onRestart();
    }


}