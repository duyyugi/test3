package com.arviet.arproject.model;

public class ARContent {
    private int contentID;
    private float xPosition;
    private float yPosition;
    private float zPosition;
    private float xRotation;
    private float yRotation;
    private float zRotation;
    private float xScale;
    private float yScale;
    private float zScale;
    private String URL;
    private int actionID;
    private String filename;
    private int isFile;
    private int isTemp;
    private int isChoosen;
    private TextARContent textARContent;
    private ARContent fatherARContent;
    private boolean isChildHidden;

    public int getContentID() {
        return contentID;
    }

    public void setContentID(int contentID) {
        this.contentID = contentID;
    }

    public float getxPosition() {
        return xPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public float getzPosition() {
        return zPosition;
    }

    public void setzPosition(float zPosition) {
        this.zPosition = zPosition;
    }

    public float getxRotation() {
        return xRotation;
    }

    public void setxRotation(float xRotation) {
        this.xRotation = xRotation;
    }

    public float getyRotation() {
        return yRotation;
    }

    public void setyRotation(float yRotation) {
        this.yRotation = yRotation;
    }

    public float getzRotation() {
        return zRotation;
    }

    public void setzRotation(float zRotation) {
        this.zRotation = zRotation;
    }

    public float getxScale() {
        return xScale;
    }

    public void setxScale(float xScale) {
        this.xScale = xScale;
    }

    public float getyScale() {
        return yScale;
    }

    public void setyScale(float yScale) {
        this.yScale = yScale;
    }

    public float getzScale() {
        return zScale;
    }

    public void setzScale(float zScale) {
        this.zScale = zScale;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getActionID() {
        return actionID;
    }

    public void setActionID(int actionID) {
        this.actionID = actionID;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getIsFile() {
        return isFile;
    }

    public void setIsFile(int isFile) {
        this.isFile = isFile;
    }

    public int getIsTemp() {
        return isTemp;
    }

    public void setIsTemp(int isTemp) {
        this.isTemp = isTemp;
    }

    public int getIsChoosen() {
        return isChoosen;
    }

    public void setIsChoosen(int isChoosen) {
        this.isChoosen = isChoosen;
    }

    public TextARContent getTextARContent() {
        return textARContent;
    }

    public void setTextARContent(TextARContent textARContent) {
        this.textARContent = textARContent;
    }

    public ARContent getFatherARContent() {
        return fatherARContent;
    }

    public void setFatherARContent(ARContent fatherARContent) {
        this.fatherARContent = fatherARContent;
    }

    public boolean isChildHidden() {
        return isChildHidden;
    }

    public void setChildHidden(boolean childHidden) {
        isChildHidden = childHidden;
    }
}
