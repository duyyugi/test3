package com.arviet.arproject.responseModel;

import com.arviet.arproject.model.Marker;

import java.util.List;

public class GetMarkerListResponse {
    private List<Marker> markerList;
    private String status;

    public GetMarkerListResponse(List<Marker> markerList, String status) {
        this.markerList = markerList;
        this.status = status;
    }
    public List<Marker> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<Marker> markerList) {
        this.markerList = markerList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
