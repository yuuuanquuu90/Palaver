package de.paluno.mse.palaver.appdata;

import android.content.Context;

/**
 * Created by asus on 2017/5/31.
 */

public interface MySharedPreferences {
    String getPassword();

    void changePassword(String newpassword);

    void saveLoginState(Context context, String username, String password, boolean state);

    void Signout(Context context);

    boolean checkLoginState(Context context);

    String getCurrentUsername();

    String getCommando();


}
