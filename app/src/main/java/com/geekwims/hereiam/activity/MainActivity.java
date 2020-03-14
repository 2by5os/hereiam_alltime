package com.geekwims.hereiam.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.api.UserInfo;
import com.geekwims.hereiam.autocheck.AutoCheckManager;
import com.geekwims.hereiam.constant.Font;
import com.geekwims.hereiam.response.course.NextCourseInfo;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.geekwims.hereiam.activity.LoginActivity.PREF_KEY_LOGIN_ID;
import static com.geekwims.hereiam.activity.LoginActivity.PREF_KEY_LOGIN_IS_KEEP;
import static com.geekwims.hereiam.activity.LoginActivity.PREF_KEY_LOGIN_PW;
import static com.geekwims.hereiam.activity.LoginActivity.PREF_NAME_LOGIN_INFO;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("notice");
        Log.d(TAG, "TOKEN : " + FirebaseInstanceId.getInstance().getToken());


        Font.getTypeface(getApplicationContext());

        if (!UserInfo.getInstance().isLogined()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        LinearLayout layoutAttend = (LinearLayout) findViewById(R.id.layout_attend);
        LinearLayout layoutSchedule = (LinearLayout) findViewById(R.id.layout_schedule);
        LinearLayout layoutHistory = (LinearLayout) findViewById(R.id.layout_history);
        LinearLayout layoutBoard = (LinearLayout) findViewById(R.id.btn_board_start);
        LinearLayout layoutSetting = (LinearLayout) findViewById(R.id.btn_setting_start);


        layoutAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAttentanceActivity();
            }
        });

        layoutSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schedule();
            }
        });

        final Context context = this;

        layoutHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TakesActivity.class);
                startActivity(intent);
            }
        });

        layoutBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBoardListeActivity();
            }
        });

        layoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSetting();
            }
        });

        progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);


    }

    private void openSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_logout:
                logout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME_LOGIN_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (prefs.getBoolean(PREF_KEY_LOGIN_IS_KEEP, false)) {
            editor.putString(PREF_KEY_LOGIN_ID, "");
            editor.putString(PREF_KEY_LOGIN_PW, "");
            editor.putBoolean(PREF_KEY_LOGIN_IS_KEEP, false);
        }

        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void schedule() {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void openAttentanceActivity() {
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.show();

        new AttendanceInfoTask().execute();
    }

    public void openBoardListeActivity() {
        Intent intent = new Intent(this, BoardListActivity.class);
        startActivity(intent);
    }

    public void tryOpenTakesActivity() {
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.show();

        new AttendanceInfoTask().execute();
    }

    // first parameter is progress dialog
    private class AttendanceInfoTask extends AsyncTask<Object, Void, NextCourseInfo> {
        @Override
        protected NextCourseInfo doInBackground(Object... objects) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ApiService apiService = ApiService.getInstance();

            return apiService.getNextCourseInfo();
        }

        @Override
        protected void onPostExecute(NextCourseInfo nextCourseInfo) {

            if (nextCourseInfo == null) {
                initializeFail();
                return;
            }

            initializeSuccess(nextCourseInfo);
        }
    }

    private void initializeSuccess(NextCourseInfo nextCourseInfo) {
        Intent intent;

        progressDialog.dismiss();


        switch (nextCourseInfo.getStatus()) {
            case CUR:
                intent = new Intent(this, AttendanceActivity.class);
                intent.putExtra("curCourseInfo", nextCourseInfo);
                Log.e(TAG, nextCourseInfo.toString());
                startActivity(intent);
                break;
            case NEXT:
                intent = new Intent(this, NextCourseInfoActivity.class);
                intent.putExtra("nextCourseInfo", nextCourseInfo);
                startActivity(intent);
                break;
            case NONE:
                Toast.makeText(getApplicationContext(), "이번 주는 더 이상 수강정보가 없습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void initializeFail() {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Fail to load course data", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "************** main activity is destroyed    ****************");

    }
}
