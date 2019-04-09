package de.paluno.mse.palaver.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import de.paluno.mse.palaver.MyApplication;

/**
 * Created by asus on 2017/6/15.
 */

public class TokenService extends IntentService {
    public TokenService() {
        super("TokenService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance((this));
            String token = instanceID.getToken("594324547505", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            ((MyApplication) getApplication()).sendTokenToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
