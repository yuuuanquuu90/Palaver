package de.paluno.mse.palaver.appdata;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by asus on 2017/5/17.
 */

public class Account implements IConfigure, MySharedPreferences {
    private SharedPreferences sp;
    private SQLiteDatabase wdb;
    private SQLiteDatabase rdb;
    private static Account account;
    private final static int FRIEND_ALREAD_EXISTS = -1;
    private final static String TAG = "MYPALAVER_CLASS_ACCOUNT";
    //FOR TABLE
    //table user
    private final String TABLE_USER = MyDbHelper.User.TABLE_NAME;
    private final String USERNAME = MyDbHelper.User.USERNAME;
    //table friend
    private final String TABLE_FRIEND = MyDbHelper.Friend.TABLE_NAME;
    private final String FRIENDNAME = MyDbHelper.Friend.FRIENDNAME;
    private final String FRIENDINDEX = MyDbHelper.Friend.INDEX;
    //table msg
    private final String TABLE_MSG = MyDbHelper.Message.TABLE_NAME;
    private final String DATE = MyDbHelper.Message.DATE;
    private final String MSG = MyDbHelper.Message.MESSAGE;
    private final String PHTO = MyDbHelper.Message.PHOTEPATH;
    private final String MSG_TYPE = MyDbHelper.Message.TYPE;

    public Account(Context context) {
        sp = context.getSharedPreferences("LoginState", Context.MODE_PRIVATE);
        MyDbHelper mDb = new MyDbHelper(context);
        wdb = mDb.getWritableDatabase();
        rdb = mDb.getReadableDatabase();
    }

    public static void setAccount(Context context) {
        account = new Account(context);
        Log.i(TAG, "account was initialized");
    }

    public static Account getAccount() {
        return account;
    }

    @Override
    public void saveLoginState(Context context, String username, String password, boolean state) {
        Editor editor = sp.edit();
        editor.putString("Username", username);
        editor.putString("Password", password);
        editor.putBoolean("Login", state);
        editor.apply();
    }


    @Override
    public void Signout(Context context) {
        Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void saveChatsItem(String[] name) {
        if (name.length == 0)
            return;
        StringBuilder list = new StringBuilder();
        for (String s : name) {
            list.append(s);
            list.append(",");
        }
        list.deleteCharAt(list.length() - 1);
        Editor editor = sp.edit();
        editor.putString("chatlist", list.toString());
        editor.apply();
        Log.i(TAG, "chat list is " + list.toString());
    }

    @Override
    public void removeChatsItem(String name) {
        String oldList = sp.getString("chatlist", null);
        StringBuilder out = new StringBuilder();
        if (oldList == null)
            Log.e(TAG, "CHATSITEM EXCEPTION");
        else {
            String[] newList = oldList.split(",");
            for (int i = 0; i < newList.length; i++) {
                if (!newList[i].equals(name)) {
                    out.append(newList[i]);
                    if (i != newList.length - 1)
                        out.append(",");
                }
            }
        }
        Editor editor = sp.edit();
        if (out.length() > 0)
            editor.putString("chatlist", out.toString());
        else editor.putString("chatlist", null);
        editor.apply();
    }

    @Override
    public String[] readChatsList() {
        String tmp = sp.getString("chatlist", null);
        if (tmp == null)
            return null;
        else
            return tmp.split(",");
    }

    @Override
    public boolean checkLoginState(Context context) {
        return sp.getBoolean("Login", false);
    }

    @Override
    public String getCurrentUsername() {
        return sp.getString("Username", "");
    }

    @Override
    public String getPassword() {
        return sp.getString("Password", "");
    }

    @Override
    public void changePassword(String newpassword) {
        Editor editor = sp.edit();
        editor.putString("Password", newpassword);
        System.out.println(newpassword);
        editor.apply();
    }

    @Override
    public String getCommando() {
        return sp.getString("Commando", "");
    }


    @Override
    public boolean insertFriend(String friendname) {
        long newRowId = insertFriend(getCurrentUsername(), friendname);
        Log.i(TAG, "Row id =: " + newRowId);
        return !(newRowId == -1);
    }

    @Override
    public boolean deleteFriend(String friendname) {
//        String selection = MyDbHelper.Friend.USERNAME + " = ? and "+ FRIENDNAME+ " = ?";
//        String[] selectionArgs = {getCurrentUsername(),friendname};
//        return !(deleteData(TABLE_FRIEND, selection, selectionArgs) == -1);
        wdb.execSQL("delete from " + TABLE_FRIEND + " where " + MyDbHelper.Friend.USERNAME + " = ?  and " + FRIENDNAME + " = ? ", new String[]{getCurrentUsername(), friendname});
        return true;
    }

    @Override
    public boolean insertMsg(String friendname, String msg, String date, int type) {
        ContentValues values = new ContentValues();
        int index = readUserFriendPair(getCurrentUsername(), friendname);
        values.put(MyDbHelper.Message.INDEX, index);
        values.put(MSG, msg);
        values.put(DATE, date);
        values.put(MSG_TYPE, type);
        long newRowId = wdb.insert(TABLE_MSG, null, values);
        Log.i(TAG, "Msg inserted." + " msg: " + msg + " friend " + friendname + " date " + date + " type " + type + " row " + newRowId);
        return !(newRowId == -1);
    }

    @Override
    public ArrayList<String>[] synchronizeFriendsData(JSONArray dataFromServer) throws JSONException {
        if (dataFromServer == null) {
            Log.e(TAG, "server error");
            return null;
        }
//        long count;
//        Cursor cursor = rdb.rawQuery("select count(*) from " + TABLE_FRIEND + " where " + MyDbHelper.Friend.USERNAME + " =?", new String[]{getCurrentUsername()});
//        cursor.moveToFirst();
//        count = cursor.getLong(0);
//        Log.i(TAG, "count of table friend=: " + String.valueOf(count));
//        cursor.close();
//        if (count == dataFromServer.length()) {
//            Log.i(TAG, "do not need to synchronize");
//            return null;
//        } else {
//            Log.i(TAG, "need to synchronize");
//            //  very rare and costly
//            ArrayList<String> out = new ArrayList<>();
//            for (int i = 0; i < dataFromServer.length(); i++) {
//                String friendname = dataFromServer.getString(i);
//                if (insertFriend(getCurrentUsername(), friendname) != FRIEND_ALREAD_EXISTS) {
//                    out.add(friendname);
//                    Log.i(TAG, friendname + " needs to be added");
//                }
//            }
//
//            return out;

//        }
        ArrayList<String> friends = getFriendsList(null);
        ArrayList<String> add = new ArrayList<>();
        ArrayList<String> delete = new ArrayList<>();
        for (int i = 0; i < dataFromServer.length(); i++) {
            if (insertFriend(dataFromServer.getString(i)))
                add.add(dataFromServer.getString(i));
        }
        for (String f : friends) {
            boolean remove = true;
            for (int i = 0; i < dataFromServer.length(); i++) {
                if (dataFromServer.getString(i).equals(f))
                    remove = false;
            }
            if (remove) {
                delete.add(f);
                deleteFriend(f);
            }
        }
        ArrayList<String>[] out = new ArrayList[2];
        out[0] = add;
        out[1] = delete;
        return out;
    }

    @Override
    public ArrayList<String> getFriendsList(String order) {
        ArrayList<String> out = new ArrayList<>();
        Cursor cursor;
        if (order == null)
            cursor = rdb.rawQuery("select " + FRIENDNAME + " from " + TABLE_FRIEND + " where " + MyDbHelper.Friend.USERNAME + " =?", new String[]{getCurrentUsername()});
        else
            cursor = rdb.rawQuery("select " + FRIENDNAME + " from " + TABLE_FRIEND + " where " + MyDbHelper.Friend.USERNAME + " =?" + " order by " + FRIENDNAME + " " + order + " ", new String[]{getCurrentUsername()});

        if (cursor.moveToFirst())
            out.add(cursor.getString(cursor.getColumnIndexOrThrow(FRIENDNAME)));
        else {
            Log.i(TAG, "friendslist from " + getCurrentUsername() + "'s database" + " =null");
            return out;
        }
        while (cursor.moveToNext()) {
            out.add(cursor.getString(cursor.getColumnIndexOrThrow(FRIENDNAME)));
        }
        return out;
    }

    @Override
    public ArrayList<Message> getFriendsMsg(String friendname, ContentValues extra, String orderby) {
        ArrayList<Message> out = new ArrayList<>();
        Cursor cursor = rdb.rawQuery(
                "select " + MSG + " , " + DATE + " , " + MSG_TYPE +
                        " from " + TABLE_FRIEND + " natural join " + TABLE_MSG +
                        " where " + USERNAME + "=? " + " and " + FRIENDNAME + "=? " +
                        "order by " + DATE + " " + orderby, new String[]{getCurrentUsername(), friendname});
        if (cursor.moveToFirst()) {
            String msg = cursor.getString(cursor.getColumnIndexOrThrow(MSG));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DATE));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(MSG_TYPE));
            out.add(new Message(friendname, date, msg, type == MyData.TYPE_SENDER));
        }
        int row = extra.getAsInteger("ROW");
        if (row > 1) {
            while (cursor.moveToNext() && (row > 1)) {
                row--;
                String msg = cursor.getString(cursor.getColumnIndexOrThrow(MSG));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DATE));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(MSG_TYPE));
                out.add(new Message(friendname, date, msg, type == MyData.TYPE_SENDER));
            }
        } else if (row == -1) {
            while (cursor.moveToNext()) {
                row--;
                String msg = cursor.getString(cursor.getColumnIndexOrThrow(MSG));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DATE));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(MSG_TYPE));
                out.add(new Message(friendname, date, msg, type == MyData.TYPE_SENDER));
            }
        }

        return out;

    }


    /**
     * register current user in database.
     *
     * @return false if user already exists.
     */
    @Override
    public boolean registerUser() {
        long newRowId = insertUser(getCurrentUsername());
        if (newRowId > 0) {
            Log.i(TAG, "Register successful");
            return true;
        } else {
            Log.w(TAG, "User already exists.");
            return false;
        }

    }


    /**
     * @param name username
     * @return rowId or -1 if was not inserted.
     */
    private long insertUser(String name) {
        ContentValues values = new ContentValues();
        values.put(USERNAME, name);
        return wdb.insert(TABLE_USER, null, values);
    }

    /**
     * @param name       username
     * @param friendname friendname
     * @return rowId or -1 if was not inserted.
     */
    private long insertFriend(String name, String friendname) {
        ContentValues values = new ContentValues();
        values.put(MyDbHelper.Friend.USERNAME, name);
        values.put(FRIENDNAME, friendname);
        Log.i(TAG, "insert friend " + friendname);
        return wdb.insert(TABLE_FRIEND, null, values);
    }


    private int readUserFriendPair(String username, String friendname) {
        Cursor cursor = rdb.rawQuery(
                "select " + FRIENDINDEX +
                        " from " + TABLE_FRIEND +
                        " where " + USERNAME + "=? and " + FRIENDNAME + "=? ",
                new String[]{username, friendname});
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndexOrThrow(FRIENDINDEX));
        } else return -1;
    }
}
