package de.paluno.mse.palaver.informationexchanger;

/**
 * Created by asus on 2017/5/15.
 */

public enum Commando {


    REGISTER("REGISTER", "/api/user/register"), VALIDATE("VALIDATE", "/api/user/validate"), PASSWORD("PASSWORD", "/api/user/password"), PUSHTOKEN("PUSHTOKEN", "/api/user/pushtoken"), SEND("SEND", "/api/message/send"), GETMESSEGE("GETMESSEGE", "/api/message/get"), GETOFFSET("GETOFFSET", "/api/message/getoffset"), ADDFRIENDS("ADDFRIENDS", "/api/friends/add"), REMOVEFRIENDS("REMOVEFRIENDS", "/api/friends/remove"), GETFRIENDS("GETFRIENDS", "/api/friends/get");
    private  String commando;
    String path;

    Commando(String commando, String path) {
        this.commando = commando;
        this.path = path;
    }

    public String getCommando() {
        return this.commando;
    }

    public String getPath() {
        return this.path;
    }
}
