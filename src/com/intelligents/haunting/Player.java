package com.intelligents.haunting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class Player implements java.io.Serializable {

    private static Player playerSingleton;
    private String name;
    private String mostRecentExit;
    private int hitPoints = 1000;
    private final List<String> journal = new ArrayList<>();
    private final List<String> roomsVisited = new ArrayList<>();
    private List<Items> weapons = new ArrayList<>();
    private List<Items> items = new ArrayList<>();
    private List<Weapon> inventory = new ArrayList<>();


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

    public void addWeapon(Items weapon) {
        weapons.add(weapon);
    // For now these are set exclusive to weapons, but once items are formed, this can be changed to items and weapon can be made an item
    public void addWeapon(Weapon weapon) {
        inventory.add(weapon);
    }

    public void addItem(Items item) {
        items.add(item);
    }

    public void removeWeapon(Weapon weapon) {
        inventory.remove(weapon);
    }

    public List<Items> getAllWeapons() {
        return weapons;
    public List getAllWeapons() {
        return inventory;
    }

    public List<Items> getItems() {
        return items;
    }

    public void playerTakesDamage(int damagePoints) {
        hitPoints -= damagePoints;
    }

    public void playerGainsHealth(int healthPoints) {
        hitPoints += healthPoints;
    }

    public int getPlayerHitPoints() {
        return hitPoints;
    }



}