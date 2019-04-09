package de.paluno.mse.palaver.informationexchanger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import de.paluno.mse.palaver.MyApplication;

/**
 * Created by asus on 2017/5/17.
 */

public class Connection extends AsyncTask<String, Integer, JSONObject> {
    private String server;
    private IReceive receive;
    private Context context;
    private static final String TAG = "MYPALAVER_CONTECTION";
    private static final int REQUEST = 0;
    private static final int PATH = 1;
    private static final int COMMANDO = 2;
    private static final int RECIPIENT = 3;
    private static final int MESSAGE = 4;
    private static final int FRIENDNAME = 3;
    private static final int NEWPASSWORD = 3;

    public Connection(IReceive receive) {
        this.server = "http://palaver.se.paluno.uni-due.de";
        this.receive = receive;
//        this.context = context;
        this.context = MyApplication.getContext();
    }


    /*
    request[0]= Anfrage. z.B. "{\"Username\":\"" + username + "\",\"Password\":\"" + password + "\"}"
    request[1]=Pfad z.B. "/api/user/register"
    request[2]=Befehl z.B. REGISTER
    request[3]=recipient optional.
    request[4]=msg optional
     */
    @Override
    protected JSONObject doInBackground(String... request) {
        if (checkNetworkConnection()) {
            try {
                URL url = new URL(server + request[PATH]);
                URLConnection uc = url.openConnection();
                uc.setConnectTimeout(15000);
                uc.setReadTimeout(15000);
                Log.i(TAG, "request is " + request[REQUEST]);
                Log.i(TAG, "path is " + url.toString());
                Log.i(TAG, "commando is " + request[COMMANDO]);
                uc.setDoOutput(true);
                uc.setDoInput(true);
                // Content-Type
                uc.setRequestProperty("Content-Type", "application/json");
                // request senden
                PrintWriter out = new PrintWriter(uc.getOutputStream());
                out.write(request[REQUEST]);
                out.flush();
                // Antwort empfangen
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                // Antwort lesen
                StringBuilder string = new StringBuilder();
                String tmp;
                while ((tmp = in.readLine()) != null)
                    string.append(tmp);
                JSONObject jo = new JSONObject(string.toString());
                Log.i(TAG, "reply is: " + jo.toString());
                jo.put("Commando", request[COMMANDO]);
                //
                jo.put("connected", true);
                //for send
                //put extra info
                switch (request[COMMANDO]) {
                    case "SEND":
                        jo.put("recipient", request[RECIPIENT]);
                        jo.put("msg", request[MESSAGE]);
                        break;
                    case "REMOVEFRIENDS":
                        jo.put("friendname", request[FRIENDNAME]);
                        break;
                    case "PASSWORD":
                        jo.put("newpassword", request[NEWPASSWORD]);
                        break;
                }
                return jo;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else {
            JSONObject jo = new JSONObject();
            try {
                jo.put("connected", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jo;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jo) {
        super.onPostExecute(jo);
        try {
            if (receive != null) {
                if (jo != null && jo.getBoolean("connected"))
                    receive.afterReceivefromServer(jo);
                else {
                    Toast.makeText(context, "no network.", Toast.LENGTH_SHORT).show();
                    JSONObject j = new JSONObject();
                    j.put("connected", false);
                    receive.afterReceivefromServer(j);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
