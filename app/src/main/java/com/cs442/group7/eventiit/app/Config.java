package com.cs442.group7.eventiit.app;

/**
 * Created by Shyam on 11/15/16.
 */

public class Config {


    // global topic to receive com.cs442.group7.eventiit.app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final String SERVER_URL = "http://ec2-54-147-231-178.compute-1.amazonaws.com";
    public static final String PROJECT_NAME = "/PhpProject1";

    public static String getRegistrationTokenURL(){
        return new String(SERVER_URL+PROJECT_NAME+"/register_token.php");
    }


}


