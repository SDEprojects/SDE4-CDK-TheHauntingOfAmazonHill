package com.intelligents.haunting;

public class MiniGhost extends Ghost implements java.io.Serializable {
    int hitPoints;

    MiniGhost(String name, String type, int hitPoints) {
        super(name, type);
        this.hitPoints = hitPoints;
    }

    @Override
    public String toString() {
        return getType() + ":\n";
    }
}