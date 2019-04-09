package de.paluno.mse.palaver.generalhelper;

/**
 * Created by asus on 2017/6/17.
 */

public class ButtonUtils {
    private static long lastClickTime = 0;
    private static long DIFF = 1000;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastClickTime > 0 && timeD < DIFF) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    public static boolean isFastDoubleClick(long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastClickTime > 0 && timeD < diff) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }
}
