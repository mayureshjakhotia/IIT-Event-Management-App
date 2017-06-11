package com.cs442.group7.eventiit;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cs442.group7.eventiit.app.Config;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.cs442.group7.eventiit.helper.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends BaseActivity implements UploadDownloadUtil.GenericListener{

    private SessionManager session;

    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;
    protected LinearLayout optionsBarTop;
    protected LinearLayout optionsBarBottom;

    private UploadDownloadUtil util;

    @Override
    public void setContentView(int layoutResID) {
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);    // The base layout
        subActivityContent = (RelativeLayout) fullLayout.findViewById(R.id.content_frame);          // The frame layout where the activity content is placed.
        optionsBarTop = (LinearLayout) fullLayout.findViewById(R.id.options_bar_top);
        optionsBarTop.setVisibility(View.GONE);
        optionsBarBottom = (LinearLayout) fullLayout.findViewById(R.id.options_bar_bottom);
        optionsBarBottom.setVisibility(View.GONE);
        getLayoutInflater().inflate(layoutResID, subActivityContent, true);                         // Places the activity layout inside the activity content frame.
        super.setContentView(fullLayout);                                                           // Sets the content view as the merged layouts.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        menu.removeItem(R.id.menu_plus_for_new_event);

        if (!SessionManager.isHawkUser()) {

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();

            nav_Menu.findItem(R.id.nav_my_hosted_events).setVisible(false);

        }

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        loadPreferences();

        // Check if user is already logged in or not
        if (!SessionManager.isLoggedIn()) {
            // User is not already logged in. Take him to Main Page
            Intent intent = new Intent(Profile.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_home_screen);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, my_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation Drawer Listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Bottom Options Bar Button - Events is clicked
        Button bottom_events = (Button) findViewById(R.id.bottom_events);
        bottom_events.setOnClickListener(this);

        // Bottom Options Bar Button - Map is clicked
        Button bottom_map = (Button) findViewById(R.id.bottom_map);
        bottom_map.setOnClickListener(this);

        Button bottom_profile = (Button) findViewById(R.id.bottom_profile);

        bottom_profile.setTextColor(Color.parseColor("#FFFFFF"));


        TextView title = (TextView) findViewById(R.id.title_profile);
        TextView userEmail = (TextView) findViewById(R.id.user_email);
        TextView userFullName = (TextView) findViewById(R.id.user_full_name);

        // Button logout
        Button btnLogout = (Button) findViewById(R.id.btnLogout);

        // displaying user data
        title.setText("Welcome to your profile " + firstname);
        userFullName.setText(firstname + "  " + lastname);
        userEmail.setText(email);

        util= new UploadDownloadUtil(this);

        /*
         * Logout button click event
         */
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data &
                // redirect user to MainActivity
                if (email.contains("@iit.edu") ||
                        email.contains("@hawk.iit.edu")) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                }
                            });
                }

                // Dergister the token!
                //

                // MJ --> Changed from Profile.this to NULL to avoid rare force close
                Map<String,String> param = new HashMap<>();
                param.put("registrationToken","");
                param.put("userEmail", email);
                util.uploadDownloadGenericData(param,null,null,Profile.this, Config.getRegistrationTokenURL(),null);
//                SessionManager.setRunningFirstTime(false);
//                SessionManager.logoutUser();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onRestart() {
        loadPreferences();
        super.onRestart();
    }

    @Override
    public void onTaskComplete(boolean success, List<Map<String, String>> response, boolean error, String passThroughParam) {
        if(success==true) {
            SessionManager.setRunningFirstTime(false);
            SessionManager.logoutUser();
        }
    }
}
