package com.arviet.arproject.requestModel;

public class GetLessonListRequest {
    private int studentID;

    public GetLessonListRequest(int studentID) {
        this.studentID = studentID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }
}
