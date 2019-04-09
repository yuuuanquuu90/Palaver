package de.paluno.mse.palaver.activitymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.paluno.mse.palaver.generalhelper.ConnectionHelper;
import de.paluno.mse.palaver.generalhelper.EncapsulationHelper;
import de.paluno.mse.palaver.informationexchanger.IReceive;
import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.R;

public class AddFriends extends AppCompatActivity implements IReceive {
    private static final String TAG = "MYPALAVER_ADD";
    private static String FRIENDNAME;
    EditText searchtext;
    private static final int FRIENDADDED = 0;
    private static final int FRIENDEXISTS = 1;
    private static final int BACK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        searchtext = (EditText) findViewById(R.id.add_friends_searchfriend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((EditText) findViewById(R.id.add_friends_searchfriend)).addTextChangedListener(getMyTextWatcher());
        findViewById(R.id.add_friends_ok).setOnClickListener(getMyOnClickListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addfriends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addfriends_app_bar_search:
                myDone();
                return true;
            case android.R.id.home:
                setResultToParent(BACK);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setResultToParent(BACK);
    }

    @Override
    public void afterReceivefromServer(JSONObject jo) {
        searchtext.setText("");
        if (MyApplication.replySuccess(jo)) {
            MyApplication.getAccount().insertFriend(FRIENDNAME);
            setResultToParent(FRIENDADDED);
        } else try {
            if (jo.get("Info").equals("Freund bereits auf der Liste")) {
                Log.i(TAG, "friend exists");
                setResultToParent(FRIENDEXISTS);
            } else {
                setVisibility(false);
                Toast.makeText(this, "no such a friend was found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void myDone() {
        searchtext.setError(null);
        FRIENDNAME = searchtext.getText().toString();
        //all user of others platform which name with special symbols, will be filtered.
        //...
        if (FRIENDNAME.equals("")) {
            searchtext.setError(getString(R.string.error_empty_friendname));
        } else if (FRIENDNAME.equals(MyApplication.getAccount().getCurrentUsername()))
            searchtext.setError("this is you.");
        else if (checkError(FRIENDNAME))
            searchtext.setError(getString(R.string.error_invalid_nickname));
        else
            DoAddFriend(FRIENDNAME);
    }

    private boolean checkError(String friendname) {
        for (int i = 0; i < friendname.length(); i++) {
            char check = friendname.charAt(i);
            if ((check < '0') || (check > '9' && check < 'A') || (check > 'Z' && check < 'a') || (check > 'z'))
                return true;
        }
        return false;
    }

    private void setResultToParent(int msg) {
        Intent intent = new Intent();
        switch (msg) {
            case FRIENDADDED:
                intent.putExtra("name", FRIENDNAME);
                break;
            case FRIENDEXISTS:
                intent.putExtra("exists", FRIENDNAME);
                break;
            case BACK:
                Log.i(TAG, "user back to parent activity");
                break;
            default:
                Log.e(TAG, "msg unspecified");
                break;
        }
        setResult(MainMenu.ADDFRIEND, intent);
        finish();
    }

    private void DoAddFriend(String friend) {
        setVisibility(true);
//        new Connection(this).execute(EncapsulationHelper.addFriends(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), friend), Commando.ADDFRIENDS.getPath(), Commando.ADDFRIENDS.getCommando());
        MyApplication.getConnection().addfriend(this, EncapsulationHelper.addFriends(MyApplication.getAccount().getCurrentUsername(), MyApplication.getAccount().getPassword(), friend));
    }

    private void setVisibility(boolean wait) {
        if (wait) {
            findViewById(R.id.add_friends_progressbar).setVisibility(View.VISIBLE);
            findViewById(R.id.add_friends_searchfriend).setVisibility(View.GONE);
            findViewById(R.id.add_friends_ok).setVisibility(View.GONE);
        } else {
            findViewById(R.id.add_friends_progressbar).setVisibility(View.GONE);
            findViewById(R.id.add_friends_searchfriend).setVisibility(View.VISIBLE);
            findViewById(R.id.add_friends_ok).setVisibility(View.VISIBLE);
        }
    }

    //**********Listener*********************
    private TextWatcher getMyTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((TextView) findViewById(R.id.add_friends_friendtextview)).setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private View.OnClickListener getMyOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDone();
            }
        };
    }
}