package de.paluno.mse.palaver.appdata;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by asus on 2017/5/17.
 */

public interface IConfigure {
    public boolean registerUser();

    /**
     * save a friend in database, after this friend was added.
     *
     * @param friendname
     * @return false if the friend already exists.
     */
    public boolean insertFriend(String friendname);

    public boolean deleteFriend(String friendname);

    /**
     * @param friendname
     * @param msg
     * @param date
     * @param type       0 from user to friend
     *                   1 from friend to user
     * @return false if the message already exists.
     */
    public boolean insertMsg(String friendname, String msg, String date, int type);

    /**
     * update friendsdata, after start app.
     *
     * @param dataFromServer
     * @return null if should not update; list of friendsname, which are new added.
     */
    public ArrayList<String>[] synchronizeFriendsData(JSONArray dataFromServer) throws JSONException;

    /**
     * @param order order by desc or asc(nullable)
     * @return friendslist with name.
     */
    public ArrayList<String> getFriendsList(String order);

    /**
     * @param friendname
     * @param extra      if extra= ROW:rows of msgs,which need to be read. if -1, all msgs will be readed.
     *                   if extra=OFFSET: msgs, wicht after specified time.
     * @param orderby    ASC or DESC
     * @return list of msg
     * note: order by DESC
     */
    public ArrayList<Message> getFriendsMsg(String friendname, ContentValues extra, String orderby);

    public void saveChatsItem(String[] name);

    public void removeChatsItem(String name);

    public String[] readChatsList();

}
