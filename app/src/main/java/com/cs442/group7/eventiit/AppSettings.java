package com.cs442.group7.eventiit;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppSettings extends BaseActivity {

    protected DrawerLayout fullLayout;
    protected RelativeLayout subActivityContent;
    protected LinearLayout optionsBarTop;
    protected LinearLayout optionsBarBottom;

    private ProgressDialog pDialog;
    private String userEmail;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG = AppSettings.class.getSimpleName();
    private static String url_update_user_category = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/store_user_categories.php";


    private boolean flagForChangedCategory = false;
    private ArrayList<String> category_name = new ArrayList<String>();
    private ArrayList<String> category_status = new ArrayList<String>();

    private HashMap<String, String> category = new HashMap<>();

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefsListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
/*                    if (key.equals("background_color")) {
                        loadPreferences();

                    }*/

                    if (key.equals("background")) {
/*
                        Toast.makeText(getApplicationContext(), "Selected " +String.valueOf(sharedPreferences.getString(key, "1")), Toast.LENGTH_SHORT).show();
*/
                        loadPreferences();
                    }

                    if (key.equals("app_bar")
                            || key.equals("top_bar")
                            || key.equals("bottom_bar")) {

                        loadPreferences();
                    }

                    if (key.equals("category_all")) {
                        category.put("All",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_computer_science")) {
                        category.put("Computer Science",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_festival")) {
                        category.put("Festival",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_deals")) {
                        category.put("Deals",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_sports")) {
                        category.put("Sports",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_music")) {
                        category.put("Music",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_food")) {
                        category.put("Food",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_arts")) {
                        category.put("Arts",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_science")) {
                        category.put("Science",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_architecture")) {
                        category.put("Architecture",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_career")) {
                        category.put("Career",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_international_students")) {
                        category.put("International Students",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    } else if (key.equals("category_others")) {
                        category.put("Others",String.valueOf(sharedPreferences.getBoolean(key, false)));
                        flagForChangedCategory = true;

                    }

                }
            };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (flagForChangedCategory) {
            flagForChangedCategory = false;
/*
            for (String key:category.keySet()) {
                Toast.makeText(getApplicationContext(), key + " is " + category.get(key), Toast.LENGTH_SHORT).show();

            }
*/
            new UserCategoryPreferences().execute(category);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);  // The base layout
        subActivityContent = (RelativeLayout) fullLayout.findViewById(R.id.content_frame);            // The frame layout where the activity content is placed.
        optionsBarTop = (LinearLayout) fullLayout.findViewById(R.id.options_bar_top);
        optionsBarTop.setVisibility(View.GONE);
        optionsBarBottom = (LinearLayout) fullLayout.findViewById(R.id.options_bar_bottom);
        optionsBarBottom.setVisibility(View.GONE);
        getLayoutInflater().inflate(layoutResID, subActivityContent, true);            // Places the activity layout inside the activity content frame.
        super.setContentView(fullLayout);                                            // Sets the content view as the merged layouts.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_settings);

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        getSupportActionBar().setTitle("Settings");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SettingsFragment settingsFragment = new SettingsFragment();
        fragmentTransaction.add(R.id.content_frame, settingsFragment, "SETTINGS_FRAGMENT");
        fragmentTransaction.commit();

        loadPreferences();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_home_screen);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, my_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation Drawer Listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onRestart() {
        loadPreferences();
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mPrefsListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(mPrefsListener);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.app_preferences);

        }
    }


    class UserCategoryPreferences extends AsyncTask<HashMap, HashMap, String> {
    /**
     * Before starting background thread Show Progress Dialog
     */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
 /*           pDialog = new ProgressDialog(AppSettings.this);
            pDialog.setMessage("Updating Categories..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
 */       }

        protected String doInBackground(HashMap... args) {

            HashMap<String, String> category = args[0];

            JSONParser jsonParser = new JSONParser();



            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));

            for (String key:category.keySet()) {
                params.add(new BasicNameValuePair("eventCategory", key));
                params.add(new BasicNameValuePair("eventCategoryStatus", category.get(key)));
                Log.d("Category", key);
                Log.d("Category Checked", category.get(key));

                // getting JSON Object
                // Note that update category url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_update_user_category, "POST", params);

                // check log cat for response
                //Log.d("Update Category", json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.d("Success", "Category Updated");
                        finish();
                    } else {
                        // failed to update category
                        Log.d("Failure", "Category not updated !!!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            return null;
        }

/**
 * After completing background task Dismiss the progress dialog
 **/

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
//            pDialog.dismiss();
        }

    }
}
