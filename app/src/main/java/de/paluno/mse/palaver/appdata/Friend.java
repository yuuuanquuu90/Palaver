package de.paluno.mse.palaver.appdata;

/**
 * Created by asus on 2017/5/27.
 */

public class Friend extends MyData {
    private String tag;

    public Friend(String name) {
        super(name, MyData.TYPE_FRIEND);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
