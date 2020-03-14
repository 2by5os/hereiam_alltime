package com.geekwims.hereiam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.response.course.NextCourseInfo;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class NextCourseInfoActivity extends AppCompatActivity {
    TextView mStatusMsgTextView;
    Date courseTime;
    Timer countDownTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_course_info);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        NextCourseInfo nextCourseInfo = (NextCourseInfo) intent.getSerializableExtra("nextCourseInfo");

        Log.d("nextCourseInfo", nextCourseInfo.toString());

        mStatusMsgTextView = (TextView) findViewById(R.id.next_course_count);

        TextView nextCourseName = (TextView) findViewById(R.id.next_course_name);
        TextView nextCourseRoom = (TextView) findViewById(R.id.next_course_room);

        nextCourseName.setText(nextCourseInfo.getCourseInfo().getCourseName());
        nextCourseRoom.setText(nextCourseInfo.getCourseInfo().getRoom());

        Calendar courseSchedule = Calendar.getInstance(Locale.KOREA);

        // calender starts with 1 (ex. sunday is 1 and monday is 2 so on)
        courseSchedule.set(Calendar.DAY_OF_WEEK, nextCourseInfo.getCourseInfo().getDay() + 1);
        // course time starts with 1 and it mean nine a clock
        courseSchedule.set(Calendar.HOUR_OF_DAY, nextCourseInfo.getCourseInfo().getTime());
        courseSchedule.set(Calendar.MINUTE, 0);
        courseSchedule.set(Calendar.SECOND, 0);
        courseTime = courseSchedule.getTime();

        NextCourseTimerTask nextCourseTimerTask = new NextCourseTimerTask();
        countDownTimer.schedule(nextCourseTimerTask, 500, 1000);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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
        // automatically handle clicks on the Home/Up button, so long주
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        countDownTimer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (courseTime != null) {
            NextCourseTimerTask nextCourseTimerTask = new NextCourseTimerTask();
            countDownTimer.schedule(nextCourseTimerTask, 500, 1000);
        }
        super.onResume();
    }

    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Date now = new Date();

            long leftTime = (courseTime.getTime() - now.getTime()) / 1000;

            if (leftTime <= 0) {
                countDownTimer.cancel();
            }

            long sec = leftTime % 60;
            long min = (leftTime / 60) % 60;
            long hour = (leftTime / 60 / 60) % 24;
            long day = (leftTime / 60 / 60 / 24);

            String output = String.format(Locale.US, "남은 시간 : %02d일 %02d시간 %02d분 %02d초", day, hour, min, sec);

            mStatusMsgTextView.setText(output);
        }
    };

    private class NextCourseTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(mUpdateTimeTask);
        }
    }
}
