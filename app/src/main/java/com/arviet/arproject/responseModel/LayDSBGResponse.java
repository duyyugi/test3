package com.arviet.arproject.responseModel;

import com.arviet.arproject.model.Lesson;

import java.util.List;

public class LayDSBGResponse {
    List<Lesson> DSLesson;
    public LayDSBGResponse(List<Lesson> DSLesson) {
        this.DSLesson = DSLesson;
    }

    @Override
    public String toString() {
        return "LayDSBGResponse{" +
                "DSBaiGiang=" + DSLesson +
                '}';
    }
}
