package com.cs442.group7.eventiit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cs442.group7.eventiit.app.AppControllerSingleton;
import com.cs442.group7.eventiit.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends Activity {

    private String firstname, lastname, email, password;

    // Server user register url
    public static String URL_REGISTER = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/register.php";

    private static final String TAG = "SignUp";
    private Button button_sign_up;
    private EditText input_first_name;
    private EditText input_last_name;
    private EditText input_email;
    private EditText input_password;
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set EditText
        input_first_name = (EditText) findViewById(R.id.input_first_name);
        input_last_name = (EditText) findViewById(R.id.input_last_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);


        // Set Buttons
        button_sign_up = (Button) findViewById(R.id.button_sign_up);
        Button button_cancel = (Button) findViewById(R.id.button_cancel);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Sign Up is Clicked
        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {

                    // Convert EditText to String
                    firstname = input_first_name.getText().toString();
                    lastname = input_last_name.getText().toString();
                    email = input_email.getText().toString();
                    password = input_password.getText().toString();

                    registerUser(firstname, lastname, email, password);
                }

            }
        });

        // Cancel is Clicked
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logInIntent = new Intent(SignUp.this, MainActivity.class);
                startActivity(logInIntent);

                finish();

                /* Apply our sign up exit (slide in right) and main screen
                entry (slide out left) animation transitions. */
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

    }

    private boolean validate() {

        // Set EditText for Input Text
        final EditText input_email = (EditText) findViewById(R.id.input_email);
        EditText input_password = (EditText) findViewById(R.id.input_password);
        EditText input_first_name = (EditText) findViewById(R.id.input_first_name);
        EditText input_last_name = (EditText) findViewById(R.id.input_last_name);

        if (!input_email.getText().toString().equalsIgnoreCase("") &&
                !input_password.getText().toString().equalsIgnoreCase("") &&
                !input_first_name.getText().toString().equalsIgnoreCase("") &&
                !input_last_name.getText().toString().equalsIgnoreCase("")) {

            if (isValidEmail(input_email.getText().toString())) {
                // If all 4 fields are filled

                if (!input_email.getText().toString().contains("@iit.edu") &&
                        !input_email.getText().toString().contains("@hawk.iit.edu")) {

                    return true;

                }
                else {
                    input_email.setError("Hawk Email ID cannot be used.\nIIT Students can login directly.");
                }

            } else {
                input_email.setError("Not a Valid Email ID");
            }
        }
        else {
            Toast.makeText(this, "Error : Please fill all input fields!", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    /*
     * Check Email Format
     */
    public static boolean isValidEmail(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    public void registerUser(final String firstname, final String lastname, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        JSONObject user = jObj.getJSONObject("user");
                        String firstname = user.getString("firstname");
                        String lastname = user.getString("lastname");
                        String email = user.getString("email");

                        // Inserting row in users table
                        db.addUser(firstname, lastname, email);

                        Toast.makeText(getApplicationContext(), "User successfully registered. You can login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                SignUp.this,
                                LogIn.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppControllerSingleton.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Function to store Hawk User in MySQL database
     */
    public static void registerHawkUser(final Context context, final String firstname, final String lastname, final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
               // hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        JSONObject user = jObj.getJSONObject("user");
                        String firstname = user.getString("firstname");
                        String lastname = user.getString("lastname");
                        String email = user.getString("email");

                        SQLiteHandler db;

                        // SQLite database handler
                        db = new SQLiteHandler(context);

                        // Inserting row in users table
                        db.addUser(firstname, lastname, email);

                        //Toast.makeText(context, "Hawk User Registered with the system", Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        AppControllerSingleton.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent mainActivityIntent = new Intent(SignUp.this, MainActivity.class);
        startActivity(mainActivityIntent);

        finish();

        /* Apply our sign up exit (slide in right) and main screen
        entry (slide out left) animation transitions. */
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

    }



}
