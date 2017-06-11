package com.cs442.group7.eventiit.helper;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.cs442.group7.eventiit.BaseActivity;
import com.cs442.group7.eventiit.MainActivity;

import java.util.HashMap;

/**
 * Created by mayureshjakhotia on 11/1/16.
 */

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    static SharedPreferences pref;
    static SharedPreferences prefServiceToken;

    static Editor editor;
    static Editor editorServiceToken;

    public static void set_context(Context _context) {

        SessionManager._context = _context;
        if(pref==null&&editor==null) {
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

        if(prefServiceToken==null&&editor==null) {
            prefServiceToken = _context.getSharedPreferences(PREF_NAME_TOKEN_SERVICE, PRIVATE_MODE);
            editorServiceToken = prefServiceToken.edit();
        }
    }

    static Context _context;




    // Shared pref mode
    public static int PRIVATE_MODE = 0;

    // Shared preferences file name
    public static final String PREF_NAME = "Event-IIT_UserLoggedIn";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String KEY_FIRST_NAME = "firstname";
    public static final String KEY_LAST_NAME = "lastname";
    public static final String KEY_EMAIL = "email";

    public static final String KEY_RUNNING_FIRST_TIME = "isRunningFirstTime";

    public static final String PREF_NAME_TOKEN_SERVICE = "Event-IIT_UserRegistrtionToken";



    public static final String KEY_REG_TOKEN = "regToken";




    private SessionManager(Context context) {

        this._context = context;
        if(pref==null&&editor==null) {
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

        if(prefServiceToken==null&&editor==null) {
            prefServiceToken = _context.getSharedPreferences(PREF_NAME_TOKEN_SERVICE, PRIVATE_MODE);
            editorServiceToken = prefServiceToken.edit();
        }
    }


    public static String getUserEmail() {

        return pref.getString(KEY_EMAIL, null);
    }

    //Token Getter Setters....
    public static String getKeyRegToken() {
        initialize();
        return prefServiceToken.getString(KEY_REG_TOKEN, "");
    }

    public static void setKeyRegToken(String token){

        editorServiceToken.putString(KEY_REG_TOKEN,token);

        editorServiceToken.commit();
    }

    public static boolean isLoggedIn(){

        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public static void createLoginSession(String email, String firstname, String lastname) {

//        //Shyam
//        editor.putString(KEY_REG_TOKEN,regToken);

        // Storing login value as TRUE
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        // Storing details in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FIRST_NAME, firstname);
        editor.putString(KEY_LAST_NAME, lastname);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    /*
     * Get stored session data
     */
    public static HashMap<String, String> getUserDetails(){
        initialize();
        HashMap<String, String> user = new HashMap<String, String>();
        // user Email
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // First Name
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null));

        // Last Name
        user.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, null));

        // return user
        return user;
    }

    /*
     * Clear session details
     */
    public static void logoutUser(){



        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Main Activity
        Intent i = new Intent(_context, MainActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*
         * Add new Flag to start new Activity & Clear Task to clear any existing task
         * associated with the activity (any old activities are finished)
         */
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Starting Main Activity
        _context.startActivity(i);
    }


    // CHANGE THE METHOD NAME. NOT MEANINGFUL RIGHT NOW!!!
    public static boolean isRunningFirstTime() {
        initialize();
        return prefServiceToken.getBoolean(KEY_RUNNING_FIRST_TIME,true);
    }

    public static void setRunningFirstTime(boolean runningFirstTime) {

        initialize();
        editorServiceToken.putBoolean(KEY_RUNNING_FIRST_TIME,runningFirstTime);
        editorServiceToken.commit();
    }

    private static void initialize(){

        if(pref==null||editor==null) {
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

        if(prefServiceToken==null||editor==null) {
            prefServiceToken = _context.getSharedPreferences(PREF_NAME_TOKEN_SERVICE, PRIVATE_MODE);
            editorServiceToken = prefServiceToken.edit();
        }
    }

    public static Boolean isHawkUser() {
        if (BaseActivity.email.contains("@iit.edu") ||
                BaseActivity.email.contains("@hawk.iit.edu")) {
            return true;
        }
        else {
            return false;
        }
    }

}