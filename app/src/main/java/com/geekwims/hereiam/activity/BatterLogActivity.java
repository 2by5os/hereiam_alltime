package com.geekwims.hereiam.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.db.BatteryLog;
import com.geekwims.hereiam.db.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BatterLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_log);

        ListView list = (ListView) findViewById(R.id.list_battery_log);

        DatabaseHelper helper = DatabaseHelper.getInstance(this);

        Cursor cursor = helper.getBatteryLogCursor();

        String[] columns = new String[] {"begin_time", "end_time", "begin_level", "end_level"};
        int to[] = new int[] {1};

        MyAdapter myAdapter = new MyAdapter(this, R.layout.list_item, cursor, columns, to);

        list.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {
        private int mCount = 0;
        private ArrayList<BatteryLog> batteryLogs = new ArrayList<>();

        // override other abstract methods here
        public MyAdapter(Context context, int list_item, Cursor cursor, String[] columns, int[] to) {

            mCount = cursor.getCount();

            for (int i = 0; i < mCount; i++) {
                cursor.moveToNext();
                batteryLogs.add(new BatteryLog(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3)));
            }
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(int i) {
            return batteryLogs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }

            BatteryLog batteryLog = batteryLogs.get(position);

            Date dt = new Date();
            dt.setTime((long) batteryLog.getBeginTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a", Locale.KOREA);

            String output = batteryLog.getBeginLevel() + " ~ " + batteryLog.getEndLevel() + " // " + sdf.format(dt);

            dt.setTime((long) batteryLog.getEndTime());
            output += " ~ " + sdf.format(dt);

            ((TextView) convertView.findViewById(R.id.text_list_item)).setText(output);

            return convertView;
        }
    }
}
