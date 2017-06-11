package com.cs442.group7.eventiit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {

    private final int SPLASH_SCREEN_TIME = 1000;
    private static boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        if (!isFirstTime) {
            /* New Handler to start the MainActivity
             * & close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, SPLASH_SCREEN_TIME);
        }
        else {
            /* New Handler to start the MainActivity
             * & close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    isFirstTime = false;
                    Intent mainIntent = new Intent(SplashScreen.this, WelcomeActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, SPLASH_SCREEN_TIME);
        }

    }
}