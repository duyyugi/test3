package com.arviet.arproject.model;

import com.google.ar.sceneform.AnchorNode;

public class AnchorNodeFatherID {
    private AnchorNode anchorNode;
    private int fatherID;

    public AnchorNodeFatherID(AnchorNode anchorNode, int fatherID) {
        this.anchorNode = anchorNode;
        this.fatherID = fatherID;
    }

    public AnchorNode getAnchorNode() {
        return anchorNode;
    }

    public void setAnchorNode(AnchorNode anchorNode) {
        this.anchorNode = anchorNode;
    }

    public int getFatherID() {
        return fatherID;
    }

    public void setFatherID(int fatherID) {
        this.fatherID = fatherID;
    }
}
