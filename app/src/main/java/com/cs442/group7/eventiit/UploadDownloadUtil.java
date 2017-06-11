package com.cs442.group7.eventiit;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.cs442.group7.eventiit.app.AppControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Shyam on 11/13/16.
 */

public class UploadDownloadUtil {


//    private static final int SEND_REGISTRATION_ID = 0;
//    private static final int SEND_DEGISTRATION_REQUEST = 1;
//    private static final int DELETE_EVENT_REQUEST = 2;

    private static final String TAG = UploadDownloadUtil.class.getSimpleName();
    private UploadDownloadUtil instance;

    private UploadDownloadUtil() {
    }

    public interface GenericListener {
        // For update Rsvp...
        void onTaskComplete(boolean success, List<Map<String, String>> response, boolean error, String passThroughParam);
    }

    public interface Listener {
        // For update Rsvp...
        void onTaskComplete(boolean success);
    }

    public interface ViewRsvpListener {
        // For update Rsvp...
        void onTaskComplete(List<Map<String, String>> userRsvpList, String eventName);
    }


    private Listener listener;
    private ViewRsvpListener viewRsvpListner;
    private GenericListener genericListener;


    public UploadDownloadUtil(UploadDownloadUtil.Listener listener) {

        this.listener = listener;
    }

    public UploadDownloadUtil(UploadDownloadUtil.ViewRsvpListener viewRsvpListener) {

        this.viewRsvpListner = viewRsvpListener;
    }

    public UploadDownloadUtil(UploadDownloadUtil.GenericListener genericListener) {

        this.genericListener = genericListener;
    }

    public synchronized void uploadRsvpState(final boolean isRsvp, final String userEmail, final String eventID, final Context showProgressDialueContext, final String url) {

        final ProgressDialog progressDialog;
        //Showing the progress dialog
        if (showProgressDialueContext != null)
            progressDialog = ProgressDialog.show(showProgressDialueContext, "", "Please wait...", false, false);
        else progressDialog = null;

        // Using volley String request to send data this time.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        //Showing toast message of the response
                        if (showProgressDialueContext != null)
                            Toast.makeText(showProgressDialueContext, s.trim(), Toast.LENGTH_LONG).show();              // MJ changed to s.trim from s
                        if (s.contains("SUCCESS"))
                            listener.onTaskComplete(true);
                        else
                            listener.onTaskComplete(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                        listener.onTaskComplete(false);
//                        hidePDialog();
//                        Showing toast
//                        Toast.makeText(EditEvent.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                java.util.Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters

                params.put("isRsvp", String.valueOf(isRsvp));

                params.put("userEmail", userEmail);

                params.put("eventID", eventID);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = AppControllerSingleton.getInstance().getRequestQueue();

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public synchronized void fetchRsvpGuests(final String eventID, final Context showProgressDialueContext, String url, final String eventName) {

        // ArrayList of maps to be accepted directly by simple list adapters...
        final List<Map<String, String>> userRsvpList = new ArrayList<>();

        final ProgressDialog progressDialog;
        //Showing the progress dialog
        if (showProgressDialueContext != null)
            progressDialog = ProgressDialog.show(showProgressDialueContext, "", "Please wait...", false, false);
        else
            progressDialog = null;

        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"eventID\":\"" + eventID + "\"}");
//            Log.d(TAG,"JSON Object = "+jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest eventReq = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (progressDialog != null)
                            progressDialog.dismiss();


                        try {
                            int success = response.getInt("success");
                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("rsvplist");
                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject obj = ja.getJSONObject(i);
                                    Map<String, String> listItem = new HashMap<>();
                                    listItem.put("email", obj.getString("userEmail"));
                                    listItem.put("name", obj.getString("userFirstName") + obj.getString("userLastName"));

//                                    Log.d(TAG, obj.getString("eventName"));

                                    userRsvpList.add(listItem);
                                }//for loop ends
                            }//if ends
                            else if (success == 0) {
                                Log.d(TAG, "Error fetching data");
                                viewRsvpListner.onTaskComplete(null, eventName);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }//try catch ends

                        // Notify Listener
                        viewRsvpListner.onTaskComplete(userRsvpList, eventName);


                    }
                }
                , new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (progressDialog != null)
                    progressDialog.dismiss();
                viewRsvpListner.onTaskComplete(null, eventName);
            }
        });

        eventReq.setTag(TAG);
        // Adding request to request queue
        AppControllerSingleton.getInstance().addToRequestQueue(eventReq);
    }

    public synchronized void uploadDownloadGenericData(Map<String, String> nameValue, final String[] returnKeys, final String responseObjName, final Context showProgressDialueContext, final String url, final String passThroughParam) {
        // ArrayList of maps to be accepted directly by simple list adapters...
        final List<Map<String, String>> responseList = new ArrayList<>();

        final ProgressDialog progressDialog;
        //Showing the progress dialog
        if (showProgressDialueContext != null)
            progressDialog = ProgressDialog.show(showProgressDialueContext, "", "Please wait...", false, false);
        else
            progressDialog = null;

        JSONObject jsonBody = null;
        if (nameValue != null) {
            try {
                jsonBody = new JSONObject();
                for (Map.Entry<String, String> entry : nameValue.entrySet()) {
                    jsonBody.put(entry.getKey(), entry.getValue());
                }
                Log.i(TAG, "JSON Object = " + jsonBody.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest eventReq = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (progressDialog != null)
                            progressDialog.dismiss();

                        try {
                            int success = response.getInt("success");
                            if (success == 1) {
                                if (returnKeys != null) {//expecting keyvalue response..

                                    JSONArray ja = response.getJSONArray(responseObjName);// can be changed to opt

                                    for (int i = 0; i < ja.length(); i++) {

                                        JSONObject obj = ja.getJSONObject(i);
                                        Map<String, String> listItem = new HashMap<>();

                                        for (int j = 0; j < returnKeys.length; j++) {

                                            listItem.put(returnKeys[j], obj.optString(returnKeys[j]));
                                        }
//                                    Log.d(TAG, obj.getString("eventName"));

                                        responseList.add(listItem);
                                    }//for loop ends
                                    genericListener.onTaskComplete(true, responseList, false, passThroughParam);
                                }//inner id ends

                                else {
                                    // Not expecting any response key values
                                    genericListener.onTaskComplete(true, null, false, passThroughParam);
                                }
                            }//if ends
                            else if (success == 0) {
                                Log.e(TAG, "Error fetching data");
                                genericListener.onTaskComplete(false, null, false, passThroughParam);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "JSONException Error fetching data");
                            genericListener.onTaskComplete(false, null, false, passThroughParam);

                        }//try catch ends

                        // Notify Listener

                    }
                }
                , new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (progressDialog != null)
                    progressDialog.dismiss();
                genericListener.onTaskComplete(false, null, true, passThroughParam);
            }
        });

        eventReq.setTag(TAG);
        // Adding request to request queue
        AppControllerSingleton.getInstance().addToRequestQueue(eventReq);
    }

}
