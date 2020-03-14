package com.geekwims.hereiam.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.response.course.CourseInfo;

import java.util.ArrayList;
import java.util.List;


public class TakesListAdapter extends BaseAdapter {
    private List<CourseInfo> courseInfos = new ArrayList<>();

    public TakesListAdapter() {
    }

    public TakesListAdapter(List<CourseInfo> courseInfos) {
        this.courseInfos = courseInfos;
    }

    @Override
    public int getCount() {
        return courseInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return courseInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return courseInfos.get(position).getCourseId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {      final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.takes_list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleTextView = (TextView) convertView.findViewById(R.id.item_title) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.item_description) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        CourseInfo courseInfo = courseInfos.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(courseInfo.getCourseName());
        descTextView.setText(courseInfo.getProfessor().getName());

        return convertView;
    }
}
