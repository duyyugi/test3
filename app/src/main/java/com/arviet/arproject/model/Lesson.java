package com.arviet.arproject.model;

public class Lesson {
    public int lessonID;
    public String name;
    public String description;
    public String teacherName;

    public Lesson(int lessonID, String name, String description, String teacherName) {
        this.lessonID = lessonID;
        this.name = name;
        this.description = description;
        this.teacherName = teacherName;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
