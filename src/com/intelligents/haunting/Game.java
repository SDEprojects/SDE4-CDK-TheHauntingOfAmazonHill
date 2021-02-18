package com.intelligents.haunting;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.intelligents.haunting.CombatEngine.runCombat;

public class Game implements java.io.Serializable {
    private final String resourcePath;
    private final ClassLoader cl;
    private World world;
    private List<Ghost> ghosts = new ArrayList<>();
    private List<MiniGhost> miniGhosts = new ArrayList<>();
    private Map<String, List<? extends Items>> items = new HashMap<>();
    private List<Weapon> weapons;
    private final SaveGame SaveGame = new SaveGame();
    private Ghost currentGhost;
    private final Random r = new Random();
    private final String divider = "************************************************************************************************";
    private Player player;
    private HauntingJFrame jFrame;
    SaveGame save = new SaveGame();
    private final transient FileReader fileReader = new FileReader();
    private MusicPlayer mp;
    private MusicPlayer soundEffect;
    private MusicPlayer walkEffect;
    private MusicPlayer keyboardEffect;
    private MusicPlayer paperFalling;
    private int guessCounter = 0;
    boolean isGameRunning = true;
    String moveGuide = "To move type: Go North, Go East, Go South, or Go West";
    String currentRoom;
    private String currentLoc;
    private boolean isSound = true;
    int attemptCount = 0;


    public Game(HauntingJFrame jFrame,
                String pathStartSounds,
                String pathStartResources,
                ClassLoader classLoader) {
        //populates the main ghost list and sets a random ghost for the current game session
        resourcePath = pathStartResources;
        cl = classLoader;
        world = new World(cl, resourcePath);
        player = Player.getInstance();
        currentRoom = world.getCurrentRoom().getRoomTitle();
        currentLoc = "Your location is " + currentRoom;
        setMusic(pathStartSounds);
        populateGhostList(cl);
        populateMiniGhostList(cl);
        populateItemsList(cl);
        setCurrentGhost(getRandomGhost());
        assignRandomEvidenceToMap();
        assignRandomMiniGhostToMap();
        assignRandomItemsToMap();
        this.jFrame = jFrame;
    }

    private void setMusic(String pathStart) {
        mp = new MusicPlayer(pathStart + "Haunted Mansion.wav", cl);
        soundEffect = new MusicPlayer(pathStart + "page-flip-4.wav", cl);
        walkEffect = new MusicPlayer(pathStart + "footsteps-4.wav", cl);
        keyboardEffect = new MusicPlayer(pathStart + "fast-pace-typing.wav", cl);
        paperFalling = new MusicPlayer(pathStart + "paper flutter (2).wav", cl);
    }

    public void intro(String[] gameType) throws IOException {
        if (gameType[0].matches("1")) {
            replaceGameWindowWithColorText(fileReader.fileReader(resourcePath, "introText", cl), Color.RED);
            appendToGameWindowsWithColorNoSound(fileReader.fileReader(resourcePath, "settingTheScene", cl), Color.WHITE);
            appendToGameWindowsWithColorNoSound("\n" + "Thank you for choosing to play The Haunting of Amazon Hill. " +
                    "What would you like your name to be?\n", Color.GREEN);

            jFrame.stopThemeSong();
            mp.startMusic();
            //If loaded game was selected then the saved file is loaded
        } else if (gameType[0].matches("4")) {
            try {
                save.setGame(this);
                save.loadGame();

                jFrame.stopThemeSong();
                mp.startMusic();
                SaveGame.setGame(this);
                replaceGameWindowWithColorText("Loading game!!!", Color.YELLOW);
                appendToGameWindowsWithColorNoSound("\n\nWelcome back, " + player.getName() + "!" +
                        "\nYou are currently in the " + currentRoom + ".", Color.GREEN);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            appendToGameWindowsWithColorNoSound("Invalid selection , please enter 1.", Color.pink);
        }
    }

    void createPlayer(String[] nameInput) {
        updateCurrentRoom();

        player.setName(nameInput[0]);

        String formatted = "If you're new to the game type help for assistance.";
        replaceGameWindowWithColorText(formatted, Color.CYAN);

        formatted = "\nGood luck to you, " + player.getName() + "!";
        appendWithColoredText(formatted, Color.white);
        appendToGameWindowsWithColorNoSound("\n\n" + currentLoc, Color.YELLOW);
        appendToGameWindowsWithColorNoSound("\n" + moveGuide, Color.YELLOW);

    }

    private void updateCurrentRoom() {
        currentRoom = world.getCurrentRoom().getRoomTitle();
        currentLoc = "Your location is " + currentRoom;
        jFrame.playerLocationArea.setText(currentLoc);
    }

    public boolean checkStringNorth(String[] input) {
        return Pattern.compile("(?i)^no.*").matcher(input[1]).find();
    }

    public boolean checkStringSouth(String[] input) {
        return Pattern.compile("(?i)^so.*").matcher(input[1]).find();
    }

    public boolean checkStringEast(String[] input) {
        return Pattern.compile("(?i)^e(a|s).*").matcher(input[1]).find();
    }

    public boolean checkStringWest(String[] input) {
        return Pattern.compile("(?i)^w(e|s).*").matcher(input[1]).find();
    }


    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    void processInput(boolean isValidInput, String[] input, int attempt) {
        updateCurrentRoom();
        checkIfRoomVisited();
        if (input.length > 2) {
            replaceGameWindowWithColorText("\n\nYou cannot type more than 2 commands! Try again:", Color.RED);
        } else {
            try {
                switch (input[0].toLowerCase(Locale.ROOT)) {
                        /* Case for original developer easter egg, disabled for security. Uncomment to enable
                        and also related function at the bottom of Game.java
                        case "chris":
                            chrisIsCool();
                            break;
                         */
                    //Allows for volume to be increased or decreased
                    case "volume":
                        if (input[1].equals("up")) {
                            mp.setVolume(5.0f);
                        } else if (input[1].equals("down")) {
                            mp.setVolume(-5.0f);
                        }
                        break;
                    //Prints journal and plays page turning sound effect
                    case "read":
                        printJournal();
                        if (isSound) {
                            soundEffect.playSoundEffect();
                        }
                        break;
                    //Creates a save file that can be loaded
                    case "save":
                        SaveGame.save(this);
                        break;
                    //Reads the loaded usr.save file
                    case "load":
                        SaveGame.loadGame();
                        break;
                    //
                    case "?":
                    case "help":
                        replaceGameWindowWithColorText(fileReader.fileReader(resourcePath, "Rules", cl), Color.white);
                        break;
                    case "open":
                        openMap();
                        break;
                    //Displays room contents/evidence
                    case "look":
                    case "view":
                    case "show":
                        replaceGameWindowWithColorText("\n\n" + divider + "\n", Color.WHITE);
                        updateCurrentRoom();
                        if (world.getCurrentRoom().getRoomItems().isEmpty()) {
                            appendWithColoredText("You see no useful items.\n\n", Color.WHITE);
                        } else {
                            appendWithColoredText("You see some useful items: " +
                                    world.getCurrentRoom().getRoomItems().toString() + "\n\n", Color.ORANGE);
                        }
                        if (world.getCurrentRoom().getRoomEvidence().isEmpty()) {
                            appendWithColoredText("You see no useful evidence." + "\n\n", Color.WHITE);
                        } else {
                            addEvidenceToJournal();
                            appendWithColoredText("You've found some useful evidence: " +
                                    world.getCurrentRoom().getRoomEvidence() + "\n\n", Color.WHITE);
                            appendWithColoredText("Evidence logged into your journal.\n", Color.WHITE);
                        }
                        appendWithColoredText(divider + "\n", Color.WHITE);
                        break;
                    case "write":
                        replaceGameWindowWithColorText("Would you like to document anything in your journal? [Yes/No]\n", Color.WHITE);
                        break;
                    //Allows user to leave if more than one room has been input into RoomsVisited
                    case "exit":
                        if (userAbleToExit()) {
                            // In order to win, user has to have correct evidence and guessed right ghost
                            if (!checkIfHasAllEvidenceIsInJournal()) {
                                replaceGameWindowWithColorText("It seems your journal does not have all of the evidence needed to determine the ghost." +
                                        " Would you like to GUESS the ghost anyway or go back INSIDE?\n", Color.WHITE);
                            } else {
                                replaceGameWindowWithColorText("It seems like you could be ready to determine the ghost." +
                                        " Would you like to GUESS the ghost or go back INSIDE to continue exploring?\n", Color.WHITE);
                            }
                            appendWithColoredText(divider + "\n", Color.WHITE);
                        }
                        break;
                    case "quit":
                    case "q":
                        if (jFrame.quitGame()) mp.quitMusic();
                        break;
                    case "pause":
                        mp.pauseMusic();
                        break;
                    case "stop":
                        stopSound();
                        break;
                    case "play":
                        mp.startMusic();
                        break;
                    case "move":
                    case "go":
                        CheckCommand(isValidInput, input, attempt);
                        break;
                    case "inventory":
                        replaceGameWindowWithColorText("Inventory:\n\n", Color.white);
                        // This will need to be refactored to match Player once items exist in the game.
                        List<Items> playerInventory = player.getPlayerInventory();
                        for (Items item : playerInventory) {
                            appendToGameWindowsWithColorNoSound(item.getType() + " - " +
                                    item.getName() + " - " + item.getDescription() + "\n", Color.WHITE);
                        }
                        break;
                    case "get":
                    case "take":
                    case "grab":
                        String result = addToInventory(input);
                        UIManager.put("OptionPane.messageForeground", result.contains("pickup") ? Color.GREEN :
                                Color.RED);
                        UIManager.put("Panel.background", Color.DARK_GRAY);
                        JOptionPane.showMessageDialog(null, result);
                        processInput(true, new String[]{"look"}, 0);
                        break;
                    default:
                        replaceGameWindowWithColorText("Command not recognized! Please try again!", Color.RED);
                }
            } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
                appendWithColoredText("Make sure to add a verb e.g. 'move', 'go', 'open', 'read' then " +
                        "a noun e.g. 'north', 'map', 'journal'.\n", Color.WHITE);
            } catch (IOException | InterruptedException | BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    private String addToInventory(String[] input) {
        List<Items> currentItems = world.getCurrentRoom().getRoomItems();
        List<Items> newList = new ArrayList<>();
        String result = "";
        for (Items item : currentItems) {
            if (item.getName().equalsIgnoreCase(input[1])) {
                if (item.getType().equalsIgnoreCase("weapon")) {
                    player.addWeapon(item);
                } else {
                    player.addItem(item);
                }
                newList.add(item);
                result = "You pickup the " + item.getName() +
                        " and place it in your inventory.";
            } else {
                result = "I don't see what you're trying to get.";
            }
            break;
        }
        currentItems.removeAll(newList);
        return result;
    }

    private void CheckCommand(boolean isValidInput, String[] input, int attempt) throws IOException {
        if (checkStringNorth(input) && !input[1].equals("north")) {
            int dirChoice = JOptionPane.showOptionDialog(new JFrame(),
                    "Did you mean to say north? ",
                    "Going north?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, new Object[]{"Yes", "No"},
                    JOptionPane.YES_OPTION);
            switch (dirChoice) {
                case 0:
                    input[1] = "north";
                    break;
                case 1:
                case -1:
                    break;
            }
        } else if (checkStringSouth(input) && !input[1].equals("south")) {
            int dirChoice = JOptionPane.showOptionDialog(new JFrame(),
                    "Did you mean to say south? ",
                    "Going south?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, new Object[]{"Yes", "No"},
                    JOptionPane.YES_OPTION);
            switch (dirChoice) {
                case 0:
                    input[1] = "south";
                    break;
                case 1:
                case -1:
                    break;
            }
        } else if (checkStringEast(input) && !input[1].equals("east")) {
            int dirChoice = JOptionPane.showOptionDialog(new JFrame(),
                    "Did you mean to say east? ",
                    "Going east?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, new Object[]{"Yes", "No"},
                    JOptionPane.YES_OPTION);
            switch (dirChoice) {
                case 0:
                    input[1] = "east";
                    break;
                case 1:
                case -1:
                    break;
            }
        } else if (checkStringWest(input) && !input[1].equals("west")) {
            int dirChoice = JOptionPane.showOptionDialog(new JFrame(),
                    "Did you mean to say west? ",
                    "Going west?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, new Object[]{"Yes", "No"},
                    JOptionPane.YES_OPTION);
            switch (dirChoice) {
                case 0:
                    input[1] = "west";
                    break;
                case 1:
                case -1:
                    break;
            }
        }
        changeRoom(isValidInput, input, attempt);
    }

    void guessOrGoBackInside(String ans) {
        if (ans.contains("guess")) {
            replaceGameWindowWithColorText("You've collected all the evidence you could find.\n" +
                    "Based on your expertise, make an informed decision on what type of " +
                    "ghost is haunting Amazon Hill?\n" +
                    "Here are all the possible ghosts:\n", Color.WHITE);
            ghosts.forEach(ghost -> appendToGameWindowsWithColorNoSound(ghost.getType() + "\n", Color.GREEN));
            appendToGameWindowsWithColorNoSound("Which Ghost do you think it is?\n", Color.WHITE);
        } else if (ans.contains("inside")) {
            replaceGameWindowWithColorText("You are back inside", Color.WHITE);
        } else {
            replaceGameWindowWithColorText("Invalid input, please decide whether you want to GUESS or go " +
                    "back INSIDE.\n", Color.WHITE);
        }
    }

    void userGuess(String ans) throws IOException, InterruptedException {
        replaceGameWindowWithColorText("Good job gathering evidence, " + player.getName() + ".\nYou " +
                "guessed: " + ans + "\n", Color.WHITE);
        if (ans.equalsIgnoreCase(currentGhost.getType())) {
            appendWithColoredText("You won!\n", Color.RED);
            appendWithColoredText(getGhostBackstory() + "\n", Color.WHITE);
            isGameRunning = false;
            playAgain(this);
        } else {
            if (guessCounter < 1) {
                appendWithColoredText("Unfortunately, the ghost you determined was incorrect. The correct " +
                        "ghost was \n" + currentGhost.toString() + "\nYou have been loaded into a new " +
                        "world. Good luck trying again.\n", Color.WHITE);
            }
            resetWorld();
        }
    }


    private void stopSound() {
        mp.pauseMusic();
        soundEffect.stopSoundEffect();
        walkEffect.stopSoundEffect();
        keyboardEffect.stopSoundEffect();
        paperFalling.stopSoundEffect();
        isSound = false;
    }


    public String normalizeText(String input) {
        List<String> northOptions = Arrays.asList("north", "up");
        List<String> southOptions = Arrays.asList("south", "down");
        List<String> eastOptions = Arrays.asList("east", "right");
        List<String> westOptions = Arrays.asList("west", "left");
        if (northOptions.contains(input.toLowerCase())) {
            return "north";
        }
        if (southOptions.contains(input.toLowerCase())) {
            return "south";
        }
        if (eastOptions.contains(input.toLowerCase())) {
            return "east";
        }
        if (westOptions.contains(input.toLowerCase())) {
            return "west";
        }
        return "";
    }

    public void changeRoom(boolean isValidInput, String[] input, int attemptCount) throws IOException {
        while (isValidInput) {
            String normalize = normalizeText(input[1]);
            try {
                if (world.getCurrentRoom().roomExits.containsKey(normalize)) {
                    player.setMostRecentExit(normalize);
                    world.setCurrentRoom(world.getCurrentRoom().roomExits.get(normalize));
                    isValidInput = false;
                    if (isSound) {
                        walkEffect.playSoundEffect();
                    }
                    Thread.sleep(1800);
                    narrateRooms(world.getCurrentRoom().getDescription(), Color.red);
                    updateCurrentRoom();
                } else {
                    replaceGameWindowWithColorText("You hit a wall. Try again.\n ", Color.RED);
                    attemptCount++;
                    if (attemptCount >= 2) {
                        appendToGameWindowsWithColorNoSound("\n", Color.WHITE);
                        openMap();
                        appendToGameWindowsWithColorNoSound("Where would you like to go?\n ", Color.WHITE);
                        setAttemptCount(0);
                    }
                }
                break;
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
        if (world.getCurrentRoom().getRoomMiniGhost() != null) {
            // displays the fight dialog as an option pane, with yes(0) = fight, no(1) = run, close (-1) = run
            int fightChoice = JOptionPane.showOptionDialog(new JFrame(),
                    "You have run into a " + world.getCurrentRoom().getRoomMiniGhost().getName() +
                            ". What will you do? [Fight/Run]\n",
                    "Combat!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"Fight", "Run"},
                    JOptionPane.YES_OPTION);
            appendToGameWindowsWithColorNoSound(runCombat(Integer.toString(fightChoice), this, player), Color.WHITE);
        }
    }

    private void openMap() throws IOException {
        jFrame.showMap();
    }


    private void addEvidenceToJournal() {
        if (!world.getCurrentRoom().getRoomEvidence().isEmpty()) {
            String journalEntry = (world.getCurrentRoom().getRoomTitle() + ": " +
                    world.getCurrentRoom().getRoomEvidence() + "(Automatically Logged)");
            player.setJournal(journalEntry);
        }
    }


    void writeEntryInJournal(String journalEntry) {
        if (journalEntry.equals("no")) {
            appendWithColoredText("Journal Closed.\n", Color.WHITE);
        } else if (journalEntry.equalsIgnoreCase("yes")) {
            replaceGameWindowWithColorText("Your entry:\n ", Color.WHITE);
        } else {
            appendWithColoredText("Invalid Journal entry. Please look/show again to document again.\n", Color.WHITE);
        }
    }

    void inputEntryInJournal(String journalEntry) {
        player.setJournal(journalEntry);
        replaceGameWindowWithColorText("Entry Saved!", Color.YELLOW);
    }

    private void printJournal() throws BadLocationException {
        String ghostEmoji = "\uD83D\uDC7B ";
        String houseEmoji = "\uD83C\uDFE0";
        jFrame.setTextBoxJournal(divider + "\n", Color.WHITE);
        jFrame.appendTextColorAndDisplayJournal(player + "\n", Color.PINK);
        String formatted = ghostEmoji + "Possible Ghosts " + ghostEmoji;
        jFrame.appendTextColorAndDisplayJournal(formatted, Color.GREEN);
        jFrame.appendTextColorAndDisplayJournal(ghosts.toString() + "\n", Color.GREEN);
        formatted = houseEmoji + " Rooms visited " + houseEmoji;
        jFrame.appendTextColorAndDisplayJournal(formatted, Color.PINK);
        jFrame.appendTextColorAndDisplayJournal(player.getRoomsVisited() + "\n", Color.YELLOW);
        jFrame.appendTextColorAndDisplayJournal(divider + "\n", Color.pink);
    }


    void populateGhostList(ClassLoader cl) {
        this.setGhosts(XMLParser.populateGhosts(XMLParser.readXML(resourcePath + "Ghosts", cl), "ghost"));
    }

    void populateMiniGhostList(ClassLoader cl) {
        this.setMiniGhosts(XMLParser.populateMiniGhosts(XMLParser.readXML(resourcePath + "Ghosts", cl), "minighost"));
    }

    void populateItemsList(ClassLoader cl) {
        Map<String, List<? extends Items>> submitted = XMLParser.populateItems(XMLParser.readXML(resourcePath + "Items", cl), "item");
        this.setItems(submitted);
    }

    Ghost getRandomGhost() {
        int index = r.nextInt(ghosts.size());
        return ghosts.get(index);
    }

    private void assignRandomEvidenceToMap() {
        try {
            //for each evidence from monster, get rooms from world.gamemap equivalent to the number of evidences.
            for (int i = 0; i < currentGhost.getEvidence().size(); i++) {
                // Success condition
                boolean addedEvidence = false;

                // Loop while no success
                while (!addedEvidence) {
                    Room x = getRandomRoomFromWorld();
                    if (x.getRoomEvidence().equals("")) {
                        x.setRoomEvidence(currentGhost.getEvidence().get(i));
                        addedEvidence = true;
                    }
                }

            }
        } catch (NullPointerException e) {
            appendToGameWindowsWithColorNoSound("The data given is empty, cannot perform function", Color.pink);
        }
    }

    private void assignRandomMiniGhostToMap() {
        try {
            //for each minighost, get rooms from world.gamemap equivalent to the number of evidences.
            for (MiniGhost miniGhost : miniGhosts) {
                // Success condition
                boolean addedMiniGhost = false;

                // Loop while no success
                while (!addedMiniGhost) {
                    Room x = getRandomRoomFromWorld();
                    if (x.getRoomMiniGhost() == (null)) {
                        x.setRoomMiniGhost(miniGhost);
                        addedMiniGhost = true;
                    }
                }

            }
        } catch (NullPointerException e) {
            appendToGameWindowsWithColorNoSound("There is no minighost to add to the room.\n", Color.pink);
        }
    }

    private void assignRandomItemsToMap() {
        try {
            for (int i = 0; i < items.get("items").size(); i++) {
                // Success condition
                boolean addedItem = false;

                // Loop while no success
                while (!addedItem) {
                    Room x = getRandomRoomFromWorld();
                    if (x.getRoomItems().size() <= 2) {
                        x.addItemToRoom(items.get("items").get(i));
                        addedItem = true;
                    }
                }

            }
            for (int i = 0; i < items.get("weapons").size(); i++) {
                // Success condition
                boolean addedItem = false;

                // Loop while no success
                while (!addedItem) {
                    Room x = getRandomRoomFromWorld();
                    if (x.getRoomItems().size() <= 2) {
                        x.addItemToRoom(items.get("weapons").get(i));
                        addedItem = true;
                    }
                }

            }
        } catch (NullPointerException e) {
            appendToGameWindowsWithColorNoSound("There is no minighost to add to the room.\n", Color.pink);
        }
    }

    private Room getRandomRoomFromWorld() {
        int index = r.nextInt(world.gameMap.size());
        return world.gameMap.get(index);
    }

    private void checkIfRoomVisited() {
        if (!player.getRoomsVisited().contains(world.getCurrentRoom().getRoomTitle())) {
            player.addToRoomsVisited(world.getCurrentRoom().getRoomTitle());
        }
    }

    // Getters / Setters


    Player getPlayer() {
        return player;
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    List<Ghost> getGhosts() {
        return ghosts;
    }

    List<MiniGhost> getMiniGhosts() {
        return miniGhosts;
    }

    void setGhosts(List<Ghost> ghosts) {
        this.ghosts = ghosts;
    }

    void setMiniGhosts(List<MiniGhost> miniGhosts) {
        this.miniGhosts = miniGhosts;
    }


    private void setItems(Map<String, List<? extends Items>> items) {
        this.items = items;
    }

    private void setWeapon(List<Weapon> weapons) {
        this.weapons = weapons;
    }

    Ghost getCurrentGhost() {
        return currentGhost;
    }

    void setCurrentGhost(Ghost ghost) {
        this.currentGhost = ghost;
    }

    World getWorld() {
        return world;
    }

    void setWorld(World world) {
        this.world = world;
    }

    private String getGhostBackstory() {
        return currentGhost.getBackstory();
    }

    private boolean userAbleToExit() {
        // Is player currently in lobby? Has user visited any other rooms? Is so size of roomsVisited would be greater than 1
        if (!world.getCurrentRoom().getRoomTitle().equals("Lobby")) {
            appendToGameWindowsWithColorNoSound("You can only exit from Lobby.\n", Color.WHITE);
            return false;
        }
        if (player.getRoomsVisited().size() == 1) {
            appendToGameWindowsWithColorNoSound("You must visit more than one room to exit.\n", Color.WHITE);
            return false;
        }
        return true;
    }

    private void resetWorld() throws IOException, InterruptedException {
        //resets world and adds a new ghost. guessCounter is incremented with a maximum allowable guesses
        // set at 2.
        guessCounter++;
        if (guessCounter <= 1) {
            removeAllEvidenceFromWorld();
            setCurrentGhost(getRandomGhost());
            assignRandomEvidenceToMap();
            player.resetPlayer();
            jFrame.setControllerFlag();
        } else {
            String formatted = "Sorry, you've made too many incorrect guesses. GAME OVER.";
            appendToGameWindowsWithColorNoSound(formatted, Color.YELLOW);
            isGameRunning = false;
            playAgain(this);
        }
    }

    private void removeAllEvidenceFromWorld() {
        for (Room room : world.gameMap) {
            if (!room.getRoomEvidence().isEmpty()) {
                room.setRoomEvidence("");
            }
        }
    }

    private void resetGame() {
        removeAllEvidenceFromWorld();
        setCurrentGhost(getRandomGhost());
        assignRandomEvidenceToMap();
        player.resetPlayer();
        jFrame.setControllerFlag();
    }

    private void playAgain(Game game) throws IOException, InterruptedException {
        int playGameAgain = JOptionPane.showOptionDialog(new JFrame(),
                "Do you want to play again? ",
                "PLAY AGAIN?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{"Yes", "No"},
                JOptionPane.YES_OPTION);

        if (JOptionPane.YES_OPTION == playGameAgain) {
            String[] response = {"1"};
            game.resetGame();
            game.intro(response);
            String [] name = {getPlayer().getName()};
            game.createPlayer(name);
        }else{
            JOptionPane.showMessageDialog(new JFrame(),
            "Thank you for playing our game!",
            "GOODBYE!",
            JOptionPane.INFORMATION_MESSAGE);
            jFrame.quitGame();
        }
    }

    private boolean checkIfHasAllEvidenceIsInJournal() {
        boolean hasAllEvidence = true;
        // grab characteristics of currentGhost
        ArrayList<String> evidence = currentGhost.getEvidence();
        // grab contents of journal
        // make everything in journal lower case
        // grab list of last word of ghost evidence which should be the noun we are looking for
        // for each noun in list of nouns see if its in journal
        for (String e : evidence) {
            String nounToLookFor = e.substring(e.lastIndexOf(" ") + 1);
            if (!player.getJournal().toString().toLowerCase().contains(nounToLookFor.toLowerCase())) {
                hasAllEvidence = false;
                break;
            }
        }
        return hasAllEvidence;
    }

    // Used to add narration by appending to the GUI without removing any currently displayed text
    public void appendWithColoredText(String input, Color color) {

        if (isSound) {
            keyboardEffect.playSoundEffect();
        }
        try {
            jFrame.appendTextColorAndDisplay(input, color);
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
        keyboardEffect.stopSoundEffect();
    }

    // Add narration to the GUI by removing all prior text added
    public void replaceGameWindowWithColorText(String input, Color color) {
        if (isSound) {
            keyboardEffect.playSoundEffect();
        }
        jFrame.setTextBox(input, color);
        keyboardEffect.stopSoundEffect();
    }

    // Removes all prior text presented in GUI and displays new room narration
    private void narrateRooms(String input, Color color) {

        if (isSound) {
            paperFalling.playSoundEffect();
        }
        jFrame.setTextBox(input, color);
        paperFalling.stopSoundEffect();
    }

    // Appends to GUI without altering prior added text
    public void appendToGameWindowsWithColorNoSound(String input, Color color) {
        try {
            jFrame.appendTextColorAndDisplay(input, color);
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
    }

    /*Disabling original developer easter egg for security, but leaving it in the code.
    private void chrisIsCool() {
        String url_open = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
            mp.quitMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}