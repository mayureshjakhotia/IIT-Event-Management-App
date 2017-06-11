package com.cs442.group7.eventiit;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cs442.group7.eventiit.helper.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Shyam on 10/28/16.
 */

public class MyHostingAdapter extends RecyclerView.Adapter<MyHostingAdapter.ViewHolder> implements UploadDownloadUtil.ViewRsvpListener {

    private static final String TAG = MyHostingAdapter.class.getSimpleName();
    private ImageLoader imageLoader;
    private static ArrayList<EventItemModel> eim;
    private static Context mContext;
    private int resource;
    private DeleteEvent deleteEvent;
    private static final String deleteEventURL = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/delete_event.php";
    private static final String viewRsvpURL = "http://ec2-54-147-231-178.compute-1.amazonaws.com/PhpProject1/get_all_rsvp.php";

    public MyHostingAdapter(ArrayList<EventItemModel> eim, Context context, ImageLoader imageLoader, int resource) {
        this.eim = eim;
        this.mContext = context;
        this.imageLoader = imageLoader;
        this.resource = resource;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        // set the view's size, margins, paddings and layout parameters
        // .......
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final EventItemModel eim = this.eim.get(position);

        holder.eventIcon.setDefaultImageResId(R.drawable.ic_tool_bar_search);
        if (eim.getEventIconURL() != null && !eim.getEventIconURL().equalsIgnoreCase("null")) {
            holder.eventIcon.setImageUrl(eim.getEventIconURL(), imageLoader);
        }
        holder.eventNameText.setText(eim.getEventName());

        if (eim.startDateEqualsEndDate() == true) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            holder.eventTime.setText(sdf.format(eim.getEventStartDateTime().getTime()) + " "
                    + eim.getEventStartTime() + " - " + eim.getEventEndTime());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            holder.eventTime.setText(sdf.format(eim.getEventStartDateTime().getTime()) + " "
                    + eim.getEventStartTime() + " - " + sdf.format(eim.getEventEndDateTime().
                    getTime()) + " " + eim.getEventEndTime());
        }

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopupMenu(holder.overflow, eim.getEventID(),eim.getEventEndDateTime(), position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */

    private void showPopupMenu(View view, String event_ID, Calendar eventEndDateTime, int position) {

        // inflate menu
        Context wrapper = new ContextThemeWrapper(mContext, R.style.MyPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);
//        PopupMenu popup = new PopupMenu(mContext, view);


        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.my_hosting_overload_menu, popup.getMenu());
        if (eventEndDateTime.before(Calendar.getInstance( TimeZone.getTimeZone("America/Chicago"),
                Locale.getDefault()))){
//            ((MyHostingActivity)mContext).findViewById(R.id.my_hosting_overflow_action_edit).setVisibility(View.GONE);
            Menu m = popup.getMenu();
            m.removeItem(R.id.my_hosting_overflow_action_edit);
//            Toast.makeText(mContext,"remove Item",Toast.LENGTH_SHORT);
//            Log.e(TAG,"past event");
        }

//        Log.d(TAG,position+"");
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(event_ID, position));
        popup.show();
    }

    @Override
    public void onTaskComplete(List<Map<String, String>> userRsvpList, String eventName) {

        if (userRsvpList == null || userRsvpList.size() == 0) {
            Toast.makeText(mContext, "No users have rsvped", Toast.LENGTH_SHORT).show();

        } else {

            // start the fragment when the list has been fetched;
            MyHostingAdapter.ViewRsvpDialogueFragment viewRsvpDialogueFragment =
                    MyHostingAdapter.ViewRsvpDialogueFragment.newInstance(userRsvpList, eventName);

            FragmentManager fm = ((MyHostingActivity) mContext).getFragmentManager();
            viewRsvpDialogueFragment.show(fm, "fragment_show_rsvp_guest");

        }

    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        String event_ID;
        int position;

        public MyMenuItemClickListener(String event_ID, int position) {
            this.event_ID = event_ID;
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.my_hosting_overflow_action_edit:

                    Intent intent = new Intent(mContext, EditEvent.class);
                    intent.putExtra("event_ID", event_ID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);

                    return true;

                case R.id.my_hosting_overflow_action_view_all_rsvp:
                    UploadDownloadUtil uploadDownloadUtil = new UploadDownloadUtil(MyHostingAdapter.this);
                    uploadDownloadUtil.fetchRsvpGuests(event_ID, MyHostingAdapter.mContext, viewRsvpURL, eim.get(position).getEventName());
                    return true;

                case R.id.my_hosting_overflow_delete_event:
                    handleDeleteDialogue(event_ID, SessionManager.getUserEmail(), position);

                    return true;

                default:
            }
            return false;
        }
    }

    private void handleDeleteDialogue(final String eventID, final String userEmail, final int position)
    {


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    mContext );

            // set title
            alertDialogBuilder.setTitle("Confirmation");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete event!")
                    .setCancelable(false)
                    .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            deleteEvent = new DeleteEvent(position);
                            deleteEvent.startJob(userEmail,eventID);
                            // if this button is clicked, close
                            // current activity

                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return eim.size();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView eventNameText;
        TextView eventTime;
        NetworkImageView eventIcon;
        ImageView overflow;

        public ViewHolder(View view) {
            super(view);
            eventIcon = (NetworkImageView) view.findViewById(R.id.my_hosting_thumbnail);
            eventNameText = (TextView) view.findViewById(R.id.my_hosting_event_name);
            eventTime = (TextView) view.findViewById(R.id.my_hosting_event_date);
            overflow = (ImageView) view.findViewById(R.id.my_hosting_overflow);
        }
    }


    public static class ViewRsvpDialogueFragment extends DialogFragment {

        static List<Map<String, String>> userRsvpList;
        static String eventName;

        static MyHostingAdapter.ViewRsvpDialogueFragment newInstance(List<Map<String, String>> userRsvpList, String eventName) {
            MyHostingAdapter.ViewRsvpDialogueFragment f = new MyHostingAdapter.ViewRsvpDialogueFragment();

            ViewRsvpDialogueFragment.userRsvpList = userRsvpList;
            ViewRsvpDialogueFragment.eventName = eventName;

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            int mNum = 6;

            // Pick a style based on the num.
            int style = DialogFragment.STYLE_NORMAL, theme = 0;
            switch ((mNum - 1) % 6) {
                case 1:
                    style = DialogFragment.STYLE_NO_TITLE;
                    break;
                case 2:
                    style = DialogFragment.STYLE_NO_FRAME;
                    break;
                case 3:
                    style = DialogFragment.STYLE_NO_INPUT;
                    break;
                case 4:
                    style = DialogFragment.STYLE_NORMAL;
                    break;
                case 5:
                    style = DialogFragment.STYLE_NORMAL;
                    break;
                case 6:
                    style = DialogFragment.STYLE_NO_TITLE;
                    break;
                case 7:
                    style = DialogFragment.STYLE_NO_FRAME;
                    break;
                case 8:
                    style = DialogFragment.STYLE_NORMAL;
                    break;
            }
            switch ((mNum - 1) % 6) {
                case 4:
                    theme = android.R.style.Theme_Holo;
                    break;
                case 5:
                    theme = android.R.style.Theme_Holo_Light_Dialog;
                    break;
                case 6:
                    theme = android.R.style.Theme_Holo_Light;
                    break;
                case 7:
                    theme = android.R.style.Theme_Holo_Light_Panel;
                    break;
                case 8:
                    theme = android.R.style.Theme_Holo_Light;
                    break;
            }
            setStyle(style, theme);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            getDialog().setTitle("Guests Attending \"" + eventName + "\"");
            View view = inflater.inflate(R.layout.fragment_view_rsvp_guests, container);

            final TextView total = (TextView) view.findViewById(R.id.view_rsvp_guest_total);
            total.setText("Total : " + userRsvpList.size());

            ListView listView = (ListView) view.findViewById(R.id.view_rsvp_guest_list);


            SimpleAdapter adapter = new SimpleAdapter(MyHostingAdapter.mContext, userRsvpList,
                    android.R.layout.simple_list_item_2,
                    new String[]{"email", "name"},
                    new int[]{android.R.id.text1,
                            android.R.id.text2});
            listView.setAdapter(adapter);

            ImageButton imageButton = (ImageButton) view.findViewById(R.id.button_view_rsvp_send_email);
            imageButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // When button is clicked, call up to owning activity.
//                    ((EditEvent) getActivity()).onDescriptionAdded(edit_event_description.getText().toString());
                    String[] userEmails = new String[userRsvpList.size()];
                    int j = 0;
                    for (Map<String,String> recepients : userRsvpList)
                    {
                        userEmails[j] = recepients.get("email");
                        j++;
                    }

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , userEmails);
                    i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                    i.putExtra(Intent.EXTRA_TEXT   , "body of email");


                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(MyHostingAdapter.mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            });

            // Watch for button clicks.
            Button button_back = (Button) view.findViewById(R.id.button_rsvp_guest_back);
            button_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // When button is clicked, call up to owning activity.
//                    ((EditEvent) getActivity()).onDescriptionAdded(edit_event_description.getText().toString());
                    dismiss();
                }
            });


            return view;
        }

    }

    public class DeleteEvent implements UploadDownloadUtil.GenericListener
    {
int position;
        public DeleteEvent(int position){
this.position=position;
        }

        public void startJob(String userEmail, String eventID){
            UploadDownloadUtil uploadDownloadUtil = new UploadDownloadUtil(this);
            Map<String, String> param = new HashMap<>();
            param.put("userEmail", userEmail);
            param.put("eventID", eventID);
            String[] returnKeys = {"isDeleted"};
//            Log.e("ViewEvent", "userEmail "+userEmail + " evenId "+eventID);
            uploadDownloadUtil.uploadDownloadGenericData(param, returnKeys, "isDeleted", null, deleteEventURL, null);


        }

        @Override
        public void onTaskComplete(boolean success, List<Map<String, String>> response, boolean error, String passThroughParam) {
//            Toast.makeText(mContext, "Deleleted", Toast.LENGTH_LONG);
//            Log.e(TAG, "deleted");

            eim.remove(position);
            ((MyHostingActivity)mContext).mRecyclerView.removeViewAt(position);;
            MyHostingAdapter.this.notifyItemRemoved(position);
            MyHostingAdapter.this.notifyItemRangeChanged(position, eim.size());
        }
    }

}


