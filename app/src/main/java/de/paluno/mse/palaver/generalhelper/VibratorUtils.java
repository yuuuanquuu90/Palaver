package de.paluno.mse.palaver.generalhelper;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * Created by asus on 2017/6/27.
 */

public class VibratorUtils {
    private static boolean viration;

    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    public static boolean checkVibration() {
        return viration;
    }
}
