package com.cs442.group7.eventiit;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

// Serializable implemented in EventItemModel also in order to pass event item model object to ViewEvent class
public class ListViewAdapter extends ArrayAdapter<EventItemModel> implements View.OnClickListener, Serializable {

    private int resource;
    private static LayoutInflater inflater = null;
    private ImageLoader imageLoader;
    private List<EventItemModel> eventItems;
    private Context context;
    private static final String TAG = ListViewAdapter.class.getSimpleName();


    public ListViewAdapter(Context context,
                           int resource,
                           List<EventItemModel> items, ImageLoader imageLoader) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        eventItems = (List) items;
        this.imageLoader = imageLoader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (inflater == null) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            //Do things for the first time
            convertView = inflater.inflate(resource, null);
            //set up the viewHolder
            holder = new ViewHolder(convertView);

            // Commented by MJ -->
            // Log.d("ListView Adapter", "initialized");

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        //Get the model object(List) that contains the data to be inserted into listview.
        EventItemModel eim = eventItems.get(position);

//        if (imageLoader == null) {
//            //!!Trying to get results from cache
//            imageLoader = AppControllerSingleton.getInstance().getImageLoader();
////            imageLoader = new ImageLoader(AppControllerSingleton.getInstance().getRequestQueue(),
////                    (new LruBitmapCache(LruBitmapCache.getDefaultLruCacheSize())));
//        }

        if (eim != null && holder != null) {

            holder.eventIcon.setDefaultImageResId(R.drawable.ic_tool_bar_search);
            if (eim.getEventIconURL() != null && !eim.getEventIconURL().equalsIgnoreCase("null")) {
//                Log.d("ListView", "image loader is cached" + imageLoader.isCached(eim.getEventIconURL(), 50, 50));

                // Commented by MJ-->
                // Log.d("ListAdapter", eim.getEventIconURL());

                holder.eventIcon.setImageUrl(eim.getEventIconURL(), imageLoader);
            }

            holder.eventNameText.setText(eim.getEventName());

            // Commented by MJ-->
            // Log.e(TAG,eim.getEventName()+" position "+position);

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
        }
        return convertView;
    }

     static class ViewHolder {
        TextView eventNameText;
        TextView eventTime;
        NetworkImageView eventIcon;
        //        ProgressBar progress;
        int position;

        ViewHolder(View view) {
            eventIcon = (NetworkImageView) view.findViewById(R.id.listitem_event_image);
            eventNameText = (TextView) view.findViewById(R.id.listitem_event_name);
            eventTime = (TextView) view.findViewById(R.id.listitem_event_timestamp);
        }
    }

    @Override
    public void onClick(final View v) {

        ListView list = (ListView) v.findViewById(R.id.listview_this_week);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                EventItemModel eim = eventItems.get(position);

                Intent intent = new Intent(getContext(), ViewEvent.class);

                intent.putExtra("EventDetails", eim);
                intent.putExtra("position",position);

                if (v.getContext().toString().contains("PastEvents")) {

                    intent.putExtra("isPastEvent", "true");


//                  Toast.makeText(getContext(), "Past Events Recognized", Toast.LENGTH_LONG).show();

                }

                getContext().startActivity(intent);

            }
        });
    }

}