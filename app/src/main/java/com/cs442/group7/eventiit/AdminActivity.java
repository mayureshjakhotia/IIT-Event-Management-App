package com.cs442.group7.eventiit;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cs442.group7.eventiit.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends Activity {

    private static final String TAG_SUCCESS = "success";

    private static String url_store_admin_message = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/store_admin_message.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Check if user is already logged in or not
        if (!SessionManager.isLoggedIn()) {
            // User is not already logged in. Take him to Main Page
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        final CheckBox isAlert_Checkbox = (CheckBox) findViewById(R.id.isAlert);

        final EditText input_header = (EditText) findViewById(R.id.input_header);
        // Button input message
        final EditText input_message = (EditText) findViewById(R.id.input_message);

        // Button submit message
        Button submit_message = (Button) findViewById(R.id.submit_message);
        submit_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (input_message.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(AdminActivity.this, "Please enter a valid message!", Toast.LENGTH_SHORT).show();
                }
                else if(input_header.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(AdminActivity.this, "Please enter a valid header!", Toast.LENGTH_SHORT).show();
                }
                else {
                    new AdminMessages().execute(input_message.getText().toString(), input_header.getText().toString(), isAlert_Checkbox.isChecked()==true?"1":"0");
                    Toast.makeText(AdminActivity.this, "Message submitted successfully", Toast.LENGTH_SHORT).show();
                    input_message.setText("");
                }
            }
        });

        // Button delete_message
        Button delete_message = (Button) findViewById(R.id.delete_message);
        delete_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteMessage = new Intent(AdminActivity.this, DeleteMessage.class);
                startActivity(deleteMessage);
            }
        });


        // Button logout
        Button btnLogout = (Button) findViewById(R.id.btnLogout);

        /*
         * Logout button click event
         */
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SessionManager.setRunningFirstTime(false);
                SessionManager.logoutUser();
            }
        });


    }

    class AdminMessages extends AsyncTask<String, String, String> {
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

        protected String doInBackground(String... args) {

            String message = args[0];
            String header = args[1];
            String isAlert = args[2];


            JSONParser jsonParser = new JSONParser();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("message", message));
            params.add(new BasicNameValuePair("header", header));
            params.add(new BasicNameValuePair("isAlert", isAlert));

            Log.d("Message", message);

            // getting JSON Object
            // Note that store message url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_store_admin_message, "POST", params);

            // check for success tag

            Log.e("AdminActivity", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.d("Success", "Message Inserted");
                    Intent deleteMessage = new Intent(AdminActivity.this, DeleteMessage.class);
                    startActivity(deleteMessage);

                } else {
                    // failed to update category
                    Log.d("Failure", "Message not inserted !!!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
