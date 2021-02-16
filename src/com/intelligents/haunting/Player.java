package com.intelligents.haunting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class Player implements java.io.Serializable {

    private static Player playerSingleton;
    private String name;
    private String mostRecentExit;
    private final List<String> journal = new ArrayList<>();
    private final List<String> roomsVisited = new ArrayList<>();
    private List<Weapon> weapons = new ArrayList<>();


    private Player() {
        if (playerSingleton != null) {
            throw new RuntimeException("Need to use getInstance()");
        }
        addWeapon(new Weapon("Sword", "Long and heavy; with a point that can pierce through any solid object!", 100));
    }


    static Player getInstance() {
        if (playerSingleton == null) {
            playerSingleton = new Player();
        }
        return playerSingleton;
    }

    public String getMostRecentExit() {
        return mostRecentExit;
    }

    public void setMostRecentExit(String mostRecentExit) {
        this.mostRecentExit = mostRecentExit;
    }

    void addToRoomsVisited(String roomTitle) {
        roomsVisited.add(roomTitle);
    }

    void setName(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    List<String> getRoomsVisited() {
        return roomsVisited;
    }

    List<String> getJournal() {
        return journal;
    }

    void setJournal(String journal) {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy @ HH:mm"));
        this.journal.add("Date: " + localDateString + " -> " + journal);
    }

    void resetPlayer() {
        this.clearJournal();
        this.resetRoomsVisited();
    }

    private void resetRoomsVisited() {
        roomsVisited.clear();
        addToRoomsVisited("Lobby");
    }

    private void clearJournal() {
        journal.clear();
    }

    @Override
    public String toString() {
        return getName() + "'s"
                + " journal currently shows these items: " + "\uD83D\uDCD6" + "\n\n"
                + getJournal();
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }

    public void removeWeapon(Weapon weapon) {
        weapons.remove(weapon);
    }

    public List getAllWeapons() {
        return weapons;
    }
}