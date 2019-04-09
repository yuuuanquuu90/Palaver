package de.paluno.mse.palaver.activitymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import de.paluno.mse.palaver.gcm.TokenService;
import de.paluno.mse.palaver.generalhelper.ConnectionHelper;
import de.paluno.mse.palaver.informationexchanger.Commando;
import de.paluno.mse.palaver.informationexchanger.Connection;
import de.paluno.mse.palaver.generalhelper.EncapsulationHelper;
import de.paluno.mse.palaver.informationexchanger.IReceive;
import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.R;

public class Login extends AppCompatActivity implements IReceive {
    private String username;
    private String password;
    private ProgressBar pb;
    private View showview;
    private EditText passwordtext;
    private EditText usernametext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passwordtext = (EditText) findViewById(R.id.login_password);
        usernametext = (EditText) findViewById(R.id.login_username);
        pb = (ProgressBar) findViewById(R.id.login_progressBar);
        showview = findViewById(R.id.login_showview);
        //login confirm
        findViewById(R.id.login_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reset error field.
                usernametext.setError(null);
                passwordtext.setError(null);

                username = usernametext.getText().toString();
                password = passwordtext.getText().toString();
                try {
                    DoLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        // sign up
        findViewById(R.id.login_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });


    }

    /*
    jo beinhält:
    "MsgType": 1/0
    ...
    "Commando": VALIDATE

     */
    @Override
    public void afterReceivefromServer(JSONObject jo) {
        try {
            //valid user
            if (MyApplication.replySuccess(jo)) {
                //save UserInfo in config.
                MyApplication.getAccount().saveLoginState(this, this.username, this.password, true);
                //Register user in database
                MyApplication.getAccount().registerUser();
                //start service
                Intent msgIntent = new Intent(this, TokenService.class);
                startService(msgIntent);
                //Log in
                Intent intent = new Intent(Login.this, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                //view switch
                pb.setVisibility(View.GONE);
                showview.setVisibility(View.VISIBLE);
                //set errors
                String error = jo.getString("Info");
                switch (error) {
                    case "Benutzer existiert nicht":
                        usernametext.setError(getString(R.string.error_user_not_exist));
                        break;
                    case "Passwort nicht korrekt":
                        passwordtext.setError(getString(R.string.error_incorrect_password));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
    Diese Methode wird aufgerufen, wenn der Benutzer validiert wurde.
     */
    private void DoLogin() {
        //view switch
        pb.setVisibility(View.VISIBLE);
        showview.setVisibility(View.GONE);
//        new Connection(this).execute(EncapsulationHelper.validate(username, password), Commando.VALIDATE.getPath(), Commando.VALIDATE.getCommando());
        MyApplication.getConnection().login(this, EncapsulationHelper.validate(username, password));

    }


}
