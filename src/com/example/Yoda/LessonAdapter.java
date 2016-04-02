package com.example.Yoda;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by apcom on 31.03.2016.
 */
public class LessonAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Lesson> objects;

    private static String SETTINGS_FILENAME = "settings";
    private static String SETTINGS_HIDE_CANCELED_LESSONS = "HIDE_CANCELED_LESSONS";
    private static String SETTINGS_MARK_ENDED_LESSONS = "MARK_ENDED_LESSONS";
    private SharedPreferences settings;

    LessonAdapter(Context context, ArrayList<Lesson> lessons) {
        this.ctx = context;
        this.objects = lessons;
        inflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        settings = context.getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.lesson_layout, parent, false);
        }

        Lesson lesson = getLesson(position);

        ((TextView) view.findViewById(R.id.lessonTime)).setText(lesson.time);
        ((TextView) view.findViewById(R.id.lessonTitle)).setText(lesson.title);
        ((TextView) view.findViewById(R.id.lessonMeta)).setText(lesson.type + " в " + lesson.place + " / " + lesson.teacher);

        TextView homeworkLabel = (TextView) view.findViewById(R.id.homeworkLabel);
        TextView homework = (TextView) view.findViewById(R.id.homework);

        TextView controlLabel = (TextView) view.findViewById(R.id.controlLabel);
        TextView control = (TextView) view.findViewById(R.id.control);

        TextView canceledLabel = (TextView) view.findViewById(R.id.canceledLabel);

        LinearLayout rootLayout = (LinearLayout)view.findViewById(R.id.lessonLayout);

        if (lesson.type_css.equals("practice")) {
            Log.d("WWW", "Practice");
            rootLayout.setBackgroundColor(Color.parseColor("#85c0ec"));
        } else if (lesson.type_css.equals("lab")) {
            Log.d("WWW", "Lab");
            rootLayout.setBackgroundColor(Color.parseColor("#ed9595"));
        }

        if (!lesson.homework.equals("")) {
            homeworkLabel.setVisibility(View.VISIBLE);
            homework.setVisibility(View.VISIBLE);
            homework.setText(lesson.homework);
        }

        if (!lesson.control.equals("")) {
            controlLabel.setVisibility(View.VISIBLE);
            control.setVisibility(View.VISIBLE);
            control.setText(lesson.control);
        }

        if (lesson.is_canceled) {
            homeworkLabel.setVisibility(View.GONE);
            homework.setVisibility(View.GONE);
            controlLabel.setVisibility(View.GONE);
            control.setVisibility(View.GONE);
            canceledLabel.setVisibility(View.VISIBLE);
        }

        if (settings.getBoolean(SETTINGS_MARK_ENDED_LESSONS, false) && lesson.is_ended == true) {
            rootLayout.setAlpha((float) 0.5);
        }

        return view;
    }

    Lesson getLesson(int position) {
        return ((Lesson) getItem(position));
    }
}
