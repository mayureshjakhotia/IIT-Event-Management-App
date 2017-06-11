package com.cs442.group7.eventiit.service;

/**
 * Created by Shyam on 11/14/16.
 */

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cs442.group7.eventiit.app.Config;
import com.cs442.group7.eventiit.helper.SessionManager;

import com.cs442.group7.eventiit.UploadDownloadUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements UploadDownloadUtil.GenericListener {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    private static int retryTimes = 10;
    private static int retryTimesWaitForSession = 10;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.e(TAG,"TOKEN REFRESH");
        //get token from firebase..
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "refreshedToken: " + refreshedToken);
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.

        // Sending broadcast that
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {

        // Looping Can be reason of crash!!
        UploadDownloadUtil util = new UploadDownloadUtil(this);
//        Toast.makeText(getApplicationContext(),"Service Connecting to server",Toast.LENGTH_SHORT).show();
        Log.e(TAG,"Connecting to server");
        Map<String,String> param = new HashMap<>();
        param.put("registrationToken",token);
//         Wait till the user name is not available
        while (SessionManager.getUserEmail()==null){
//            Log.e(TAG,"Iterating ");


            // TAKE CARE of This Stuffff.!!!!
            this.retryAfter(4000);
        }

        param.put("userEmail", SessionManager.getUserEmail());

        util.uploadDownloadGenericData(param,null,null,null, com.cs442.group7.eventiit.app.Config.getRegistrationTokenURL(),token);

    }

    private void storeRegIdInPref(String token) {
//        SharedPreferences pref = getApplicationContext().getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(SessionManager.KEY_REG_TOKEN, token);
//        editor.commit();

        //changed!!!!
        SessionManager.set_context(getApplicationContext());
        SessionManager.setRunningFirstTime(false);
        SessionManager.setKeyRegToken(token);
    }

    @Override
    public void onTaskComplete(boolean success, List<Map<String, String>> response,boolean error,String token) {
        if(!error) {
            if (!success&&retryTimes>=0) {
                //Keep on trying...
                retryTimes--;
                Log.e(TAG, "Failure Resistering Token");
                retryAfter(1000);
                // Loop Back
                this.sendRegistrationToServer(token);
            } else {
                Log.e(TAG, "Success Resistering Token");
            }
        }else {
            Log.e(TAG, "Error Registering Token");
        }
    }
    public void retryAfter(int millisecond){

        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
