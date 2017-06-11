package com.cs442.group7.eventiit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.cs442.group7.eventiit.helper.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteMessage extends Activity implements UploadDownloadUtil.GenericListener{

    public static ArrayList<MessageObj> adminMessages = new ArrayList<MessageObj>();

    DeleteMessagesAdapter deleteMessagesAdapter;

    private static final String TAG_SUCCESS = "success";

    private static String url_fetch_admin_messages = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/fetch_admin_messages.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_message);

        // Check if user is already logged in or not
        if (!SessionManager.isLoggedIn()) {
            // User is not already logged in. Take him to Main Page
            Intent intent = new Intent(DeleteMessage.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        deleteMessagesAdapter = new DeleteMessagesAdapter(this, adminMessages);
        ListView messageListView = (ListView) findViewById(R.id.listview_delete_messages);
        messageListView.setAdapter(deleteMessagesAdapter);

        //Load from online database
        readFromDB();

    }

    public void readFromDB() {
        adminMessages.clear();
        UploadDownloadUtil util= new UploadDownloadUtil(this);
        Map<String, String> param = new HashMap<>();
        param.put("userEmail", SessionManager.getUserEmail());
        String[] returnKeys = {"id","content", "header", "isAlert"};


        util.uploadDownloadGenericData(param,returnKeys,"events",this,url_fetch_admin_messages,null);
    }

    @Override
    public void onTaskComplete(boolean success, List<Map<String, String>> response, boolean error, String passThroughParam) {

        if (success) {

            for (Map<String, String> eventParam : response) {

                int id = Integer.parseInt(eventParam.get("id"));
                String message = eventParam.get("content");
                String header = eventParam.get("header");

                boolean isAlert = eventParam.get("isAlert").equals("1")?true:false;

                MessageObj messageObj = new MessageObj(id,header,message);
                messageObj.setAlert(isAlert);
                adminMessages.add(messageObj);
//                Toast.makeText(this, "Message : "+adminMessages, Toast.LENGTH_SHORT).show();
            }

            deleteMessagesAdapter.notifyDataSetChanged();
        }
    }

}
