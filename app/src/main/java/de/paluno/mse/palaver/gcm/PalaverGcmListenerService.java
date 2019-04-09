package de.paluno.mse.palaver.gcm;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import de.paluno.mse.palaver.R;

/**
 * Created by asus on 2017/6/15.
 */

public class PalaverGcmListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //TODO NOTIFICATION:

        Intent i = new Intent(getString(R.string.GCM));
        i.putExtra("sender", data.getString("sender"));
        sendBroadcast(i);
    }
}
