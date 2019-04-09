package de.paluno.mse.palaver;

import android.app.Application;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import de.paluno.mse.palaver.appdata.Account;
import de.paluno.mse.palaver.generalhelper.ConnectionHelper;
import de.paluno.mse.palaver.informationexchanger.Commando;
import de.paluno.mse.palaver.informationexchanger.Connection;
import de.paluno.mse.palaver.generalhelper.EncapsulationHelper;

/**
 * Created by asus on 2017/6/4.
 */

public class MyApplication extends Application {
    private static Account account;
    private static Context context;
    private static ConnectionHelper connectionHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        account = new Account(this);
        context = this;
        connectionHelper = new ConnectionHelper();
    }

    public static Context getContext() {
        return context;
    }

    public void sendTokenToServer(String token) {
        new Connection(null).execute(EncapsulationHelper.pushtoken(getAccount().getCurrentUsername(), getAccount().getPassword(), token), Commando.PUSHTOKEN.getPath(), Commando.PUSHTOKEN.getCommando());

    }

    public static Account getAccount() {
        return account;
    }

    public static ConnectionHelper getConnection() {
        return connectionHelper;
    }

    public static boolean replySuccess(JSONObject jo) {
        try {
            if (jo.getInt("MsgType") == 1)
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }
}
