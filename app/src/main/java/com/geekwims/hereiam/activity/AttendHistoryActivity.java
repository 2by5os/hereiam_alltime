package com.geekwims.hereiam.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.attendance.AttendHistory;
import com.geekwims.hereiam.response.course.CourseInfo;
import com.geekwims.hereiam.widget.HistoryListAdapter;


public class AttendHistoryActivity extends AppCompatActivity {
    private static final String TAG = "AttendHistoryActivity";
    ProgressDialog progressDialog;
    CourseInfo courseInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        courseInfo = (CourseInfo) getIntent().getSerializableExtra("courseInfo");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(courseInfo.getCourseName() + " - 출결 정보");
        }


        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("불러오는 중...");
        progressDialog.show();
        new GetAttendHistoryTask().execute();
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

    // first parameter is progress dialog
    private class GetAttendHistoryTask extends AsyncTask<Object, Void, AttendHistory> {
        @Override
        protected AttendHistory doInBackground(Object... objects) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ApiService apiService = ApiService.getInstance();

            return apiService.getAttendHistory(courseInfo);
        }

        @Override
        protected void onPostExecute(AttendHistory attendHistory) {

            if (attendHistory == null) {
                loadFail();
                return;
            }

            loadSuccess(attendHistory);
        }
    }

    private void loadFail() {
        progressDialog.dismiss();
        finish();
    }
    private void loadSuccess(AttendHistory attendHistory) {

        ListView listView = (ListView) findViewById(R.id.takes_list);
        HistoryListAdapter historyListAdapter = new HistoryListAdapter(attendHistory);
        listView.setAdapter(historyListAdapter);

        progressDialog.dismiss();
    }
}
