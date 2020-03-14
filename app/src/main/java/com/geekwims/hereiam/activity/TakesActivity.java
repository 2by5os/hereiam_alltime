package com.geekwims.hereiam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.response.course.CourseInfo;
import com.geekwims.hereiam.widget.TakesListAdapter;

import java.util.List;


public class TakesActivity extends AppCompatActivity {
    private static final String TAG = "TakesActivity";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("불러오는 중...");
        progressDialog.show();

        new GetTakesTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // first parameter is progress dialog
    private class GetTakesTask extends AsyncTask<Object, Void, List<CourseInfo>> {
        @Override
        protected List<CourseInfo> doInBackground(Object... objects) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ApiService apiService = ApiService.getInstance();

            return apiService.getTakes();
        }

        @Override
        protected void onPostExecute(List<CourseInfo> courseInfos) {

            if (courseInfos == null) {
                loadFail();
                return;
            }

            loadSuccess(courseInfos);
        }
    }

    private void loadFail() {
        progressDialog.dismiss();
        finish();
    }
    private void loadSuccess(List<CourseInfo> courseInfos) {
        ListView listView;
        TakesListAdapter takesListAdapter = new TakesListAdapter(courseInfos);

        listView = (ListView) findViewById(R.id.takes_list);
        listView.setAdapter(takesListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseInfo courseInfo = (CourseInfo) parent.getItemAtPosition(position);
                openAttenHistory(courseInfo);
            }
        });

        progressDialog.dismiss();
    }

    private void openAttenHistory(CourseInfo courseInfo) {
        Intent intent = new Intent(this, AttendHistoryActivity.class);
        intent.putExtra("courseInfo", courseInfo);
        startActivity(intent);
    }
}
