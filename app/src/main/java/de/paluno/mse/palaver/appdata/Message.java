package de.paluno.mse.palaver.appdata;

import de.paluno.mse.palaver.MyApplication;

/**
 * Created by asus on 2017/6/7.
 */

public class Message extends MyData {
    private String date;
    private String message;
    private String friendname;
    private boolean newMsg;
//    private Date dateFormat;

    public Message(String friendname, String date, String message, boolean user_is_sender) {
        super(MyApplication.getAccount().getCurrentUsername());
        this.friendname = friendname;
        this.date = date;
        this.message = message;
        if (user_is_sender)
            this.setType(TYPE_SENDER);
        else this.setType(TYPE_RECIPIENT);
        newMsg = false;
    }

    public Message(String friendname, boolean user_is_sender) {
        super(MyApplication.getAccount().getCurrentUsername());
        this.friendname = friendname;
        if (user_is_sender)
            this.setType(TYPE_SENDER);
        else this.setType(TYPE_RECIPIENT);
        newMsg = false;

    }

    public Message(String friendname, String message, boolean user_is_sender) {
        super(MyApplication.getAccount().getCurrentUsername());
        this.friendname = friendname;
        this.message = message;
        if (user_is_sender)
            this.setType(TYPE_SENDER);
        else this.setType(TYPE_RECIPIENT);
        newMsg = false;

    }

    public Message(String friendname) {
        super(MyApplication.getAccount().getCurrentUsername());
        this.friendname = friendname;
        newMsg = false;
    }

    public boolean getNewMsg() {
        return newMsg;
    }

    public void setNewMsg(boolean newMsg) {
        this.newMsg = newMsg;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getFriendname() {
        return friendname;
    }

    public String getDay() {
        return date.substring(0, 10);
    }

    public String getTime() {
        return date.substring(11, 16);
    }

    public String getTimeToSecond() {
        return date.substring(11, 19);
    }
}
