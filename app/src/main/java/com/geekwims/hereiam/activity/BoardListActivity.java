package com.geekwims.hereiam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.constant.BOARD_TYPE;
import com.geekwims.hereiam.response.BoardPageResponse;
import com.geekwims.hereiam.response.BoardResponse;
import com.geekwims.hereiam.response.course.CourseInfo;
import com.geekwims.hereiam.widget.BoardListAdapter;
import com.geekwims.hereiam.widget.TakesListAdapter;

import java.util.List;

public class BoardListActivity extends AppCompatActivity {
    private static final String TAG = "TakesActivity";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("불러오는 중...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new InitTask().execute(0);
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
    private class InitTask extends AsyncTask<Integer, Void, BoardPageResponse> {
        @Override
        protected BoardPageResponse doInBackground(Integer... objects) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ApiService apiService = ApiService.getInstance();

            return apiService.getBoards(BOARD_TYPE.NOTICE, objects[0]);
        }

        @Override
        protected void onPostExecute(BoardPageResponse courseInfos) {

            if (courseInfos == null) {
                loadFail();
                return;
            }

            loadSuccess(courseInfos);
        }
    }

    private void loadContent(int page) {
        progressDialog.show();
        new InitTask().execute(page);
    }

    private void loadFail() {
        progressDialog.dismiss();
        finish();
    }
    private void loadSuccess(BoardPageResponse page) {
        ListView listView;
        BoardListAdapter takesListAdapter = new BoardListAdapter(page);

        listView = (ListView) findViewById(R.id.board_list);
        listView.setAdapter(takesListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BoardResponse boardResponse = (BoardResponse) parent.getItemAtPosition(position);
                view(boardResponse);
            }
        });

        // pagination

        LinearLayout paginationWrapper = (LinearLayout) findViewById(R.id.layout_pagination);

        paginationWrapper.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        for (int i = 1; i < page.getTotalPages(); i++) {
            Button pagingBtnTemplate = new Button(this);
            pagingBtnTemplate.setLayoutParams(layoutParams);
            pagingBtnTemplate.setText(String.valueOf(i));
            pagingBtnTemplate.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
            final int finalI = i;
            pagingBtnTemplate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadContent(finalI);
                }
            });

            paginationWrapper.addView(pagingBtnTemplate);
        }


        progressDialog.dismiss();
    }

    private void view(BoardResponse boardResponse) {
        Intent intent = new Intent(this, BoardActivity.class);
        intent.putExtra("boardId", boardResponse.getId());
        startActivity(intent);
    }
}
