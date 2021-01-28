package com.intelligents.haunting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private World world = new World();
    private List<Ghost> ghosts = new ArrayList<>();

    private Ghost currentGhost;

    private Random r = new Random();

    public Game() {
        populateGhostList();
        setCurrentGhost(getRandomGhost());
        assignRandomEvidenceToMap();
        for (Room room : world.gameMap) {
            System.out.println(room.getRoomTitle());
            System.out.println(room.getRoomEvidence());
        }
    }

    void start() {
        boolean isValidInput;
        boolean isGameRunning = true;
        String[] input;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Thank you for choosing to play The Haunting on Amazon Hill. " +
                "What would you like your name to be? ");
        System.out.println(">>");

        input = scanner.nextLine().strip().toLowerCase().split(" ");

        Player player = new Player(input[0]);

        //System.out.println(player);
        System.out.println("Good luck to you, Detective " + player.getName());
        System.out.println("---------------------------");


        while (isGameRunning) {
            isValidInput = true;

            System.out.println("Your location is " + world.currentRoom.getRoomTitle());
            System.out.println(world.currentRoom.getDescription());
            System.out.println("To move type: Go North, Go East, Go South, or Go West");
            System.out.println("---------------------------");
                        System.out.println("****************************");
            System.out.println(">>");

            input = scanner.nextLine().strip().toLowerCase().split(" ");


            switch (input[0]) {
                case "read":
                    System.out.println("****************************");
                    System.out.println(player);
                    System.out.println("****************************");
                    break;
                case "show":
                    System.out.println("****************************");
                    System.out.println("Your location is " + world.currentRoom.getRoomTitle());
                    if (world.currentRoom.getRoomItems().isEmpty()) {
                        System.out.println("Currently there are no items in "
                                + world.currentRoom.getRoomTitle());
                    }
                    else {
                        System.out.println(world.currentRoom.getRoomItems());
                    }
                    System.out.println("****************************");
                    break;
                case "exit":
                case "quit":
                case "q":
                    isGameRunning = false;
                    break;
                case "move":
                case "go":

                    while (isValidInput) {

                        switch (input[1]) {

                            case "north":
                            case "east":
                            case "south":
                            case "west":
                                if (world.currentRoom.roomExits.containsKey(input[1])) {
                                    world.currentRoom = world.currentRoom.roomExits.get(input[1]);
                                    isValidInput = false;

                                    break;
                                }
                            default:
                                System.out.println("You hit wall. Try again: ");
                                System.out.println(">>");
                                input = scanner.nextLine().strip().toLowerCase().split(" ");
                                break;

                        }

                    }

            }
        }
        System.out.println("Thank you for playing our game!!");
    }

    public void populateGhostList() {
        this.setGhosts(XMLParser.populateGhosts(XMLParser.readGhosts()));
    }

    public void printGhosts() {
        for (Ghost ghost : ghosts) {
            System.out.println(ghost);
        }
    }
    public Ghost getRandomGhost() {
        int index = r.nextInt(ghosts.size());
        return ghosts.get(index);
    }
    public void assignRandomEvidenceToMap() {
        //for each evidence from monster, get rooms from world.gamemap equivalent to the number of evidences.
        for (int i = 0; i < currentGhost.getEvidence().size(); i++) {
            // Success condition
            boolean addedEvidence = false;

            // Loop while no success
            while(!addedEvidence) {
                Room x = getRandomRoomFromWorld();
                System.out.println(x.getRoomTitle());
                if (x.getRoomEvidence() == null) {
                    x.setRoomEvidence(currentGhost.getEvidence().get(i));
                    System.out.println("added " + currentGhost.getEvidence().get(i) + " to " + x.getRoomTitle());
                    addedEvidence = true;
                }
            }

        }
    }
    public Room getRandomRoomFromWorld() {
        int index = r.nextInt(ghosts.size());
        return world.gameMap.get(index);
    }

    // Getters / Setters

    public List<Ghost> getGhosts() {
        return ghosts;
    }
    public void setGhosts(List<Ghost> ghosts) {
        this.ghosts = ghosts;
    }
    public Ghost getCurrentGhost() {
        return currentGhost;
    }
    public void setCurrentGhost(Ghost ghost) {
        this.currentGhost = ghost;
    }
    public World getWorld() {
        return world;
    }

}
