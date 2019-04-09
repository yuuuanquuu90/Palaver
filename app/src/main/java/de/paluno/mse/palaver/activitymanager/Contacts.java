package de.paluno.mse.palaver.activitymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.paluno.mse.palaver.appdata.Category;
import de.paluno.mse.palaver.appdata.MyData;
import de.paluno.mse.palaver.generalhelper.CharacterHelper;
import de.paluno.mse.palaver.generalhelper.ConnectionHelper;
import de.paluno.mse.palaver.generalhelper.EncapsulationHelper;
import de.paluno.mse.palaver.informationexchanger.IReceive;
import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.R;

/**
 * Created by yuan on 2017/5/24.
 */

public class Contacts extends Fragment implements IReceive {
    private final static String TAG = "MYPALAVER_CONTACS";
    private MyAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        initRecyclerView(rootView);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void insertIteminView(String name) {
        mAdapter.insertItem(name);
    }

    public void removeIteminView(String name) {
        mAdapter.deleteIteminView(name);
    }

    private void initRecyclerView(View view) {
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.contacts_listview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = mAdapter.getOnLongClickPosition();
        switch (item.getItemId()) {
            case R.id.contacts_delete:
                mAdapter.deleteItem(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void deleteIteminDataBase(String friendname) {
        MyApplication.getAccount().deleteFriend(friendname);
    }

    private void deleteIteminServer(String friendname) {
//        new Connection(this).execute(EncapsulationHelper.removeFriends(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), friendname), Commando.REMOVEFRIENDS.getPath(), Commando.REMOVEFRIENDS.getCommando(), friendname);
        MyApplication.getConnection().removefriend(this, EncapsulationHelper.removeFriends(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), friendname), new String[]{friendname});
    }

    @Override
    public void afterReceivefromServer(JSONObject jo) {
        if (MyApplication.replySuccess(jo)) {
            try { //delete in datebase
                String friendname = jo.getString(getString(R.string.FRIENDNAME));
                deleteIteminDataBase(friendname);
                mAdapter.deleteIteminView(friendname);
                Intent intent = new Intent(getString(R.string.DELETE_BROADCASTING));
                intent.putExtra(getString(R.string.FRIENDNAME), friendname);
                intent.putExtra(getString(R.string.FLAG_DELETE_FRIEND), true);
                getActivity().sendBroadcast(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Category> mCategory;
        private int onLongClickPosition;

        MyAdapter() {
            mCategory = new ArrayList<>();
            //create Category A to Z.
            for (int i = 'A'; i <= 'Z'; i++)
                mCategory.add(new Category(String.valueOf((char) i)));
            //create special characters
            mCategory.add(new Category("#"));
            //add items(friends)
            ArrayList<String> friends = MyApplication.getAccount().getFriendsList("asc");
            if (friends != null) {
                for (int i = 0; i < friends.size(); i++)
                    insertItem(friends.get(i));
                Log.i(TAG, "contact is created. " + friends.size() + " friends have been created.");
            } else {
                Log.w(TAG, "friend table from current user is null");
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            ViewHolder vh;
            switch (viewType) {
                case MyData.TYPE_CATEGORY:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_category, parent, false);
                    vh = new ViewHolder(v, MyData.TYPE_CATEGORY);
                    return vh;
                case MyData.TYPE_FRIEND:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_friend_info, parent, false);
                    vh = new ViewHolder(v, MyData.TYPE_FRIEND);
                    return vh;
                default:
                    Log.e(TAG, "oncreateviewholder error" + " viewtype=: " + viewType);
                    return null;

            }
        }


        public void onBindViewHolder(ViewHolder holder, int position) {
            MyData m = getItem(position);
            final int p = position;
            if (m != null) {
                switch (m.getType()) {
                    case MyData.TYPE_CATEGORY:
                        holder.textView.setText(m.getName());
                        break;
                    case MyData.TYPE_FRIEND:
                        holder.textView.setText(m.getName());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String friendname = ((TextView) v.findViewById(R.id.contacts_friend_info)).getText().toString();
                                ((Chats) ((MainMenu) getActivity()).getFragment(R.layout.fragment_chats)).startChat(friendname);
                            }
                        });
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                setOnLongClickPosition(p);
                                return false;
                            }
                        });
                        break;
                    default:
                        Log.e(TAG, "ERROR ON ONBINDVIEWHOLDER");
                        break;
                }
            } else Log.e(TAG, "on BindViewHolder NULL EXCEPTION");
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
                if (c.getExist())
                    count += c.getListSize();
            }
            return count;
        }

        void insertItem(String name) {
            Category c;
            if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {

                c = mCategory.get(mCategory.size() - 1);
                if (!c.getExist()) {
                    Log.i(TAG, "new number item inserted" + name);
                    //add category
                    c.addCategory(c.getName());
                    notifyDataSetChanged();
                    //add item
                    c.addFriend(name);
                    notifyDataSetChanged();

                } else {
                    //NOTE: first item is category!!!
                    if (name.compareToIgnoreCase(c.getItem(c.getListSize() - 1).getName()) > 0) {
                        c.addFriend(name);
                        notifyDataSetChanged();
                        Log.i(TAG, "insert number item last one in queue");
                        return;
                    }
                    for (int i = 1; i < c.getListSize(); i++) {
                        if (name.compareToIgnoreCase(c.getItem(i).getName()) <= 0) {
                            c.addFriend(i, name);
                            notifyDataSetChanged();
                            Log.i(TAG, "insert number item " + name);
                            return;
                        }
                    }
                }
            } else {
                String first = String.valueOf(CharacterHelper.upperLetters(name.charAt(0)));
                for (Category ca : mCategory) {
                    //find the category:
                    if (ca.getName().equals(first)) {
                        if (!ca.getExist()) {
                            ca.addCategory(ca.getName());
                            notifyDataSetChanged();
                            ca.addFriend(name);
                            notifyDataSetChanged();
                            Log.i(TAG, "insert Item and Category " + name);
                        } else {
                            if (name.compareToIgnoreCase(ca.getItem(ca.getListSize() - 1).getName()) > 0) {
                                ca.addFriend(name);
                                Log.i(TAG, "insert Item last one in queue " + name);
                                notifyDataSetChanged();
                                return;
                            }
                            //NOTE: i=0 --> category
                            for (int i = 1; i < ca.getListSize(); i++) {
                                if (name.compareToIgnoreCase(ca.getItem(i).getName()) < 0) {
                                    ca.addFriend(i, name);
                                    notifyDataSetChanged();
                                    Log.i(TAG, "insert Item " + name);
                                    return;
                                }
                            }
                        }
                    }
                }

            }
        }


        void deleteItem(int position) {
            MyData f = getItem(position);
            if (f != null && f.getType() == MyData.TYPE_FRIEND) {
                Log.e(TAG, "deleteItem " + f.getName());
                deleteIteminServer(f.getName());
            } else Log.e(TAG, "friend null exception");
        }

        void deleteIteminView(String name) {
            int p = 0;
            String first;
            if (CharacterHelper.number(name.charAt(0)))
                first = "#";
            else first = String.valueOf(CharacterHelper.upperLetters(name.charAt(0)));
            //check number
            Log.i(TAG, "first char is " + first);
            for (Category c : mCategory) {
                if (c.getExist()) {
                    if (!first.equals(c.getName())) {
                        p += c.getListSize();
                    } else {
                        // item in this category
                        //NOTE: first item is category
                        p++;
                        for (int i = 1; i < c.getListSize(); i++) {
                            if (c.getItem(i).getName().equals(name)) {
                                Log.i(TAG, name + " is in position " + p);
                                c.removeItem(i);
                                notifyItemRemoved(p);
                                notifyItemRangeChanged(0, getItemCount());
                                if (!c.getExist()) {
                                    c.removeItem(0);
                                    notifyItemRemoved(0);
                                    notifyItemRangeChanged(0, getItemCount());
                                    Log.i(TAG, "remove category");
                                    return;
                                }
                            } else p++;
                        }
                    }
                }
            }
        }


        private MyData getItem(int position) {
            int p = position;
            for (Category c : mCategory) {
                if (c.getExist()) {
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
            }
            Log.e(TAG, "no such a Item was found.");
            return null;

        }


        private void setOnLongClickPosition(int p) {
            this.onLongClickPosition = p;

        }

        int getOnLongClickPosition() {
            return onLongClickPosition;
        }

        //here below is Class: ViewHolder
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            TextView textView;

            ViewHolder(View itemView, int type) {
                super(itemView);
                if (type == MyData.TYPE_FRIEND)
                    itemView.setOnCreateContextMenuListener(this);
                switch (type) {
                    case MyData.TYPE_CATEGORY:
                        textView = (TextView) itemView.findViewById(R.id.contacts_category);
                        break;
                    case MyData.TYPE_FRIEND:
                        textView = (TextView) itemView.findViewById(R.id.contacts_friend_info);
                        break;
                    default:
                        Log.e(TAG, "ERROR ON VIEWHOLDER");
                }
            }


            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.contact_delete, menu);
            }
        }

    }
}
