package com.cs442.group7.eventiit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayureshjakhotia on 12/9/16.
 */

public class DeleteMessagesAdapter extends ArrayAdapter {


    private static final String TAG_SUCCESS = "success";

    private static String url_delete_admin_message = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/delete_admin_message.php";

    private ProgressDialog pDialog;

    ArrayList<MessageObj> adminMessages;
    Context context;

    public DeleteMessagesAdapter(Activity context, ArrayList<MessageObj> adminMessages) {
        super(context, R.layout.delete_messages_custom_list, adminMessages);
        this.adminMessages = adminMessages;
        this.context = context;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRow = layoutInflater.inflate(R.layout.delete_messages_custom_list, null, true);
        final TextView message = (TextView) viewRow.findViewById(R.id.message);
        final TextView header = (TextView) viewRow.findViewById(R.id.header);
        TextView s_no = (TextView) viewRow.findViewById(R.id.s_no);
        final Button delete = (Button) viewRow.findViewById(R.id.delete);
        final int id = adminMessages.get(i).getID();
        message.setText(adminMessages.get(i).getContent());
        header.setText(adminMessages.get(i).getHeader());
        s_no.setText(String.valueOf(i+1));

        if(adminMessages.get(i).isAlert()) {

            LinearLayout linearLayout = (LinearLayout) viewRow.findViewById(R.id.layout_messages_custom_list);
            linearLayout.setBackgroundResource(R.color.colorAlert);
            header.setTextColor(context.getResources().getColor(R.color.cancelButton));
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                delete.setVisibility(View.INVISIBLE);
                delete.setText("Deleted");
                delete.setBackgroundColor(Color.parseColor("#FFFFFF"));
                delete.setTextColor(Color.parseColor("#C62828"));
                new DeleteAdminMessage().execute(header.getText().toString(), message.getText().toString(), String.valueOf(id), String.valueOf(i));
 //               ((DeleteMessage)context).recreate();

            }
        });

        return viewRow;
    }

    class DeleteAdminMessage extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */

        int arrayID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Updating Categories..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
 }

        protected String doInBackground(String... args) {

            String header = args[0];
            String message = args[1];
            String id =  args[2];
            arrayID = Integer.parseInt(args[3]);

            JSONParser jsonParser = new JSONParser();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("message", message));
            params.add(new BasicNameValuePair("header", header));
            params.add(new BasicNameValuePair("id", id));

            Log.d("Message", message);

            // getting JSON Object
            // Note that store message url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_delete_admin_message, "POST", params);

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.d("Success", "Message Deleted");

                } else {
                    // failed to update category
                    Log.d("Failure", "Message not deleted !!!");
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
            pDialog.dismiss();
            DeleteMessagesAdapter.this.remove(DeleteMessagesAdapter.this.getItem(arrayID));
            DeleteMessagesAdapter.this.notifyDataSetChanged();
        }

    }

}
