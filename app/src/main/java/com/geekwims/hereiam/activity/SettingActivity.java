package com.geekwims.hereiam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.response.BoardResponse;

import static com.geekwims.hereiam.activity.LoginActivity.PREF_KEY_LOGIN_IS_KEEP;
import static com.geekwims.hereiam.activity.LoginActivity.PREF_NAME_LOGIN_INFO;


public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    private static final String PREF_NOTIFICATINO = "notification";
    private static final String PREF_NOTIFICATINO_ALLOW = "allow";
    ProgressDialog progressDialog;
    boolean allowNotification = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("불러오는 중...");
        //progressDialog.show();

        SharedPreferences prefs = getSharedPreferences(PREF_NOTIFICATINO, MODE_PRIVATE);

        allowNotification = prefs.getBoolean(PREF_NOTIFICATINO_ALLOW, true);

        Button button = (Button) findViewById(R.id.btn_reset_autologin);
        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.allow_toggle);
        switchCompat.setChecked(allowNotification);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAutoLogin(false);
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNotification(isChecked);
            }
        });

        findViewById(R.id.btn_battery_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BatterLogActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNotification(boolean isChecked) {
        SharedPreferences prefs = getSharedPreferences(PREF_NOTIFICATINO, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_NOTIFICATINO_ALLOW, isChecked);
        editor.apply();

        if (isChecked)
            Toast.makeText(this, "알람을 켰습니다.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "알람을 껐습니다.", Toast.LENGTH_SHORT).show();
    }

    private void setAutoLogin(boolean action) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME_LOGIN_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_KEY_LOGIN_IS_KEEP, action);
        editor.apply();

        Toast.makeText(this, "자동 로그인이 해제 되었습니다.", Toast.LENGTH_LONG).show();
    }
}
