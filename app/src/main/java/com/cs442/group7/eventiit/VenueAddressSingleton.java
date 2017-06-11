package com.cs442.group7.eventiit;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.cs442.group7.eventiit.app.AppControllerSingleton;

/**
 * Created by Shyam on 11/10/16.
 */

public class VenueAddressSingleton {

    public static interface Listener {
        void onFetchVenueDetails(VenueArrayList<VenueAddressObject> venueAddressObjects);
    }

    private String url_venue_address = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/venue_details.php";
    public static final String TAG = VenueAddressSingleton.class.getSimpleName();

    private boolean flagDataFetchComplete = false;

    private static VenueArrayList<VenueAddressObject> venueAddressObjects;
    //    private static HashMap<Integer, VenueAddressObject> venueIDLocalID;
    private static VenueAddressSingleton instance;

    static VenueAddressSingleton.Listener listener;

    private VenueAddressSingleton() {
        venueAddressObjects = new VenueArrayList<VenueAddressObject>();
        //For mapping to local
//        venueIDLocalID = new HashMap<Integer,VenueAddressObject >();

    }

    public void startJob() {
        if (flagDataFetchComplete) {
            if (listener != null)
                listener.onFetchVenueDetails(venueAddressObjects);
        } else {
            venueAddressObjects.clear();
            fetchData();
       }

    }

    public static synchronized VenueAddressSingleton getInstance(VenueAddressSingleton.Listener listener) {

        VenueAddressSingleton.listener = listener;
//        Log.d(TAG,"Reached ");

        if (instance == null)
            instance = new VenueAddressSingleton();

        return instance;
    }
    private void fetchData() {
        //data request to server

        JsonObjectRequest dataReq = new JsonObjectRequest(Request.Method.POST, url_venue_address, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                // Parsing json
                try {

                    int success = response.getInt("success");
                    if (success == 1) {

                        JSONArray ja = response.getJSONArray("venue_details");

                        for (int i = 0; i < ja.length(); i++) {

                            JSONObject obj = ja.getJSONObject(i);
//                            Log.d(TAG, "object" + obj.toString());
                            VenueAddressObject venueAddressObject = new VenueAddressObject
                                    (obj.getInt("ID"), obj.getString("venue_name"),
                                            obj.getString("venue_address"),obj.getString("venue_latitude"),obj.getString("venue_longitude"));

                            // adding eventitem to event array
//                            venueAddressObjects.put(obj.getString("venue_name"), venueAddressObject);
                            venueAddressObjects.add(venueAddressObject);

                        }//for loop ends
                    }//if ends

                    flagDataFetchComplete = true;
                    if (listener != null)
                        listener.onFetchVenueDetails(venueAddressObjects);

                } catch (JSONException e) {
                    e.printStackTrace();
                }//try catch ends

            }//onResonse Ends
        }, new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Log.d(TAG, "ERROR FETCHINGr");

            }
        });
        dataReq.setTag(TAG);
        // Adding request to request queue
        AppControllerSingleton.getInstance().addToRequestQueue(dataReq);

    }

    public static class VenueAddressObject {
        int ID;
        String venueName;
        String venueAddress;
        String venueLatitude;
        String venueLongitude;

        public VenueAddressObject(int ID, String venueName, String venueAddress, String venueLatitude, String venueLongitude) {
            this.ID = ID;
            this.venueName = venueName;
            this.venueAddress = venueAddress;
            this.venueLatitude = venueLatitude;
            this.venueLongitude = venueLongitude;
        }
    }

    public static class VenueArrayList<E> extends ArrayList<VenueAddressObject> {

        public int getIndexByVenueName(String venueName) {
            for (int i = 0; i < this.size(); i++) {
                if (this.get(i).venueName == venueName) {
                    return this.get(i).ID;
                }
            }
            return -1;// not there is list
        }

//        public String getNameByIndex(int index) {
//            for (int i = 0; i < this.size(); i++) {
//                if (this.get(i).ID == index) {
//                    return this.get(i).venueName;
//                }
//            }
//            return null;
//        }

        public VenueAddressObject getObjectByID(int ID) {
            for (int i = 0; i < this.size(); i++) {

                if (this.get(i).ID == ID) {
                    return this.get(i);
                }
            }
            return null;
        }
    }

}

