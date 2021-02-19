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
    private final List<Items> playerWeapons = new ArrayList<>();
    private final List<Items> playerItems = new ArrayList<>();
    private final List<Items> playerInventory = new ArrayList<>();


    private Player() {
        if (playerSingleton != null) {
            throw new RuntimeException("Need to use getInstance()");
        }
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
        this.resetPlayerWeapons();
        this.resetPlayerHitPoints();
        this.resetPlayerItems();
        this.resetPlayerInventory();
    }

    public void resetPlayerRoundTwo() {
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
        String bookEmoji = "\uD83D\uDCD6";
        return bookEmoji + getName() + "'s"
                + " journal currently shows these items: " + bookEmoji + "\n\n"
                + getJournal();
    }

    public void addWeapon(Items weapon) {
        playerWeapons.add(weapon);
    }

    public void addItem(Items item) {
        playerItems.add(item);
    }

    public void removeItem(Items item) {
        playerInventory.remove(item);
        playerItems.remove(item);
    }

    public void removeWeapon(Items weapon) {
        playerInventory.remove(weapon);
        playerWeapons.remove(weapon);
    }

    public List<Items> getAllWeapons() {
        return playerWeapons;
    }

    private void resetPlayerWeapons() {
        playerWeapons.clear();
    }

    public Items getSpecificWeapon(String weaponName) {
        for(int itr = 0; itr < playerWeapons.size(); itr++) {
            if (playerWeapons.get(itr).getName().equals(weaponName)) return playerWeapons.get(itr);
        }
        return null;
    }

    public List<Items> getPlayerInventory() {
        playerInventory.clear();
        playerInventory.addAll(playerWeapons);
        playerInventory.addAll(playerItems);
        return playerInventory;
    }

    private void resetPlayerInventory() {
        playerInventory.clear();
    }


    public List<Items> getPlayerItems() {
        return playerItems;
    }

    public Items getSpecificItem(String itemName) {
        for (Items playerItem : playerItems) {
            if (playerItem.getName().equals(itemName)) return playerItem;
        }
        return null;
    }

    private void resetPlayerItems() {
        playerItems.clear();
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

    private void resetPlayerHitPoints() { hitPoints = 1000; }

}