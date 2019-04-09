package de.paluno.mse.palaver.appdata;

/**
 * Created by asus on 2017/5/27.
 */

public class MyData {
    private String name;
    private String id;
    private int type;
    public static final int TYPE_FRIEND = 0;
    public static final int TYPE_CHAT = 2;
    public static final int TYPE_SENDER = 0;
    public static final int TYPE_RECIPIENT = 1;
    public static final int TYPE_CATEGORY = 2;

    public MyData(String name) {
        this.name = name;
    }

    public MyData(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public MyData(String name, String id, int type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    protected void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }
}
