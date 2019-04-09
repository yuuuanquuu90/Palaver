package de.paluno.mse.palaver.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by asus on 2017/6/15.
 */

public class PalaverInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent msgIntent = new Intent(this, TokenService.class);
        startService(msgIntent);
    }
}
