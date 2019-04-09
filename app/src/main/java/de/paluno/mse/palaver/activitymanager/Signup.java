package de.paluno.mse.palaver.activitymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONObject;

import de.paluno.mse.palaver.generalhelper.EncapsulationHelper;
import de.paluno.mse.palaver.informationexchanger.IReceive;
import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.R;

public class Signup extends AppCompatActivity implements IReceive {
    private EditText nickename;
    private EditText password1;
    private EditText password2;
    private View showView;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showView = findViewById(R.id.showview);
        nickename = (EditText) findViewById(R.id.signiup_nickname);
        password1 = (EditText) findViewById(R.id.signup_password1);
        password2 = (EditText) findViewById(R.id.signup_password2);
        findViewById(R.id.login_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInputError()) {
                    pb = (ProgressBar) findViewById(R.id.sinup_progressBar);
                    //change Visibility
                    pb.setVisibility(View.VISIBLE);
                    showView.setVisibility(View.INVISIBLE);
                    //send Message
                    DoSignUp();

                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private boolean checkInputError() {
        boolean error;
        nickename.setError(null);
        password1.setError(null);
        password2.setError(null);
        String checkName = nickename.getText().toString();
        String checkPassword1 = password1.getText().toString();
        String checkPassword2 = password2.getText().toString();
        //Check name:
        if (checkName.equals(""))
            nickename.setError(getString(R.string.error_empty_name));
        else {
            for (int i = 0; i < checkName.length(); i++) {
                if (!checkLetters(checkName.charAt(i)))
                    nickename.setError(getString(R.string.error_invalid_nickname));
                break;
            }


        }
        //Check password:
        if (error = checkPassword1.equals("") || checkPassword2.equals("")) {
            password1.setError(getString(R.string.error_empty_password));
            password2.setError(getString(R.string.error_empty_password));
        } else if (error = !checkPassword1.equals(checkPassword2)) {
            password1.setError(getString(R.string.error_password_inconsistent));
            password2.setError(getString(R.string.error_password_inconsistent));
        } else if (error = checkPassword1.length() <= 3) {
            password1.setError(getString(R.string.error_invalid_password));
            password2.setError(getString(R.string.error_invalid_password));
        }
        return error;
    }

    private boolean checkLetters(char letter) {
        return letter >= '0' && letter <= '9' || letter >= 'a' && letter <= 'z' || letter >= 'A' && letter <= 'Z';
    }

    @Override
    public void afterReceivefromServer(JSONObject jo) {
        if (MyApplication.replySuccess(jo)) {
//            startActivity(new Intent(Signup.this, Login.class));
            finish();
        } else {
            //change Visibility
            showView.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            //report Error
            nickename.setError(getString(R.string.error_user_exist));
        }
    }


    private void DoSignUp() {
//        new Connection(this).execute(EncapsulationHelper.register(nickename.getText().toString(), password1.getText().toString()), Commando.REGISTER.getPath(), Commando.REGISTER.getCommando());
        MyApplication.getConnection().signup(this,EncapsulationHelper.register(nickename.getText().toString(), password1.getText().toString()));
    }
}
