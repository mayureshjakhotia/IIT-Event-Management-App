package com.cs442.group7.eventiit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.cs442.group7.eventiit.helper.SessionManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, Serializable {

    public static String firstname;
    public static String lastname;
    public static String email;

    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        DrawerLayout layout = (DrawerLayout) findViewById(R.id.activity_home_screen);

        layout.setBackgroundResource(R.drawable.background1);
        // Session class instance
//        SessionManager session = new SessionManager(getApplicationContext());
        SessionManager.set_context(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = SessionManager.getUserDetails();

        // first name
        firstname = user.get(SessionManager.KEY_FIRST_NAME);

        // last name
        lastname = user.get(SessionManager.KEY_LAST_NAME);

        // email
        email = user.get(SessionManager.KEY_EMAIL);

    }

    public void loadPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

/*
        boolean isBackgroundDark = sharedPreferences.getBoolean("background_color", false);
*/

        String backgroundID = sharedPreferences.getString("background", "1");

        String app_bar = sharedPreferences.getString("app_bar", "1");

        String top_bar = sharedPreferences.getString("top_bar", "1");

        String bottom_bar = sharedPreferences.getString("bottom_bar", "1");

        DrawerLayout homescreenLayout = (DrawerLayout) findViewById(R.id.activity_home_screen);


        switch (backgroundID) {

            case "1":
                homescreenLayout.setBackgroundResource(R.drawable.background1);
                break;

            case "2":
                homescreenLayout.setBackgroundResource(R.drawable.background2);
                break;

            case "3":
                homescreenLayout.setBackgroundResource(R.drawable.background3);
                break;

            case "4":
                homescreenLayout.setBackgroundResource(R.drawable.background_image3);
                break;

            case "5":
                homescreenLayout.setBackgroundResource(R.drawable.background_image2);
                break;

            case "6":
                homescreenLayout.setBackgroundResource(0);
                homescreenLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;

        }

        switch (app_bar) {

            case "1":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03A9F4")));
                break;

            case "2":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8BC34A")));
                break;

            case "3":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F44336")));
                break;

            case "4":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9C27B0")));
                break;

            case "5":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFC107")));
                break;

            case "6":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DE9B6")));
                break;

        }

        Button top_featured_events = (Button) findViewById(R.id.top_featured_events);
        Button top_all_events = (Button) findViewById(R.id.top_all_events);
        Button top_my_hosting_event = (Button) findViewById(R.id.top_my_hosting);
        Button top_registered_events = (Button) findViewById(R.id.top_registered_events);
        Button top_past_events = (Button) findViewById(R.id.top_past_events);
        Button bottom_events = (Button) findViewById(R.id.bottom_events);
        Button bottom_map = (Button) findViewById(R.id.bottom_map);
        Button bottom_profile = (Button) findViewById(R.id.bottom_profile);

        switch (top_bar) {

            case "1":
                top_featured_events.setBackgroundColor(Color.parseColor("#03A9F4"));
                top_all_events.setBackgroundColor(Color.parseColor("#03A9F4"));
                top_registered_events.setBackgroundColor(Color.parseColor("#03A9F4"));
                top_past_events.setBackgroundColor(Color.parseColor("#03A9F4"));
                top_my_hosting_event.setBackgroundColor(Color.parseColor("#03A9F4"));
                break;

            case "2":
                top_featured_events.setBackgroundColor(Color.parseColor("#8BC34A"));
                top_all_events.setBackgroundColor(Color.parseColor("#8BC34A"));
                top_registered_events.setBackgroundColor(Color.parseColor("#8BC34A"));
                top_past_events.setBackgroundColor(Color.parseColor("#8BC34A"));
                top_my_hosting_event.setBackgroundColor(Color.parseColor("#8BC34A"));
                break;

            case "3":
                top_featured_events.setBackgroundColor(Color.parseColor("#F44336"));
                top_all_events.setBackgroundColor(Color.parseColor("#F44336"));
                top_registered_events.setBackgroundColor(Color.parseColor("#F44336"));
                top_past_events.setBackgroundColor(Color.parseColor("#F44336"));
                top_my_hosting_event.setBackgroundColor(Color.parseColor("#F44336"));
                break;

            case "4":
                top_featured_events.setBackgroundColor(Color.parseColor("#9C27B0"));
                top_all_events.setBackgroundColor(Color.parseColor("#9C27B0"));
                top_registered_events.setBackgroundColor(Color.parseColor("#9C27B0"));
                top_past_events.setBackgroundColor(Color.parseColor("#9C27B0"));
                top_my_hosting_event.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;

            case "5":
                top_featured_events.setBackgroundColor(Color.parseColor("#FFC107"));
                top_all_events.setBackgroundColor(Color.parseColor("#FFC107"));
                top_registered_events.setBackgroundColor(Color.parseColor("#FFC107"));
                top_past_events.setBackgroundColor(Color.parseColor("#FFC107"));
                top_my_hosting_event.setBackgroundColor(Color.parseColor("#FFC107"));
                break;

            case "6":
                top_featured_events.setBackgroundColor(Color.parseColor("#1DE9B6"));
                top_all_events.setBackgroundColor(Color.parseColor("#1DE9B6"));
                top_registered_events.setBackgroundColor(Color.parseColor("#1DE9B6"));
                top_past_events.setBackgroundColor(Color.parseColor("#1DE9B6"));
                top_my_hosting_event.setBackgroundColor(Color.parseColor("#1DE9B6"));
                break;

        }

        switch (bottom_bar) {

            case "1":
                bottom_events.setBackgroundColor(Color.parseColor("#03A9F4"));
                bottom_map.setBackgroundColor(Color.parseColor("#03A9F4"));
                bottom_profile.setBackgroundColor(Color.parseColor("#03A9F4"));
                break;

            case "2":
                bottom_events.setBackgroundColor(Color.parseColor("#8BC34A"));
                bottom_map.setBackgroundColor(Color.parseColor("#8BC34A"));
                bottom_profile.setBackgroundColor(Color.parseColor("#8BC34A"));
                break;

            case "3":
                bottom_events.setBackgroundColor(Color.parseColor("#F44336"));
                bottom_map.setBackgroundColor(Color.parseColor("#F44336"));
                bottom_profile.setBackgroundColor(Color.parseColor("#F44336"));
                break;

            case "4":
                bottom_events.setBackgroundColor(Color.parseColor("#9C27B0"));
                bottom_map.setBackgroundColor(Color.parseColor("#9C27B0"));
                bottom_profile.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;

            case "5":
                bottom_events.setBackgroundColor(Color.parseColor("#FFC107"));
                bottom_map.setBackgroundColor(Color.parseColor("#FFC107"));
                bottom_profile.setBackgroundColor(Color.parseColor("#FFC107"));
                break;

            case "6":
                bottom_events.setBackgroundColor(Color.parseColor("#1DE9B6"));
                bottom_map.setBackgroundColor(Color.parseColor("#1DE9B6"));
                bottom_profile.setBackgroundColor(Color.parseColor("#1DE9B6"));
                break;

        }

        /*if (isBackgroundDark) {
            homescreenLayout.setBackgroundColor(Color.parseColor("#BDBDBD"));
        }
        else {
            homescreenLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
*/
        String customName = sharedPreferences.getString("title", firstname);

        if (customName==null||customName.trim().isEmpty()) {
            customName = firstname;
        }

        setTitle("Events-IIT" + " ( " + customName + " )");

    }

    @Override
    public void onClick(View v) {

        final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        /*scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });*/
        switch (v.getId()) {

            case R.id.top_featured_events:
                Intent featuredEventsIntent = new Intent(this, HomeScreen.class);
                featuredEventsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(featuredEventsIntent);
                break;

            case R.id.top_all_events:
                Intent allEvents = new Intent(this, AllEvents.class);
                allEvents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(allEvents);
                break;

            case R.id.top_registered_events:
                Intent registeredEventsIntent = new Intent(this, RegisteredEvents.class);
                registeredEventsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(registeredEventsIntent);
                break;

            // Added by Shyam
            case R.id.top_my_hosting:
                Intent myHostingIntent = new Intent(this, MyHostingActivity.class);
                myHostingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(myHostingIntent);
                break;

            case R.id.top_past_events:
                Intent pastEventsIntent = new Intent(this, PastEvents.class);
                pastEventsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(pastEventsIntent);
                break;

            case R.id.bottom_events:
                featuredEventsIntent = new Intent(BaseActivity.this, HomeScreen.class);
                featuredEventsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(featuredEventsIntent);
                break;

            case R.id.bottom_map:
                Intent mapIntent = new Intent(BaseActivity.this, MapsActivity.class);
                startActivity(mapIntent);
                break;

            case R.id.bottom_profile:
                Intent profileIntent = new Intent(BaseActivity.this, Profile.class);
                startActivity(profileIntent);
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_messages:
                Intent displayMessages = new Intent(this, DisplayMessages.class);
                startActivity(displayMessages);
                break;

            case R.id.menu_plus_for_new_event:
                Intent createEvent = new Intent(this, CreateEvent.class);
                startActivity(createEvent);
                break;

            case R.id.action_settings:
                Intent settings = new Intent(this, AppSettings.class);
                startActivity(settings);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_featured_events) {
            Intent featuredEventsIntent = new Intent(this, HomeScreen.class);
            featuredEventsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(featuredEventsIntent);
        }
        else if (id == R.id.nav_all_events) {
            Intent allEvents = new Intent(this, AllEvents.class);
            allEvents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(allEvents);
        }
        else if (id == R.id.nav_registered_events) {
            Intent registeredEventsIntent = new Intent(this, RegisteredEvents.class);
            registeredEventsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(registeredEventsIntent);
        }
        else if (id == R.id.nav_past_events) {
            Intent pastEventsIntent = new Intent(this, PastEvents.class);
            pastEventsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(pastEventsIntent);
        }
        else if (id == R.id.nav_my_hosted_events) {
            Intent myHostedEventsIntent = new Intent(this, MyHostingActivity.class);
            myHostedEventsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myHostedEventsIntent);
        }
        else if (id == R.id.nav_map) {

            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, AppSettings.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_share) {
            shareIt();

        }
        else if (id == R.id.nav_report_issue) {
            reportIssueViaEmail();
        }

        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_home_screen);
        mDrawerLayout.closeDrawers();

        return true;
    }
    public static String getEmail() {
        return email;
    }

    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey Check out this Amazing Events app!!!");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Now Participate in any event held at Illinois Tech. Meet new friends, learn new skills and have a great time in Chicago. Click here to become our beta tester : https://drive.google.com/a/iit.edu/file/d/0B1BjiIsXnGkMbVBhX2tuRGhrWU0/view?usp=sharing ");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void reportIssueViaEmail() {
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"issues@eventsiit.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Issue dated " + c.getTime());
        i.putExtra(Intent.EXTRA_TEXT   , "Please provide a brief description of bug here & we'll fix it asap.");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BaseActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
