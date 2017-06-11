package com.cs442.group7.eventiit;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shyam on 10/23/16.
 */

public class DateTimeFormater {
    // Class to help create event for now... All date related processing
    private Integer day;
    private Integer month;
    private Integer year;
    private Integer hour;
    private Integer minute;
    private Calendar calenderDateTime;
    private boolean dateSet = false;//variable to check dateset and timeset
    private boolean timeSet = false;
    private int week;
    private String AM_PM;

    public DateTimeFormater() {

    }

    public DateTimeFormater(Calendar calenderDateTime) {
        //expects a calender object.
        day = calenderDateTime.get(Calendar.DAY_OF_MONTH);
        month = calenderDateTime.get(Calendar.MONTH)+1;
        year = calenderDateTime.get(Calendar.YEAR);
        hour = calenderDateTime.get(Calendar.HOUR_OF_DAY);
        minute = calenderDateTime.get(Calendar.MINUTE);
        week = calenderDateTime.get(Calendar.WEEK_OF_YEAR);

        setAMPM(hour);

        this.dateSet = true;
        this.timeSet = true;
    }
    public void setAMPM(int hour){
        if (hour < 12 && hour >= 0)
            AM_PM = "AM";
        else
            AM_PM = "PM";
    }

    // month starts from 1.
    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.timeSet = true;

        setAMPM(hour);


    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.dateSet = true;
    }

    public Calendar getCalenderInstance() {
        if (dateSet && timeSet) {
            //setTime and setDate should be called first.
            String value = year + "-" + month + "-" + day + "-" + hour + "-" + minute;
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            Date date = null;
            try {
                date = originalFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            this.calenderDateTime = Calendar.getInstance();
            this.calenderDateTime.setTime(date);
            return calenderDateTime;
        } else return null;
    }

    public String getSQLTime() {

        return hour + ":" + minute;
    }

    public String getSQLDate() {

        return year + "-" + month + "-" + day;
    }

    public String getSQLDateTime() {

        return year + "-" + month + "-" + day + " " + hour + ":" + minute;
    }

    public String getTextViewDate() {
        return new String((new DateFormatSymbols().getMonths()[(month) - 1]) + " " + day + " " + year);
    }

    public String getTextViewTime() {
        return new String(hour + " : " + String.format("%02d", minute));
    }

    public boolean isSet() {
        //if date and time both are set!!.
        return (dateSet && timeSet);
    }

    public Integer getWeek() {
        return week;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getHour(boolean is24Hour) {
        if (is24Hour)
            return hour;
        else {
            if (hour > 12) return hour - 12;
            else if (hour == 0) return 12;
            else return hour;
        }

    }

    public Integer getMinute() {
        return minute;
    }

    public String getAMPM() {
        return AM_PM;
    }

}
