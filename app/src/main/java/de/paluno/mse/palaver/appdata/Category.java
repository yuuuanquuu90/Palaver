package de.paluno.mse.palaver.appdata;

import java.util.ArrayList;

/**
 * Created by asus on 2017/5/27.
 */

/**
 */
public class Category extends MyData {
    private ArrayList<MyData> items;

    public Category(String name) {
        super(name, MyData.TYPE_CATEGORY);
        items = new ArrayList<>();
    }

    public Category(String name, int type) {
        super(name, MyData.TYPE_CATEGORY);
        items = new ArrayList<>();
        if (type == TYPE_CHAT)
            addCategory(name);
    }

    public boolean getExist() {
        return getListSize() > 1;
    }


    public int getListSize() {
        return items.size();
    }

    public void addFriend(String name) {
        items.add(new Friend(name));
    }

    public void addFriend(int index, String name) {
        items.add(index, new Friend(name));
    }

    public void addMsg(Message msg) {
        items.add(msg);
    }

    public void addMsg(int index, Message msg) {
        items.add(index, msg);
    }

    public void addCategory(String name) {
        items.add(new Category(name));
    }


    public MyData getItem(int position) {
        return items.get(position);
    }

    public void removeItem(int position) {
        items.remove(position);
    }


}
