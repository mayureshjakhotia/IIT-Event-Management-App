package com.cs442.group7.eventiit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Created by Shyam on 10/21/16.
 */

public class ShowDialogueCreateEvent extends DialogFragment {



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final TextView message = new TextView(getActivity());

        // i.e.: R.string.dialog_message =>
        // "Test this dialog following the link to dtmilano.blogspot.com"
        final SpannableString s =
                new SpannableString(getArguments().getString("message"));
        Linkify.addLinks(s, Linkify.ALL);
        message.setText(s);
//        message.setTextColor(getResources().getColor(R.color.bg_screen4));
        message.setTextSize(16);
        message.setPadding(15,10,10,30);
        message.setMovementMethod(LinkMovementMethod.getInstance());


//            setStyle(5,5);
//        if (context == null) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyAlertDialogStyle);
            builder.setTitle(getArguments().getString("title"))
//                    setMessage(getArguments().getString("message"))
                    .setView(message)

                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    });
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
