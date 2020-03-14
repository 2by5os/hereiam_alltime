package com.geekwims.hereiam.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.attendance.AttendHistory;
import com.geekwims.hereiam.attendance.AttendInfo;

import java.util.Locale;


public class HistoryListAdapter extends BaseAdapter {
    private AttendHistory attendHistory = new AttendHistory();

    public HistoryListAdapter() {
    }

    public HistoryListAdapter(AttendHistory attendHistory) {
        this.attendHistory = attendHistory;
    }

    @Override
    public int getCount() {
        return attendHistory.getAttendInfos().size();
    }

    @Override
    public Object getItem(int position) {
        return attendHistory.getAttendInfos().get(position);
    }

    @Override
    public long getItemId(int position) {
        return attendHistory.getAttendInfos().get(position).getWeek();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView number = (TextView) convertView.findViewById(R.id.attend_history_no);
        TextView date = (TextView) convertView.findViewById(R.id.attend_history_date);
        TextView time = (TextView) convertView.findViewById(R.id.attend_history_time);
        TextView status = (TextView) convertView.findViewById(R.id.attend_history_status);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        AttendInfo attendInfo = attendHistory.getAttendInfos().get(position);

        // 아이템 내 각 위젯에 데이터 반영
        number.setText(String.format(Locale.KOREA, "%d주차", attendInfo.getWeek()));

        date.setText(attendInfo.getStrDate());
        time.setText(attendInfo.getStrTime());

        status.setText(attendInfo.getStatus().toString());

        return convertView;
    }
}
