package de.paluno.mse.palaver.appdata;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by asus on 2017/5/29.
 */

public class MyDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "MYPALAVER_DATABASE";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Account.db";
    private static final String SQL_CREATE_USER = "create table " + User.TABLE_NAME + " ( "
            + User.USERID + " integer primary key autoincrement, "
            + User.USERNAME + " text unique)";
    private static final String SQL_CREATE_FRIEND = "create table " + Friend.TABLE_NAME + " ( "
            + Friend.INDEX + " integer primary key autoincrement,"
            + Friend.USERNAME + " text  REFERENCES " + User.TABLE_NAME + " ( " + User.USERNAME + " )ON DELETE CASCADE ON UPDATE CASCADE, "
            + Friend.FRIENDNAME + " text not null,"
            + " UNIQUE( " + Friend.USERNAME + " , " + Friend.FRIENDNAME + " ))";
    private static final String SQL_CREATE_MESSAGE = "create table " + Message.TABLE_NAME + " ( "
            + Message.INDEX + " integer not null references " + Friend.TABLE_NAME + "( " + Friend.INDEX + " )ON DELETE CASCADE ON UPDATE CASCADE,"
            + Message.DATE + " text NOT NULL, "
            + Message.MESSAGE + " text, "
            + Message.PHOTEPATH + " text, "
            + Message.TYPE + " int, "
            + "primary key( " + Message.INDEX + " , " + Message.DATE + " , " + Message.TYPE + " ))";
//    private static final String SQL_CREATE_CHATLIST = "create table " + ChatList.TABLE_NAME + " ( "
//            + ChatList.ID + " integer primary key autoincrement, "
//            + ChatList.USERNAME + " text REFERENCES " + Friend.TABLE_NAME + " ( " + Friend.USERNAME + " )ON DELETE CASCADE ON UPDATE CASCADE, "
//            + ChatList.FRIENDNAME + " text REFERENCES " + Friend.TABLE_NAME + " ( " + Friend.FRIENDNAME + " )ON DELETE CASCADE ON UPDATE CASCADE, "
//            + " UNIQUE( " + ChatList.USERNAME + " , " + ChatList.FRIENDNAME + " ))";

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");
        db.execSQL(SQL_CREATE_USER);
        Log.i(TAG, SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_FRIEND);
        Log.i(TAG, SQL_CREATE_FRIEND);
        db.execSQL(SQL_CREATE_MESSAGE);
        Log.i(TAG, SQL_CREATE_MESSAGE);
//        db.execSQL(SQL_CREATE_CHATLIST);
//        Log.i(TAG, SQL_CREATE_CHATLIST);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


    public static class User {
        public static final String TABLE_NAME = "user";
        public static final String USERID = "id";
        public static final String USERNAME = "username";
    }

    public static class Friend {
        public static final String TABLE_NAME = "friend";
        public static final String INDEX = "friendindex";
        public static final String USERNAME = "username";
        public static final String FRIENDNAME = "friendname";
    }

    public static class Message {
        public static final String TABLE_NAME = "message";
        public static final String INDEX = "friendindex";
        public static final String MESSAGE = "message";
        public static final String PHOTEPATH = "photopath";
        public static final String DATE = "date";
        public static final String TYPE = "type";
    }

    public static class ChatList {
        public static final String TABLE_NAME = "chatlist";
        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String FRIENDNAME = "friendname";
    }

}
