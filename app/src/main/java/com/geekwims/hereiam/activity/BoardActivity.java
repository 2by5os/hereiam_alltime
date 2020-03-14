package com.geekwims.hereiam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.attendance.AttendHistory;
import com.geekwims.hereiam.response.BoardResponse;
import com.geekwims.hereiam.response.course.CourseInfo;
import com.geekwims.hereiam.widget.HistoryListAdapter;


public class BoardActivity extends AppCompatActivity {
    private static final String TAG = "BoardActivity";
    public static final String EXTRA_BOARD_ID = "boardId";
    ProgressDialog progressDialog;
    BoardResponse boardResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_view);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("불러오는 중...");
        progressDialog.show();

        new GetBoardTask().execute(getIntent().getIntExtra(EXTRA_BOARD_ID, 0));
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
    private class GetBoardTask extends AsyncTask<Integer, Void, BoardResponse> {
        @Override
        protected BoardResponse doInBackground(Integer... objects) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ApiService apiService = ApiService.getInstance();

            return apiService.getBoard(objects[0]);
        }

        @Override
        protected void onPostExecute(BoardResponse attendHistory) {

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
    private void loadSuccess(BoardResponse boardResponse) {

//        TextView title = (TextView) findViewById(R.id.board_title);
        TextView content = (TextView) findViewById(R.id.board_content);
        TextView writerName = (TextView) findViewById(R.id.board_writer);
        TextView createAt = (TextView) findViewById(R.id.board_create_at);

        if (getActionBar() != null) {
            getActionBar().setTitle(boardResponse.getTitle());
        }

//        title.setText(boardResponse.getTitle());
        content.setText(boardResponse.getContent());
        writerName.setText(boardResponse.getWriterName());
        createAt.setText(boardResponse.getCreatedAt().toString());

        progressDialog.dismiss();
    }
}
