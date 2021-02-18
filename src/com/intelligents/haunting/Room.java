package com.intelligents.haunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Room implements java.io.Serializable {
    private String roomTitle;
    private String description;
    private String roomEvidence = "";
    private MiniGhost roomMiniGhost = null;
    Map<String, Room> roomExits = new HashMap<>();
    private List<Items> roomItems = new ArrayList<>();
    public Map<String, String> directionList = new HashMap<>();

    Room(String title, String description) {
        this.roomTitle = title;
        this.description = description;
    }

    public MiniGhost getRoomMiniGhost() {
        return roomMiniGhost;
    }

    public void setRoomMiniGhost(MiniGhost roomMiniGhost) {
        this.roomMiniGhost = roomMiniGhost;
    }

    String getRoomTitle() {
        return roomTitle;
    }

    String getDescription() {
        return description;
    }

    String getRoomEvidence() {
        try {
            return roomEvidence;
        } catch (NullPointerException e) {
            return "";
        }
    }

    void setRoomEvidence(String roomEvidence) {
        this.roomEvidence = roomEvidence;
    }

    void addItemToRoom(Items item) {
        roomItems.add(item);
    }

    void removeItemFromRoom(Items item) {
        roomItems.remove(item);
    }

    void clearRoomItems() {
        this.roomItems.clear();
    }

    List<Items> getRoomItems() {
        List<Items> results = new ArrayList<>();
        if (roomItems.size() == 0) {
            return results;
        }
        return roomItems;
    }

}