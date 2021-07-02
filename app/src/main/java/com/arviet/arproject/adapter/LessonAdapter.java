package com.arviet.arproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arviet.arproject.R;
import com.arviet.arproject.model.Lesson;

import java.util.List;

public class LessonAdapter extends BaseAdapter {
    Context myContext;
    int myLayout;
    List<Lesson> arrayLesson;
    public LessonAdapter(Context context, int layout, List<Lesson> lessonList){
        myContext = context;
        myLayout = layout;
        arrayLesson = lessonList;
    }
    @Override
    public int getCount() {
        return arrayLesson.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(myLayout, null);
        TextView txvLessonName = (TextView) convertView.findViewById(R.id.textViewLessonName);
        txvLessonName.setText(arrayLesson.get(position).name);
        TextView txvTecherName = (TextView) convertView.findViewById(R.id.textViewTeacherName);
        txvTecherName.setText(String.valueOf(arrayLesson.get(position).teacherName));
        return convertView;
    }
}
