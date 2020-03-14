package com.geekwims.hereiam.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.response.schedule.ClassSchedule;
import com.geekwims.hereiam.widget.FontText;

import java.util.Locale;

import butterknife.ButterKnife;


public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        final ProgressDialog progressDialog = new ProgressDialog(ScheduleActivity.this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.show();

        new LoadSchedule().execute(progressDialog);
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

    // param[0] progressDialog
    private class LoadSchedule extends AsyncTask<Object, Void, ClassSchedule> {
        private ProgressDialog progressDialog;

        @Override
        protected ClassSchedule doInBackground(Object... objects) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            progressDialog = (ProgressDialog) objects[0];

            ApiService apiService = ApiService.getInstance();

            return apiService.getTimeTable();
        }

        @Override
        protected void onPostExecute(ClassSchedule classSchedule) {

            if (classSchedule == null) {
                finish();
            } else {
                drawSchedule(classSchedule);
                progressDialog.dismiss();
            }
        }
    }

    private void drawSchedule(ClassSchedule classSchedule) {
        LinearLayout scheduleTable = (LinearLayout) findViewById(R.id.table_schedule);

        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams cellParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1);

        String[][] timeTable = classSchedule.getSchedule();

        final int timeEnd = 24;

        for (int time = 0; time < timeEnd; time++) {
            LinearLayout newRow = new LinearLayout(this);
            newRow.setOrientation(LinearLayout.HORIZONTAL);

            // create time name
            FontText timeName = new FontText(this);
            timeName.setMaxEms(0);
            timeName.setText(String.format(Locale.KOREA, "%d", time + 1));
            timeName.setGravity(Gravity.CENTER);
            if (time == 8)
                timeName.setBackground(ContextCompat.getDrawable(this, R.drawable.border_no_right));
            else
                timeName.setBackground(ContextCompat.getDrawable(this, R.drawable.border_left_top));
            newRow.addView(timeName, cellParams);

            for (int day = 0; day < 7; day++) {
                FontText courseCell = new FontText(this);
                courseCell.setMaxEms(0);
                courseCell.setText(timeTable[day][time]);
                courseCell.setTextSize(10);
                courseCell.setGravity(Gravity.CENTER);

                if (day == 6 && time == (timeEnd - 1)) {
                    courseCell.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
                } else if (day == 6) {
                    courseCell.setBackground(ContextCompat.getDrawable(this, R.drawable.border_no_bottom));
                } else if (time == (timeEnd - 1)) {
                    courseCell.setBackground(ContextCompat.getDrawable(this, R.drawable.border_no_right));
                } else {
                    courseCell.setBackground(ContextCompat.getDrawable(this, R.drawable.border_left_top));
                }

                newRow.addView(courseCell, cellParams);
            }
            scheduleTable.addView(newRow, tableRowParams);
        }
    }
}
