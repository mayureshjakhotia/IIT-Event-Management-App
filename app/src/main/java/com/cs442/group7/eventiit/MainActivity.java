package com.cs442.group7.eventiit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cs442.group7.eventiit.helper.SessionManager;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String token= FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG,"Token: " + token);


        //Toast.makeText(MainActivity.this,token,Toast.LENGTH_SHORT).show();

       // Set Buttons
        Button button_sign_up = (Button) findViewById(R.id.button_sign_up);
        Button button_log_in = (Button) findViewById(R.id.button_log_in);

        // Session manager to see if user is already logged in & redirect appropriately
//        SessionManager session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        SessionManager.set_context(getApplicationContext());
        if (SessionManager.isLoggedIn()) {
            // Regular User is already logged in. Take him to Home Screen
            if (!SessionManager.getUserEmail().equalsIgnoreCase("admin@eventsiit.com")) {
                Intent intent = new Intent(this, HomeScreen.class);
                startActivity(intent);
                finish();
            }
            // Admin check
            else {
                Intent intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
                finish();
            }
        }

        // Sign Up is Clicked
        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent signUpIntent = new Intent(MainActivity.this, SignUp.class);

                startActivity(signUpIntent);

                finish();


                /* Apply our main screen exit (slide in left) and sign up
                entry (slide out right) animation transitions. */
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            }
        });

        // Log In is Clicked
        button_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logInIntent = new Intent(MainActivity.this, LogIn.class);                 // Change later to IIT Login

                startActivity(logInIntent);

                finish();

             /* Apply our main screen exit (slide in right) and log in
                entry (slide out left) animation transitions. */
                overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });
    }

}
