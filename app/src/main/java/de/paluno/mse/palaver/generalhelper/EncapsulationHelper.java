package de.paluno.mse.palaver.generalhelper;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.appdata.MyData;

/**
 * Created by asus on 2017/6/14.
 */

public class EncapsulationHelper {

    public static String register(String username, String password) {
        return "{\"Username\":\"" + username + "\",\"Password\":\"" + password + "\"}";

    }

    public static String validate(String username, String password) {
        return "{\"Username\":\"" + username + "\",\"Password\":\"" + password + "\"}";
    }

    public static String addFriends(String username, String password, String friendname) {
        return "{\"Username\":\"" + username + "\", \"Password\":\"" + password + "\",\"Friend\":\"" + friendname + "\"}";

    }

    public static String removeFriends(String username, String password, String friendname) {
        return "{\"Username\":\"" + username + "\", \"Password\":\"" + password + "\",\"Friend\":\"" + friendname + "\"}";
    }

    public static String pushtoken(String username, String password, String pushtoken) {
        return "{\"Username\":\"" + username + "\",\"Password\":\"" + password + "\",\"PushToken\":\"" + pushtoken + "\"}";

    }

    public static String sendMessage(String username, String password, String recipient, String mimetype, String msg) {
        return "{\"Username\":\"" + username + "\",\"Password\":\"" + password + "\",\"Recipient\":\"" + recipient + "\",\"Mimetype\":\"" + mimetype + "\",\"Data\":\"" + msg + "\"}";
    }

    public static String getoffset(String username, String password, String recipient, String offset) {
        return "{\"Username\":\"" + username + "\",\"Password\":\"" + password + "\",\"Recipient\":\"" + recipient + "\",\"Offset\":\"" + offset + "\"}";

    }

    public static String getMsg(String username, String password, String recipient) {
        return "{\"Username\":\"" + username + "\",\"Password\":\"" + password + "\",\"Recipient\":\"" + recipient + "\"}";

    }

    public static String password(String username, String password, String newpassword) {
        return "{\"Username\":\"" + username + "\",\"Password\":\"" + password + "\",\"Newpassword\":\"" + newpassword + "\"}";

    }

    public static ContentValues decapsulate(JSONObject jo) {
        ContentValues v = new ContentValues();
        try {
            String sender = jo.getString("Sender");
            String recipient = jo.getString("Recipient");
            String mimetype = jo.getString("Mimetype");
            String msg = jo.getString("Data");
            String datetime = jo.getString("DateTime");
            int type;
            if (recipient.equals(MyApplication.getAccount().getCurrentUsername())) {
                type = MyData.TYPE_RECIPIENT;
                v.put("Friendname", sender);
            } else {
                type = MyData.TYPE_SENDER;
                v.put("Friendname", recipient);
            }
            v.put("Mimetype", mimetype);
            v.put("Msg", msg);
            v.put("DateTime", datetime.substring(0, 19));
            v.put("Type", type);
            return v;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
