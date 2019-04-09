package de.paluno.mse.palaver.informationexchanger;

import org.json.JSONObject;

/**
 * Created by asus on 2017/5/17.
 */

public interface IReceive {
     void afterReceivefromServer(JSONObject jo);

}
