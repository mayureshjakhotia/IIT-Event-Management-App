package com.cs442.group7.eventiit;


import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Shyam on 10/24/16.
 */

// Serializable required here since it is also implemented in ListViewAdapter (in order to pass event item model object to ViewEvent class)
public class EventItemModel implements Serializable {

    private int eventVenueID;

    private String eventID;
    private String eventName;
    private String eventIconURL;
    private Calendar eventStartDateTime;
    private Calendar eventEndDateTime;

    public Calendar getEventRSVPDateTime() {
        return eventRSVPDateTime;
    }

    private Calendar eventRSVPDateTime;

    private String eventStartTime;
    private String eventEndTime;
    private String eventRSVPTime;
    private String eventStartDate;
    private String eventEndDate;
    private String eventRSVPDate;

    private String eventStartDateFormatted;
    private String eventEndDateFormatted;
    private String eventRSVPDateFormatted;

    private String eventVenueOtherDetails;
    private String eventDescription;

    private int flagForRSVP = 0;
    private boolean isUserRsvped = false;

    public EventItemModel() {

    }

    public EventItemModel(String eventID, String eventName, String eventIconURL, String eventStartDateTime, String eventEndDateTime, String eventRSVPDateTime, String eventVenueID, String eventVenueOtherDetails, String eventDescription) {

        this.eventName = eventName;
        this.eventIconURL = eventIconURL;
        this.eventID = eventID;
        if (eventVenueID != null || !eventVenueID.equals(""))
            this.eventVenueID = Integer.parseInt(eventVenueID);


        this.eventVenueOtherDetails = eventVenueOtherDetails;
        this.eventDescription = eventDescription;

        Calendar startCal = Calendar.getInstance();
        eventStartDate = eventStartDateTime.substring(0, 10);
        eventEndDate = eventEndDateTime.substring(0, 10);

        if (eventRSVPDateTime == null || eventRSVPDateTime.trim().equalsIgnoreCase("") || eventRSVPDateTime.trim().equalsIgnoreCase("null")) {
            Log.d("Empty", "MJ");
            flagForRSVP = 1;
        }

        if (flagForRSVP == 0) {
            eventRSVPDate = eventRSVPDateTime.substring(0, 10);
        }

        eventStartTime = eventStartDateTime.substring(11, 16);
        eventEndTime = eventEndDateTime.substring(11, 16);

        if (flagForRSVP == 0) {
            eventRSVPTime = eventRSVPDateTime.substring(11, 16);
        }

        startCal.set(Integer.parseInt(eventStartDate.split("-")[0]),
                Integer.parseInt(eventStartDate.split("-")[1]) - 1,       // adjustment due to month number
                Integer.parseInt(eventStartDate.split("-")[2]),
                Integer.parseInt(eventStartTime.split(":")[0]),
                Integer.parseInt(eventStartTime.split(":")[1]));
        this.eventStartDateTime = startCal;

        Calendar endCal = Calendar.getInstance();
        endCal.set(Integer.parseInt(eventEndDate.split("-")[0]),
                Integer.parseInt(eventEndDate.split("-")[1]) - 1,         // adjustment due to month number
                Integer.parseInt(eventEndDate.split("-")[2]),
                Integer.parseInt(eventEndTime.split(":")[0]),
                Integer.parseInt(eventEndTime.split(":")[1]));
        this.eventEndDateTime = endCal;

        if (flagForRSVP == 0) {

            Calendar rsvpCal = Calendar.getInstance();
            rsvpCal.set(Integer.parseInt(eventRSVPDate.split("-")[0]),
                    Integer.parseInt(eventRSVPDate.split("-")[1]) - 1,         // adjustment due to month number
                    Integer.parseInt(eventRSVPDate.split("-")[2]),
                    Integer.parseInt(eventRSVPTime.split(":")[0]),
                    Integer.parseInt(eventRSVPTime.split(":")[1]));
            this.eventRSVPDateTime = rsvpCal;
        }

//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//        System.out.println( "The date is: "+  sdf.format( date )  );
    }

    public int getEventVenueID() {
        return eventVenueID;
    }

    public String getEventVenueOtherDetails() {
        if (TextUtils.isEmpty(eventVenueOtherDetails)) return "";
        else return eventVenueOtherDetails;
    }

    public String getEventDescription() {
        if (TextUtils.isEmpty(eventDescription)) return "";
        else return eventDescription;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventIconURL() {
        return eventIconURL;
    }

    public Calendar getEventStartDateTime() {
        return eventStartDateTime;
    }

    public Calendar getEventEndDateTime() {
        return eventEndDateTime;
    }

    public String getEventStartTime() {
        return eventStartTime.split(":")[0] + " : " + eventStartTime.split(":")[1];
    }

    public String getEventEndTime() {
        return eventEndTime.split(":")[0] + " : " + eventEndTime.split(":")[1];
    }

    public String getRSVPTime() {
        if (flagForRSVP == 0) {
            return eventRSVPTime.split(":")[0] + " : " + eventRSVPTime.split(":")[1];

        } else {
            eventRSVPTime = "false";
        }
        return eventRSVPTime;
    }

    public String getStartDateFormatted() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyy");
        eventStartDateFormatted = format.format(Date.valueOf(eventStartDate));

        return eventStartDateFormatted;
    }


    public String getEndDateFormatted() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyy");
        eventEndDateFormatted = format.format(Date.valueOf(eventEndDate));

        return eventEndDateFormatted;
    }


    public String getRSVPDateFormatted() {
        if (flagForRSVP == 0) {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyy");
            eventRSVPDateFormatted = format.format(Date.valueOf(eventRSVPDate));

        } else {
            eventRSVPDateFormatted = "false";
        }
        return eventRSVPDateFormatted;
    }

    public boolean startDateEqualsEndDate() {
        if (
                eventStartDateTime.get(Calendar.YEAR) == eventEndDateTime.get(Calendar.YEAR) &&
                        eventStartDateTime.get(Calendar.MONTH) == eventEndDateTime.get(Calendar.MONTH) &&
                        eventStartDateTime.get(Calendar.DAY_OF_MONTH) == eventEndDateTime.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public String getEventID() {
        return eventID;
    }


    public boolean isUserRsvped() {
        return isUserRsvped;
    }

    public void setUserRsvped(String userRsvped) {

        if (userRsvped.equals("1") || userRsvped.equalsIgnoreCase("true"))
            isUserRsvped = true;
        else
            isUserRsvped = false;
    }

}


