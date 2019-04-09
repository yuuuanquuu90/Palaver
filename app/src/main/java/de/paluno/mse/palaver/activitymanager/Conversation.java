package de.paluno.mse.palaver.activitymanager;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.paluno.mse.palaver.appdata.Category;
import de.paluno.mse.palaver.appdata.Message;
import de.paluno.mse.palaver.appdata.MyData;
import de.paluno.mse.palaver.generalhelper.ButtonUtils;
import de.paluno.mse.palaver.generalhelper.ConnectionHelper;
import de.paluno.mse.palaver.informationexchanger.Commando;
import de.paluno.mse.palaver.generalhelper.EncapsulationHelper;
import de.paluno.mse.palaver.informationexchanger.IReceive;
import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.R;

public class Conversation extends AppCompatActivity implements IReceive {
    private final static String TAG = "MYPALAVER_CONVERSTION";
    private String FRIEND;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private EditText mtext;
    private GcmBroadcastReceiver br;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        mtext = (EditText) findViewById(R.id.conversation_msg);
        initConversation();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.conversation_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonUtils.isFastDoubleClick()) {
                    sendMsg(FRIEND, "text/plain", mtext.getText().toString());
                    getAdapter().addItem(new Message(FRIEND, mtext.getText().toString(), true));
                    mtext.setText("");
                } else
                    Toast.makeText(Conversation.this, "Please wait a little while.", Toast.LENGTH_SHORT).show();

            }
        });
        if (mAdapter.getItemCount() > 1)
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);

    }


    @Override
    protected void onResume() {
        //broadcasting
        br = new GcmBroadcastReceiver();
        IntentFilter inf = new IntentFilter(getString(R.string.REFRESHMSGINCONVERSATION));
        inf.setPriority(1000);
        registerReceiver(br, inf);
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(br);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "on destroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.conversation_menu_syn:
                findViewById(R.id.conversation_syn_progressbar).setVisibility(View.VISIBLE);
                synMsg();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void synMsg() {
//        new Connection(this).execute(EncapsulationHelper.getMsg(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), FRIEND), Commando.GETMESSEGE.getPath(), Commando.GETMESSEGE.getCommando());
        MyApplication.getConnection().getmessage(this, EncapsulationHelper.getMsg(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), FRIEND));
    }

    private void sendMsg(String recipient, String mimetype, String msg) {
//        new Connection(this).execute(EncapsulationHelper.sendMessage(MyApplication.getAccount()
//                        .getCurrentUsername(), MyApplication.getAccount().getPassword(), recipient,
//                mimetype, msg), Commando.SEND.getPath(), Commando.SEND.getCommando(), recipient,
//                msg);
        MyApplication.getConnection().sendmessage(this, EncapsulationHelper.sendMessage(MyApplication.getAccount()
                        .getCurrentUsername(), MyApplication.getAccount().getPassword(), recipient,
                mimetype, msg), new String[]{recipient, msg});
        Log.i(TAG, "send new msg to " + recipient + " with msg " + msg);
    }

    private void initConversation() {
        FRIEND = getIntent().getStringExtra("friendname");
        setTitle(FRIEND);
        initRecyclerView();
        initMsg();
    }

    //TODO show (if possible) top 10~20 msgs of this user.
    private void initMsg() {
        ContentValues v = new ContentValues();
        v.put("ROW", -1);
        ArrayList<Message> msgs = MyApplication.getAccount().getFriendsMsg(FRIEND, v, "ASC");
        if (msgs.size() > 0) {
            Log.i(TAG, "insert msgs");
            for (Message msg : msgs)
                getAdapter().insertItem(msg);
            Log.i(TAG, getAdapter().getItemCount() + " items have been inserted");

        } else Log.i(TAG, "no msgs need to be inited");
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.conversation_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        //TODO SET ANIMATION.....


    }


    @Override
    public void afterReceivefromServer(JSONObject jo) {
        if (MyApplication.replySuccess(jo)) {
            //msg save in local database
            try {
                if (jo.getString("Commando").equals(Commando.SEND.getCommando())) {
                    String cdate = jo.getString("Data").substring(13, 32);
                    String cmsg = jo.getString("msg");
                    MyApplication.getAccount().insertMsg(FRIEND, cmsg, cdate, MyData.TYPE_SENDER);
                    //nofity mAdapter
                    getAdapter().msgsended(cmsg, cdate);
                } else if (jo.getString("Commando").equals(Commando.GETMESSEGE.getCommando())) {
                    new Thread(new insertMsgs(jo)).start();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    MyAdapter getAdapter() {
        return mAdapter;
    }

    private void scrollToLastPosition() {
        mRecyclerView.smoothScrollToPosition(getAdapter().getItemCount() - 1);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Category> mCategory;

        MyAdapter() {
            mCategory = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            ViewHolder vh;
            switch (viewType) {
                case MyData.TYPE_SENDER:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_sender, parent, false);
                    vh = new ViewHolder(v, MyData.TYPE_SENDER);
                    return vh;
                case MyData.TYPE_RECIPIENT:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_recipient, parent, false);
                    vh = new ViewHolder(v, MyData.TYPE_RECIPIENT);
                    return vh;
                case MyData.TYPE_CATEGORY:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_category, parent, false);
                    vh = new ViewHolder(v, MyData.TYPE_CATEGORY);
                    return vh;
                default:
                    return null;
            }
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MyData m = getItem(position);
            if (m != null) {
                switch (m.getType()) {
                    case MyData.TYPE_CATEGORY:
                        holder.date.setText(m.getName());
                        break;
                    case MyData.TYPE_RECIPIENT:
                        holder.msg.setText(((Message) m).getMessage());
                        holder.time.setText(((Message) m).getTime());
                        break;
                    case MyData.TYPE_SENDER: {
                        holder.msg.setText(((Message) m).getMessage());
                        if (((Message) m).getDate() == null) {
                            holder.progressBar.setVisibility(View.VISIBLE);
                            holder.time.setText("");
                        } else {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.time.setText(((Message) m).getTime());
                        }
                        break;
                    }
                    default:
                        Log.e(TAG, "OnBindViewHolder NULL EXCEPTION");
                        break;
                }
            }


        }

        @Override
        public int getItemViewType(int position) {
            MyData m = getItem(position);
            if (m != null)
                return m.getType();
            else {
                Log.e(TAG, "getItemViewType NULL EXCEPTION");
                return -1;
            }
        }

        @Override
        public int getItemCount() {
            int count = 0;
            for (Category c : mCategory) {
                if (c.getExist() || c.getName().equals("current"))
                    count += c.getListSize();

            }
            return count;
        }

        //NOTE: first item in category is automatic this category self.
        private void insertItem(Message msg) {
            Log.i(TAG, "insert Item " + msg.getMessage());
            String day = msg.getDay();
            String time = msg.getTimeToSecond();
            //insert category
            if (mCategory.size() == 0) {
                // category=null
                mCategory.add(new Category(day, MyData.TYPE_CHAT));
            } else {
                //category !=null
                //this day is the last day
                if (day.compareToIgnoreCase(mCategory.get(mCategory.size() - 1).getName()) > 0) {
                    mCategory.add(new Category(day, MyData.TYPE_CHAT));
                } else {
                    //this day is not the last day
                    for (int i = 0; i < mCategory.size(); i++) {
                        //day is found
                        if (mCategory.get(i).getName().equals(day)) {
                            break;
                        } else if (day.compareToIgnoreCase(mCategory.get(i).getName()) < 0) {
                            mCategory.add(i, new Category(day, MyData.TYPE_CHAT));
                            break;
                        }

                    }
                }

            }
            //catch the day
            Category ca = new Category("-1");
            for (Category c : mCategory)
                if (c.getName().equals(day)) {
                    ca = c;
                    break;
                }
            //insert item
            //last time
            if (!ca.getExist() || time.compareToIgnoreCase(((Message) ca.getItem(ca.getListSize() - 1)).getTimeToSecond()) >= 0) {
                ca.addMsg(msg);
            } else {
                for (int i = 1; i < ca.getListSize(); i++) {
                    if (time.compareToIgnoreCase(((Message) ca.getItem(i)).getTimeToSecond()) <= 0) {
                        ca.addMsg(i, msg);
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }

        private void addItem(Message msg) {
            if (mCategory.size() == 0)
                mCategory.add(new Category("current"));
            mCategory.get(mCategory.size() - 1).addMsg(msg);
            Log.i(TAG, "new msg has added");
            notifyItemInserted(getItemCount() - 1);
            notifyItemRangeChanged(0, getItemCount());
            scrollToLastPosition();
        }

        private void msgsended(String msg, String date) {
            for (Category c : mCategory)
                for (int i = 0; i < c.getListSize(); i++)
                    if (c.getItem(i).getType() == MyData.TYPE_SENDER && ((Message) c.getItem(i)).getMessage().equals(msg) && ((Message) c.getItem(i)).getDate() == null) {
                        ((Message) c.getItem(i)).setDate(date);
                        Log.i(TAG, "item " + c.getItem(i).getName() + "'date has been updated with date: " + date);
                        notifyDataSetChanged();
                        return;
                    }
            Log.e(TAG, "msg not found." + " msg: " + msg + " date: " + date);
        }


        private MyData getItem(int position) {
            int p = position;
            for (Category c : mCategory) {
                if (p == 0)
                    return c.getItem(0);
                else if (p - c.getListSize() >= 0)
                    p -= c.getListSize();
                else {
                    for (int i = 0; i < c.getListSize(); i++) {
                        if (p == 0)
                            return c.getItem(i);
                        else p--;
                    }
                }
            }
            Log.e(TAG, "no such a Item was found.");
            return null;
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView msg;
            TextView time;
            ProgressBar progressBar;
            TextView date;

            ViewHolder(View itemView, int type) {
                super(itemView);
                switch (type) {
                    case MyData.TYPE_SENDER:
                        msg = (TextView) itemView.findViewById(R.id.conversation_sender_msg);
                        time = (TextView) itemView.findViewById(R.id.conversation_sender_time);
                        progressBar = (ProgressBar) itemView.findViewById(R.id.conversation_sender_pbar);
                        break;
                    case MyData.TYPE_RECIPIENT:
                        msg = (TextView) itemView.findViewById(R.id.conversation_recipient_msg);
                        time = (TextView) itemView.findViewById(R.id.conversation_recipient_time);
                        break;
                    case MyData.TYPE_CATEGORY:
                        date = (TextView) itemView.findViewById(R.id.conversation_category);
                        break;

                }
            }
        }

    }


    private class GcmBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Receiver captures the broadcasting");
            int size = intent.getIntExtra(getString(R.string.size), -1);
            if (size != -1) {
                for (int i = 0; i < size; i++) {
                    String[] msg = intent.getStringArrayExtra("m" + i);
                    getAdapter().addItem(new Message(msg[0], msg[1], msg[2], Integer.parseInt(msg[3]) == MyData.TYPE_SENDER));
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString("added", "true");
            setResultExtras(bundle);
        }
    }

    private class insertMsgs implements Runnable {
        JSONObject jo;

        insertMsgs(JSONObject jo) {
            this.jo = jo;
        }

        @Override
        public void run() {
            try {
                JSONArray msgslist = jo.getJSONArray("Data");
                for (int i = 0; i < msgslist.length(); i++) {
                    ContentValues v = EncapsulationHelper.decapsulate(msgslist.getJSONObject(i));
                    String fn = v.getAsString("Friendname");
                    String ms = v.getAsString("Msg");
                    String dt = v.getAsString("DateTime");
                    int tp = v.getAsInteger("Type");
                    System.out.println("MSG: " + ms + " FN: " + fn + " date: " + dt + " type: " + tp);
                    final Message msg = new Message(fn, dt, ms, tp == MyData.TYPE_SENDER);
                    if (MyApplication.getAccount().insertMsg(fn, ms, dt, tp)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getAdapter().insertItem(msg);
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Conversation.this.findViewById(R.id.conversation_syn_progressbar).setVisibility(View.GONE);
                }
            });
        }
    }
}


