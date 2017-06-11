package com.cs442.group7.eventiit;

/**
 * Created by Shyam on 12/9/16.
 */

public class MessageObj{


    private String header;
    private String content;



    private int id;

    private boolean isAlert = false;

    public MessageObj (int id, String header, String content){
        this.id = id;
        this.header = header;
        this.content = content;
    }

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }

    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
        isAlert = alert;
    }

    public int getID() {
        return id;
    }

    public void setID(int ID) {
        this.id = ID;
    }
}