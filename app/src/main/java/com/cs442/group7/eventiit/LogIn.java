package com.cs442.group7.eventiit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cs442.group7.eventiit.app.AppControllerSingleton;
import com.cs442.group7.eventiit.helper.SQLiteHandler;
import com.cs442.group7.eventiit.helper.SessionManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, UploadDownloadUtil.GenericListener {

    // Server user login url
    private static String URL_LOGIN = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/login.php";

    private static final String TAG = "LogInActivity";
    private Button button_sign_in;
    private Button button_cancel;
    private Button button_google_sign_in;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    //    private SessionManager session;
    private SQLiteHandler db;


    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    private String registrationToken;

    private String email;

    private static int retryTimes = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);

        button_sign_in = (Button) findViewById(R.id.button_sign_in);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_google_sign_in = (Button) findViewById(R.id.button_google_sign_in);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
//        session = SessionManager.getInstance(getApplicationContext());

        SessionManager.set_context(getApplicationContext());


        // Check if user is already logged in or not
        if (SessionManager.isLoggedIn()) {
            // User is already logged in. Take him to Home Screen
            Intent intent = new Intent(LogIn.this, HomeScreen.class);
            startActivity(intent);
            finish();
        }

        registrationToken = SessionManager.getKeyRegToken();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Google Sign In is Clicked
        button_google_sign_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e(TAG,"Google signin clicked");
                Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(googleSignInIntent, RC_SIGN_IN);
            }
        });


        // Regular Sign In is Clicked (Server Database for guest users)
        button_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();

                    // Check if Admin is Logging In
                    if (email.equalsIgnoreCase("admin@eventsiit.com")
                            && password.equalsIgnoreCase("admin")) {

                        // Create login session
                        SessionManager.createLoginSession(email, "admin", "admin");

                        Intent adminIntent = new Intent(LogIn.this, AdminActivity.class);
                        startActivity(adminIntent);
                        finish();
                    }
                    else {
                        checkLogin(email, password);
                    }
                }
            }
        });

        // Sign Up is Clicked
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityIntent = new Intent(LogIn.this, MainActivity.class);
                startActivity(mainActivityIntent);


                finish();

                /* Apply our log in exit (slide out right) and main screen
                entry (slide in left) animation transitions. */
                overridePendingTransition(R.anim.left_in, R.anim.right_out);

            }
        });
    }


    private boolean validate() {

        // Set EditText for Input Text
        final EditText input_email = (EditText) findViewById(R.id.input_email);
        EditText input_password = (EditText) findViewById(R.id.input_password);

        // If both fields are filled
        if (!input_email.getText().toString().equalsIgnoreCase("") &&
                !input_password.getText().toString().equalsIgnoreCase("")) {

            if (isValidEmail(input_email.getText().toString())) {

                if (!input_email.getText().toString().contains("@iit.edu") &&
                        !input_email.getText().toString().contains("@hawk.iit.edu")) {

                    return true;

                }
                else {
                    input_email.setError("Hawk User Login is not allowed here.");
                }
            }
            else {
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
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // User successfully logged in

                        // Fetch User Details
                        JSONObject user = jObj.getJSONObject("user");
                        String firstname = user.getString("firstname");
                        String lastname = user.getString("lastname");
                        String email = user.getString("email");
                        sendRegistrationToServer();
                        LogIn.this.email = email;

                        // Create login session
                        SessionManager.createLoginSession(email, firstname, lastname);

                        // Now store the user in SQLite
                        // Inserting row in users table
                        db.addUser(firstname, lastname, email);

                        // Launch main activity
                        Intent intent = new Intent(LogIn.this, HomeScreen.class);
                        startActivity(intent);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct.getEmail().contains("@iit.edu") ||
                    acct.getEmail().contains("@hawk.iit.edu")) {

                Toast.makeText(this, "Sign In Success!! Yippie (:", Toast.LENGTH_LONG).show();

                SignUp.registerHawkUser(getApplicationContext(),acct.getGivenName(), acct.getFamilyName(), acct.getEmail());


                // User successfully logged in

                // Fetch User Details
                String firstname = acct.getGivenName();
                String lastname = acct.getFamilyName();
                String email = acct.getEmail();
                LogIn.this.email = email;

                sendRegistrationToServer();

                // Create login session
                SessionManager.createLoginSession(email, firstname, lastname);

                // Now store the user in SQLite
                // Inserting row in users table
                db.addUser(firstname, lastname, email);

                // Launch main activity
                Intent intent = new Intent(LogIn.this, HomeScreen.class);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Sign In Failed!! Please use IIT Email address.", Toast.LENGTH_LONG).show();
                // But user was logged in using a non-IIT Gmail account. So need to log out.
                signOutGmail();
            }

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Sign In Failed!! Please check credentials again...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    // To automatically sign out user if logging in using a non-IIT Gmail account
    public void signOutGmail() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    @Override
    public void onTaskComplete(boolean success, List<Map<String, String>> response, boolean error, String token) {
        retryTimes--;
        if(!error&&retryTimes!=0) {

            if (!success) {
                //Keep on trying...

                Log.e(TAG, "Failure Resistering Token");
                retryAfter(1000);
                // Loop Back
                this.sendRegistrationToServer();
            } else {
                Log.e(TAG, "Success Resistering Token");
                finish();
            }
        }else {
            Log.e(TAG, "Error Registering Token");
        }

    }

    private void retryAfter(int millisecond){

        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void sendRegistrationToServer() {

        if(!SessionManager.isRunningFirstTime()&&!SessionManager.getKeyRegToken().equals("")) {
            UploadDownloadUtil util = new UploadDownloadUtil(this);

            Log.e(TAG, "d");
            Map<String, String> param = new HashMap<>();
            param.put("registrationToken", registrationToken);
            param.put("userEmail", SessionManager.getUserEmail());

            util.uploadDownloadGenericData(param, null,null, null, com.cs442.group7.eventiit.app.Config.getRegistrationTokenURL(), null);

            Log.e(TAG, "sentRegistrationToServer: " + registrationToken);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent mainActivityIntent = new Intent(LogIn.this, MainActivity.class);
        startActivity(mainActivityIntent);

        finish();

        /* Apply our log in exit (slide out right) and main screen
        entry (slide in left) animation transitions. */
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }
}
