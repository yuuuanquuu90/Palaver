package de.paluno.mse.palaver.activitymanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.R;
import de.paluno.mse.palaver.appdata.Message;
import de.paluno.mse.palaver.appdata.MyData;

/**
 * Created by asus on 2017/5/24.
 */
//TODO Notification.
public class Chats extends Fragment {
    private final static String TAG = "MYPALAVER_CHATS";
    private MyAdapter myAdapter;
    private BroadcastReceiver br;
    private BroadcastReceiver br2;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        ListView mListview = (ListView) rootView.findViewById(R.id.chats_listview);
        mListview.setAdapter(myAdapter);
        //On Item Clieck Listener
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fn = ((Message) parent.getItemAtPosition(position)).getFriendname();
                startConversation(fn);
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        Log.e(TAG, "on create");
        myAdapter = new MyAdapter(getContext(), R.layout.item_chats_chat);
        br = new DeleteChatReceiver();
        getContext().registerReceiver(br, new IntentFilter(getString(R.string.DELETE_BROADCASTING)));
        br2 = new GcmBroadcastReceiver();
        IntentFilter ifl = new IntentFilter(getString(R.string.REFRESHMSGINCONVERSATION));
        ifl.setPriority(100);
        getContext().registerReceiver(br2, ifl);
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(br);
        getContext().unregisterReceiver(br2);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        MyApplication.getAccount().saveChatsItem(getAdapter().getAllItem());
        super.onPause();
    }

    public void startChat(String friendname) {
        getAdapter().addChatItem(friendname);
        startConversation(friendname);

    }

    private void startConversation(String friendname) {
        Intent intent = new Intent(getActivity(), Conversation.class);
        intent.putExtra(getString(R.string.FRIENDNAME), friendname);
        startActivity(intent);
        getAdapter().putItemonTop(friendname);
        getAdapter().setChatOverlay(friendname, false);
    }

    void deleteAllItem() {
        getAdapter().deleteAllItem();
    }

    MyAdapter getAdapter() {
        return myAdapter;
    }

    private void setNotification(Message msg) {
        Intent intent = new Intent(getActivity(), MainMenu.class);
        PendingIntent pI = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder = new NotificationCompat.Builder(getContext());
        mBuilder.setSmallIcon(R.drawable.ic_mms_black_24dp)
                .setTicker("you have a new message.")
                .setContentTitle(msg.getFriendname())
                .setContentText(msg.getMessage())
                .setContentIntent(pI)
                .setAutoCancel(true);
        mManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(1, mBuilder.build());


    }

    private class DeleteChatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String fn = intent.getStringExtra(getString(R.string.FRIENDNAME));
            getAdapter().deleteChatItem(fn);
        }
    }

    private class GcmBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Receiver captures the broadcasting");
            Bundle b = getResultExtras(true);
            if (b.getString("added") == null) {
                String name = intent.getStringExtra("sender");
                getAdapter().addChatItem(name);
                getAdapter().setChatOverlay(name, true);
                int size = intent.getIntExtra(getString(R.string.size), -1);
                if (size != -1) {
                    String[] msg = intent.getStringArrayExtra("m" + String.valueOf(size - 1));
                    setNotification(new Message(msg[0], msg[1], msg[2], Integer.parseInt(msg[3]) == MyData.TYPE_SENDER));
                }
            }
        }
    }

    //*****************************Adapter**********************
    private class MyAdapter extends ArrayAdapter {
        private final LayoutInflater mInflater;
        private final int mResource;
        private ArrayList<Message> mChats;

        MyAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
            mInflater = LayoutInflater.from(context);
            mResource = resource;
            mChats = new ArrayList<>();
            initChatList();
        }


        @Override
        public int getCount() {
            return mChats.size();
        }

        @Override
        public Object getItem(int position) {
            return mChats.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            Message mData = (Message) getItem(position);
            if (convertView == null) {
                view = mInflater.inflate(mResource, parent, false);
            } else view = convertView;
            ((TextView) view.findViewById(R.id.chat_date)).setText(mData.getDate());
            ((TextView) view.findViewById(R.id.chat_message)).setText(mData.getMessage());
            ((TextView) view.findViewById(R.id.chat_name)).setText(mData.getFriendname());
            if (mData.getNewMsg())
                view.findViewById(R.id.chat_notification_overlay).setVisibility(View.VISIBLE);
            else view.findViewById(R.id.chat_notification_overlay).setVisibility(View.GONE);
            return view;
        }

        private void initChatList() {
            String[] list = MyApplication.getAccount().readChatsList();
            if (list != null)
                for (int i = list.length - 1; i >= 0; i--)
                    addChatItem(list[i]);
        }

        private void addChatItem(String friendname) {
            for (int i = 0; i < mChats.size(); i++) {
                if (mChats.get(i).getFriendname().equals(friendname)) {
                    Log.e(TAG, "friend chat content already exists");
                    return;
                }
            }
            Log.e(TAG, "create new friend chat content");
            mChats.add(0, BuildItemView(friendname));
            notifyDataSetChanged();
        }

        private void deleteChatItem(String name) {
            for (int i = 0; i < mChats.size(); i++) {
                if (mChats.get(i).getFriendname().equals(name)) {
                    mChats.remove(i);
                    notifyDataSetChanged();
                }
            }
            MyApplication.getAccount().removeChatsItem(name);
        }

        String[] getAllItem() {
            String[] list = new String[getCount()];
            for (int i = 0; i < getCount(); i++) {
                list[i] = ((Message) getItem(i)).getFriendname();
            }

            return list;
        }

        void deleteAllItem() {
            mChats = new ArrayList<>();
            notifyDataSetChanged();
        }

        void putItemonTop(String name) {
            deleteChatItem(name);
            mChats.add(0, BuildItemView(name));
            notifyDataSetChanged();

        }

        private Message BuildItemView(String friendname) {
            ContentValues v = new ContentValues();
            v.put("ROW", 1);
            ArrayList<Message> tmp = MyApplication.getAccount().getFriendsMsg(friendname, v, "DESC");
            if (tmp != null && tmp.size() > 0) {
                return tmp.get(0);
            } else return new Message(friendname, " ", " ", true);

        }

        void setChatOverlay(String name, boolean newmsg) {
            for (Message m : mChats)
                if (m.getFriendname().equals(name))
                    m.setNewMsg(newmsg);
            notifyDataSetChanged();
        }
    }
}
