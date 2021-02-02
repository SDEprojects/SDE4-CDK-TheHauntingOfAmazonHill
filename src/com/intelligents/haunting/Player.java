package com.intelligents.haunting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class Player implements java.io.Serializable {
    private final String name;
    private final List<String> journal = new ArrayList<>();
    private final TimeStamp time = new TimeStamp();
    String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy @ HH:mm"));

    public List<String> getRoomsVisited() {
        return roomsVisited;
    }

    private final List<String> roomsVisited = new ArrayList<>();

    Player(String name) {
        this.name = name;
    }

    void addToRoomsVisited(String roomTitle) {
        roomsVisited.add(roomTitle);
    }

    String getName() {
        return name;
    }

    List<String> getJournal() {
        return journal;
    }

    void setJournal(String journal) {
        this.journal.add("Date: " + localDateString + " -> " + journal);
    }

    @Override
    public String toString() {
        return getName() + "'s"
                + " journal currently shows these items: " + "\uD83D\uDCD6" + ConsoleColors.RESET + "\n\n"
                + ConsoleColors.RED_BOLD_BRIGHT + getJournal() + ConsoleColors.RESET;
    }

}
