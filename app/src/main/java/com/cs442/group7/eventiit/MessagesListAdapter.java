package com.cs442.group7.eventiit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by mayureshjakhotia on 12/9/16.
 */

public class MessagesListAdapter extends ArrayAdapter implements View.OnClickListener{

    ArrayList<MessageObj> adminMessages;
    Context context;

    public MessagesListAdapter(Activity context, ArrayList<MessageObj> adminMessages) {
        super(context, R.layout.messages_custom_list, adminMessages);
        this.adminMessages = adminMessages;
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRow = layoutInflater.inflate(R.layout.messages_custom_list, null, true);
        TextView header = (TextView) viewRow.findViewById(R.id.header);
        TextView message = (TextView) viewRow.findViewById(R.id.message);
        TextView s_no = (TextView) viewRow.findViewById(R.id.s_no);
        viewRow.setOnClickListener(this);
        if(adminMessages.get(i).isAlert()){

            LinearLayout linearLayout = (LinearLayout) viewRow.findViewById(R.id.layout_messages_custom_list);
            linearLayout.setBackgroundResource(R.color.colorAlert);
//            header.setBackgroundResource(R.color.cancelButton);
//            message.setBackgroundResource(R.color.cancelButton);
//            s_no.setBackgroundResource(R.color.cancelButton);
            header.setTextColor(context.getResources().getColor(R.color.cancelButton));
//            message.setTextColor(context.getResources().getColor(R.color.white));
//            s_no.setTextColor(context.getResources().getColor(R.color.white));

        }
        viewRow.setTag(i);
        message.setText(adminMessages.get(i).getContent());
        header.setText(adminMessages.get(i).getHeader());
        s_no.setText(String.valueOf(i+1));
        return viewRow;
    }


    @Override
    public void onClick(View v) {
//        Log.e("MessageListAdapter", v.getTag().toString()+ "  clicked ");
        int val = Integer.parseInt(v.getTag().toString());

        ShowDialogueCreateEvent  showDialogue= new ShowDialogueCreateEvent();


//        ShowDialogueCreateEvent.create(context ,((TextView) v.findViewById(R.id.message)).getText().toString(),((TextView) v.findViewById(R.id.header)).getText().toString());
        Bundle bundle = new Bundle();
        //

        bundle.putString("title", ((TextView) v.findViewById(R.id.header)).getText().toString());
        bundle.putString("message", ((TextView) v.findViewById(R.id.message)).getText().toString());
//
        showDialogue.setArguments(bundle);
        showDialogue.show(((DisplayMessages)context).getFragmentManager(), "ValiationFragment");

        Toast.makeText(context,val+ " value ",Toast.LENGTH_SHORT);
    }
}
