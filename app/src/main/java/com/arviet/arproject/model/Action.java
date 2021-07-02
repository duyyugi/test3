package com.arviet.arproject.model;

import java.util.List;

public class Action {
    private int actionID;
    private String name;
    private int markerID;
    private List<ARContent> arContentList;

    public int getActionID() {
        return actionID;
    }

    public void setActionID(int actionID) {
        this.actionID = actionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMarkerID() {
        return markerID;
    }

    public void setMarkerID(int markerID) {
        this.markerID = markerID;
    }

    public List<ARContent> getArContentList() {
        return arContentList;
    }

    public void setArContentList(List<ARContent> arContentList) {
        this.arContentList = arContentList;
    }
}
