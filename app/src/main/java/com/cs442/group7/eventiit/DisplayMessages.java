package com.cs442.group7.eventiit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cs442.group7.eventiit.helper.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayMessages extends BaseActivity implements UploadDownloadUtil.GenericListener{

    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;
    protected LinearLayout optionsBarTop;
    protected LinearLayout optionsBarBottom;

    public static ArrayList<MessageObj> adminMessages = new ArrayList<MessageObj>();

    MessagesListAdapter messagesListAdapter;

    private static final String TAG_SUCCESS = "success";

    private static String url_fetch_admin_messages = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/fetch_admin_messages.php";

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
        menu.removeItem(R.id.menu_messages);

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
        setContentView(R.layout.activity_display_messages);


        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        loadPreferences();

        // Check if user is already logged in or not
        if (!SessionManager.isLoggedIn()) {
            // User is not already logged in. Take him to Main Page
            Intent intent = new Intent(DisplayMessages.this, MainActivity.class);
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

        messagesListAdapter = new MessagesListAdapter(this, adminMessages);
        ListView messageListView = (ListView) findViewById(R.id.listview_messages);
        messageListView.setAdapter(messagesListAdapter);

        //Load from online database
        readFromDB();

    }

    private void readFromDB() {
        adminMessages.clear();
        UploadDownloadUtil util= new UploadDownloadUtil(this);
        Map<String, String> param = new HashMap<>();
        param.put("userEmail", SessionManager.getUserEmail());
        String[] returnKeys = {"id","content", "header", "isAlert"};


        util.uploadDownloadGenericData(param,returnKeys,"events",this,url_fetch_admin_messages,null);
    }

    @Override
    public void onTaskComplete(boolean success, List<Map<String, String>> response, boolean error, String passThroughParam) {

        if(success) {

            for(Map<String, String> eventParam : response) {
                String message = eventParam.get("content");
                String header = eventParam.get("header");
                int id = Integer.parseInt(eventParam.get("id"));
                boolean isAlert = eventParam.get("isAlert").equals("1")?true:false;

                MessageObj messageObj = new MessageObj(id,header,message);
                messageObj.setAlert(isAlert);
                adminMessages.add(messageObj);
//                Toast.makeText(this, "Message alert: "+isAlert, Toast.LENGTH_SHORT).show();
            }
            messagesListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onRestart() {
//        adapter.notifyDataSetChanged();
        loadPreferences();
        super.onRestart();
    }
}
