package com.arviet.arproject.responseModel;

import com.arviet.arproject.model.Lesson;

public class AddLessonResponse {
    private String status;
    private Lesson lesson;

    public AddLessonResponse(String status, Lesson lesson) {
        this.status = status;
        this.lesson = lesson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}
