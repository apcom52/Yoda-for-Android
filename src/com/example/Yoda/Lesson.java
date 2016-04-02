package com.example.Yoda;

/**
 * Created by apcom on 31.03.2016.
 */
public class Lesson {
    String title;
    String teacher;
    String place;
    String time;
    String type;
    String type_css;

    String homework = "";
    String control = "";

    Boolean is_canceled = false;
    Boolean is_ended = false;

    Lesson(String _title, String _teacher, String _place, String _time, String _type, String _type_css) {
        this.title = _title;
        this.teacher = _teacher;
        this.place = _place;
        this.time = _time;
        this.type = _type;
        this.type_css = _type_css;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public void setCanceled() {
        this.is_canceled = true;
    }

    public void setEnded() {
        this.is_ended = true;
    }
}
