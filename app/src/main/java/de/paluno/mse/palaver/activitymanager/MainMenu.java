package de.paluno.mse.palaver.activitymanager;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.R;
import de.paluno.mse.palaver.appdata.Message;
import de.paluno.mse.palaver.appdata.MyData;
import de.paluno.mse.palaver.informationexchanger.Commando;
import de.paluno.mse.palaver.informationexchanger.Connection;
import de.paluno.mse.palaver.generalhelper.EncapsulationHelper;
import de.paluno.mse.palaver.informationexchanger.IReceive;

public class MainMenu extends AppCompatActivity implements IReceive {
    private final static String TAG = "MYPALAVER_MAIN_MENU";
    private ViewpagerAdapter viewpagerAdapter;
    private ViewPager viewPager;
    public final static int ADDFRIEND = 0;
    private GcmBroadcastingReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setToolbar();
        initViewPager();
        //synchronize date
        synchronizeFriendData();
        br = new GcmBroadcastingReceiver();
        registerReceiver(br, new IntentFilter(getString(R.string.GCM)));


    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainmenu_app_bar_signout:
                MyApplication.getAccount().Signout(this);
                ((Chats) getFragment(R.layout.fragment_chats)).deleteAllItem();
                Intent intent1 = new Intent(MainMenu.this, Login.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                return true;
            case R.id.mainmenu_app_bar_add:
                Intent intent = new Intent(MainMenu.this, AddFriends.class);
                startActivityForResult(intent, ADDFRIEND);
                return true;
            case R.id.mainmenu_app_bar_changepassword:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog, null);
        builder.setView(view)
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String op = ((EditText) view.findViewById(R.id.oldpassword)).getText().toString();
                        String np1 = ((EditText) view.findViewById(R.id.newpassword1)).getText().toString();
                        String np2 = ((EditText) view.findViewById(R.id.newpassword2)).getText().toString();
                        if (np1.equals(np2) && op.equals(MyApplication.getAccount().getPassword())) {
                             MyApplication.getConnection().password(MainMenu.this, EncapsulationHelper.password(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), np1), new String[]{np1});
                        } else if (!op.equals(MyApplication.getAccount().getPassword())) {
                            Toast.makeText(MainMenu.this, R.string.error_incorrect_password, Toast.LENGTH_SHORT).show();

                        } else if (np1.equals("") && np2.equals("")) {
                            Toast.makeText(MainMenu.this, R.string.error_empty_password, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(MainMenu.this, R.string.error_password_inconsistent, Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String friend;
        if (data != null) {
            switch (resultCode) {
                case ADDFRIEND:
                    if (data.getStringExtra("name") != null) {
                        friend = data.getStringExtra("name");
                        Log.i(TAG, "onActivityResult: add " + friend);
                        ((Contacts) getFragment(R.layout.fragment_contacts)).insertIteminView(friend);
                    } else if (data.getStringExtra("exists") != null) {
                        Log.i(TAG, "friend exists, move to chats.");
                        friend = data.getStringExtra("exists");
                        ((Chats) getFragment(R.layout.fragment_chats)).startChat(friend);
                    }
                    break;
                default:
                    break;
            }
        } else Log.e(TAG, "onActivityResult: data is null");
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void afterReceivefromServer(JSONObject jo) {
        try {
            executePassword(jo);
            if (MyApplication.replySuccess(jo)) {
                if (jo.getString("Commando").equals(Commando.GETFRIENDS.getCommando()))
                    executeGetFriends(jo);
                if (jo.getString("Commando").equals(Commando.GETMESSEGE.getCommando()) || jo.getString("Commando").equals(Commando.GETOFFSET.getCommando()))
                    executeInsertMsg(jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void executePassword(JSONObject jo) {
        try {
            if (jo.getString("Commando").equals(Commando.PASSWORD.getCommando())) {
                if (MyApplication.replySuccess(jo)) {
                    Toast.makeText(this, "successful", Toast.LENGTH_SHORT).show();
                    MyApplication.getAccount().changePassword(jo.getString("newpassword"));
                } else Toast.makeText(this, "not successful", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void executeGetFriends(JSONObject jo) {
        try {
            JSONArray list = jo.getJSONArray("Data");
            //1.check the database
            ArrayList<String>[] syn = MyApplication.getAccount().synchronizeFriendsData(list);
            ArrayList<String> add = syn[0];
            ArrayList<String> remove = syn[1];
            if (add != null) {
//                while (!((Contacts) getFragment(R.layout.fragment_contacts)).oncreated) {
//                    Log.e(TAG, "creation of contacts not ready");
//                }
                //2. should update contact list
                for (int i = 0; i < add.size(); i++) {
                    ((Contacts) getFragment(R.layout.fragment_contacts)).insertIteminView(add.get(i));
                }
            }
            if (remove != null) {
                for (String r : remove)
                    ((Contacts) getFragment(R.layout.fragment_contacts)).removeIteminView(r);
            }
            Log.i(TAG, "contacts is synchronized.");
            Log.i(TAG, "do refresh msgs");
            refreshMsgs();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void executeInsertMsg(JSONObject jo) {
        try {
            JSONArray ja = jo.getJSONArray("Data");
            if (ja != null && ja.length() > 0) {
                ArrayList<Message> list = new ArrayList<>();
                for (int i = 0; i < ja.length(); i++) {
                    //insert msgs in database
                    ContentValues v = EncapsulationHelper.decapsulate(ja.getJSONObject(i));
                    String fn = v.getAsString("Friendname");
                    String ms = v.getAsString("Msg");
                    String dt = v.getAsString("DateTime");
                    int tp = v.getAsInteger("Type");
                    if (MyApplication.getAccount().insertMsg(fn, ms, dt, tp)) {
                        Log.i(TAG, fn + " :msg has been inserted");
                        list.add(new Message(fn, dt, ms, tp == MyData.TYPE_SENDER));
                    } else Log.e(TAG, fn + " MSG has not been inserted");
                    //TODO send,save photo
                }
                //set Vibrator
//                VibratorUtils.Vibrate(this, 300);
                //send broadcasting
                Intent intent = new Intent(getString(R.string.REFRESHMSGINCONVERSATION));
                intent.putExtra(getString(R.string.size), list.size());
                for (int i = 0; i < list.size(); i++) {
                    intent.putExtra("m" + i, new String[]{list.get(i).getFriendname(), list.get(i).getDate(), list.get(i).getMessage(), String.valueOf(list.get(i).getType())});
                }
                if (list.size() > 0) {
                    intent.putExtra("sender", list.get(0).getFriendname());
                    sendOrderedBroadcast(intent, null);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void initViewPager() {
        //set pager adapter.
        viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.mainmenu_viewpager);
        viewPager.setAdapter(viewpagerAdapter);
        //set tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.mainmenu_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainmenu_toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

    }

    private void synchronizeFriendData() {
//        new Connection(this).execute(EncapsulationHelper.validate(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword()), Commando.GETFRIENDS.getPath(), Commando.GETFRIENDS.getCommando());
         MyApplication.getConnection().getfriend(this, EncapsulationHelper.validate(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword()));
    }


    public Fragment getFragment(int id) {
        switch (id) {
            case R.layout.fragment_chats:
                return viewpagerAdapter.getItem(0);
            case R.layout.fragment_contacts:
                return viewpagerAdapter.getItem(1);
            default:
                Log.e(TAG, "getfragment error");
                return null;
        }
    }

    void refreshMsgs() {
        ArrayList<String> list = MyApplication.getAccount().getFriendsList(null);
        for (String friend : list)
            refreshMsg(friend);
    }

    //insert new msgs from server in datebase.
    void refreshMsg(String friendname) {
        //get time from the last msg.
        ContentValues v = new ContentValues();
        v.put("ROW", 1);
        //insert friend optimal
        if (MyApplication.getAccount().insertFriend(friendname))
            ((Contacts) getFragment(R.layout.fragment_contacts)).insertIteminView(friendname);
        //msg !=null get offset.
        ArrayList<Message> msglist = MyApplication.getAccount().getFriendsMsg(friendname, v, "DESC");
        if (msglist != null && msglist.size() > 0) {
            String offset = msglist.get(0).getDate();
            Log.i(TAG, "offeset is: " + offset);
//                new Connection(iReceive).execute(EncapsulationHelper.getoffset(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), sender, offset), Commando.GETOFFSET.getPath(), Commando.GETOFFSET.getCommando());
             MyApplication.getConnection().getmessageoffset(this, EncapsulationHelper.getoffset(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), friendname, offset));
        } else
//                new Connection(iReceive).execute(EncapsulationHelper.getMsg(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), sender), Commando.GETMESSEGE.getPath(), Commando.GETMESSEGE.getCommando());
             MyApplication.getConnection().getmessage(this, EncapsulationHelper.getMsg(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), friendname));


    }


    private class ViewpagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        ViewpagerAdapter(FragmentManager fm) {
            super(fm);
            //create fragements.
            fragmentList = new ArrayList<>();
            fragmentList.add(new Chats());
            fragmentList.add(new Contacts());
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chats";
                case 1:
                    return "Contacts";
                default:
                    return null;
            }
        }
    }

    private class GcmBroadcastingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String sender = intent.getStringExtra("sender");
            refreshMsg(sender);
        }
    }


}
