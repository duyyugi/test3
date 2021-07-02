package com.arviet.arproject.requestModel;

public class AddLessonRequest {
    private int lessonID;

    public AddLessonRequest(int lessonID) {
        this.lessonID = lessonID;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }
}
