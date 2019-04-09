package de.paluno.mse.palaver.activitymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.paluno.mse.palaver.appdata.Account;
import de.paluno.mse.palaver.gcm.TokenService;
import de.paluno.mse.palaver.generalhelper.EncapsulationHelper;
import de.paluno.mse.palaver.informationexchanger.IReceive;
import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.R;

public class SplashScreen extends AppCompatActivity implements IReceive {
    private final static boolean AUTOLOGIN = true;
    private final static String TAG = "MYPALAVER_MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Account.setAccount(this);
        checkLoginState();

    }


    private void checkLoginState() {
        //Check LoginState
        if (MyApplication.getAccount().checkLoginState(this)) {
            //Validated User -->automatically validate
            Log.i(TAG, "validated User -->automatically login");
            AutoLogin();
        } else
        //New User --> manually validate
        {
            Log.i(TAG, "New User --> manually validate");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreen.this, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void AutoLogin() {
//        new Connection(this).execute(EncapsulationHelper.validate(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword()), Commando.VALIDATE.getPath(), Commando.VALIDATE.getCommando());
        MyApplication.getConnection().login(this, EncapsulationHelper.validate(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword()));
    }


    @Override
    public void afterReceivefromServer(JSONObject jo) {
        //if no connection
        try {
            if (!jo.getBoolean("connected"))
                startActivity(new Intent(SplashScreen.this, Login.class));
                //valid user
            else if (MyApplication.replySuccess(jo)) {
                //start service
                Intent msgIntent = new Intent(this, TokenService.class);
                startService(msgIntent);
                //Log in
                Intent intent = new Intent(SplashScreen.this, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                //set errors
                Toast.makeText(this, "password is out of data, please enter your password manually.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
