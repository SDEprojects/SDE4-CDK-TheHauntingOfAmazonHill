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
import java.util.regex.Pattern;

import static com.intelligents.haunting.CombatEngine.runCombat;

public class Game implements java.io.Serializable {
    private final String resourcePath;
    private final ClassLoader cl;
    private World world;
    private List<Ghost> ghosts = new ArrayList<>();
    private List<MiniGhost> miniGhosts = new ArrayList<>();
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
        setCurrentGhost(getRandomGhost());
        assignRandomEvidenceToMap();
        assignRandomMiniGhostToMap();
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
            quickNarrateFormatted(fileReader.fileReader(resourcePath, "introText", cl), Color.RED);
            simpleOutputInlineSetting(fileReader.fileReader(resourcePath, "settingTheScene", cl), Color.WHITE);
            simpleOutputInlineSetting("\n" + "Thank you for choosing to play The Haunting of Amazon Hill. " +
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
                quickNarrateFormatted("Loading game!!!", Color.YELLOW);
                simpleOutputInlineSetting("\n\nWelcome back, " + player.getName() + "!" +
                        "\nYou are currently in the " + currentRoom + ".", Color.GREEN);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            simpleOutputInlineSetting("Invalid selection , please enter 1.", Color.pink);
        }
    }

    void createPlayer(String[] nameInput) {
        updateCurrentRoom();

        player.setName(nameInput[0]);

        String formatted = "If you're new to the game type help for assistance.";
        quickNarrateFormatted(formatted, Color.CYAN);

        formatted = "\nGood luck to you, " + player.getName() + "!";
        narrateNoNewLine(formatted, Color.white);
        simpleOutputInlineSetting("\n\n" + currentLoc, Color.YELLOW);
        simpleOutputInlineSetting("\n" + moveGuide, Color.YELLOW);

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


    void processInput(boolean isValidInput, String[] input, int attempt) {
        updateCurrentRoom();
        checkIfRoomVisited();
        if (input.length > 2) {
            simpleOutputInlineSetting("You cannot type more than 2 commands!Try again:\n ", Color.WHITE);
        } else {
            try {
                switch (input[0]) {
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
                        quickNarrateFormatted(fileReader.fileReader(resourcePath, "Rules", cl), Color.white);
                        break;
                    case "open":
                        openMap();
                        break;
                    //Displays room contents/evidence
                    case "look":
                    case "view":
                    case "show":
                        narrateNoNewLine(divider + "\n", Color.WHITE);
                        updateCurrentRoom();

                        if (world.getCurrentRoom().getRoomEvidence().isEmpty()) {
                            narrateNoNewLine("Currently there are no items in "
                                    + world.getCurrentRoom().getRoomTitle() + "\n\n", Color.WHITE);
                        } else {
                            addEvidenceToJournal();
                            narrateNoNewLine("You look and notice: " + world.getCurrentRoom().getRoomEvidence() + "\n\n", Color.WHITE);
                            narrateNoNewLine("Evidence logged into your journal.\n", Color.WHITE);
                        }
                        narrateNoNewLine(divider + "\n", Color.WHITE);
                        break;
                    case "write":
                        quickNarrateFormatted("Would you like to document anything in your journal? [Yes/No]\n", Color.WHITE);
                        break;
                    //Allows user to leave if more than one room has been input into RoomsVisited
                    case "exit":
                        if (userAbleToExit()) {
                            // In order to win, user has to have correct evidence and guessed right ghost
                            if (!checkIfHasAllEvidenceIsInJournal()) {
                                quickNarrateFormatted("It seems your journal does not have all of the evidence needed to determine the ghost." +
                                        " Would you like to GUESS the ghost anyway or go back INSIDE?\n", Color.WHITE);
                            } else {
                                quickNarrateFormatted("It seems like you could be ready to determine the ghost." +
                                        " Would you like to GUESS the ghost or go back INSIDE to continue exploring?\n", Color.WHITE);
                            }
                            narrateNoNewLine(divider + "\n", Color.WHITE);
                            break;
                        }
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
                }
            } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
                narrateNoNewLine("Make sure to add a verb e.g. 'move', 'go', 'open', 'read' then a noun e.g. 'north', 'map', 'journal'.\n", Color.WHITE);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        return;
    }

    void guessOrGoBackInside(String ans) {
        if (ans.contains("guess")) {
            quickNarrateFormatted("You've collected all the evidence you could find.\n" +
                    "Based on your expertise, make an informed decision on what type of " +
                    "ghost is haunting Amazon Hill?\n" +
                    "Here are all the possible ghosts:\n", Color.WHITE);
            ghosts.forEach(ghost -> simpleOutputInlineSetting(ghost.getType() + "\n", Color.GREEN));
            simpleOutputInlineSetting("Which Ghost do you think it is?\n", Color.WHITE);
        } else if (ans.contains("inside")) {
            quickNarrateFormatted("You are back inside", Color.WHITE);
        } else {
            quickNarrateFormatted("Invalid input, please decide whether you want to GUESS or go back INSIDE.\n", Color.WHITE);
        }
    }

    void userGuess(String ans) {
        quickNarrateFormatted("Good job gathering evidence, " + player.getName() + ".\nYou guessed: " + ans + "\n", Color.WHITE);
        if (ans.equalsIgnoreCase(currentGhost.getType())) {
            narrateNoNewLine("You won!\n", Color.RED);
            narrateNoNewLine(getGhostBackstory() + "\n", Color.WHITE);
            isGameRunning = false;
        } else {
            if (guessCounter < 1) {
                narrateNoNewLine("Unfortunately, the ghost you determined was incorrect. The correct ghost was \n"
                        + currentGhost.toString() + "\nYou have been loaded into a new world. Good luck trying again.\n", Color.WHITE);
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
                    quickNarrateFormatted("You hit a wall. Try again:\n ", Color.RED);
                    attemptCount++;
                    if (attemptCount >= 2) {
                        simpleOutputInlineSetting("\n", Color.WHITE);
                        openMap();
                        simpleOutputInlineSetting("Where would you like to go?\n ", Color.WHITE);
                    }
                }
                break;
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
        if (world.getCurrentRoom().getRoomMiniGhost() != null) {
            // displays the fight dialog as an option pane, with yes(0) = fight, no(1) = run, close (-1) = run
            int fightChoice = JOptionPane.showOptionDialog(new JFrame(), "You have run into a " + world.getCurrentRoom().getRoomMiniGhost().getName() + ". What will you do? [Fight/Run]\n", "Combat!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Fight", "Run"}, JOptionPane.YES_OPTION);
            simpleOutputInlineSetting(runCombat(Integer.toString(fightChoice), this), Color.WHITE);
        }
    }

    private void openMap() throws IOException {
        jFrame.showMap();
    }


    private void addEvidenceToJournal() {
        if (!world.getCurrentRoom().getRoomEvidence().isEmpty()) {
            String journalEntry = (world.getCurrentRoom().getRoomTitle() + ": " + world.getCurrentRoom().getRoomEvidence() + "(Automatically Logged)");
            player.setJournal(journalEntry);
        }
    }


    void writeEntryInJournal(String journalEntry) {
        if (journalEntry.equals("no")) {
            narrateNoNewLine("Journal Closed.\n", Color.WHITE);
        } else if (journalEntry.equalsIgnoreCase("yes")) {
            quickNarrateFormatted("Your entry:\n ", Color.WHITE);
        } else {
            narrateNoNewLine("Invalid Journal entry. Please look/show again to document again.\n", Color.WHITE);
        }
    }

    void inputEntryInJournal(String journalEntry) {
        player.setJournal(journalEntry);
        quickNarrateFormatted("Entry Saved!", Color.YELLOW);
    }

    private void printJournal() {
        quickNarrateFormatted(divider + "\n", Color.WHITE);
        narrateNoNewLine(player + "\n", Color.PINK);
        String formatted = "Possible Ghosts ";
        simpleOutputInlineSetting(formatted, Color.GREEN);
        narrateNoNewLine(ghosts.toString() + "\n", Color.GREEN);
        formatted = " Rooms visited ";
        simpleOutputInlineSetting(formatted, Color.PINK);
        narrateNoNewLine(player.getRoomsVisited() + "\n", Color.YELLOW);
        simpleOutputInlineSetting(divider + "\n", Color.pink);
    }

    public void openNewWindowJournalWithUpdatedInfo() {

        jFrame.textDisplayJournal.setText(
                player + "\n" +
                        "Possible Ghosts " +
                        ghosts.toString() + "\n" +
                        " Rooms visited " +
                        player.getRoomsVisited() + "\n"
        );
    }

    void populateGhostList(ClassLoader cl) {
        this.setGhosts(XMLParser.populateGhosts(XMLParser.readXML(resourcePath + "Ghosts", cl), "ghost"));
    }

    void populateMiniGhostList(ClassLoader cl) {
        this.setMiniGhosts(XMLParser.populateMiniGhosts(XMLParser.readXML(resourcePath + "Ghosts", cl), "minighost"));
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
            simpleOutputInlineSetting("The data given is empty, cannot perform function", Color.pink);
        }
    }

    private void assignRandomMiniGhostToMap() {
        try {
            //for each minighost, get rooms from world.gamemap equivalent to the number of evidences.
            for (int i = 0; i < miniGhosts.size(); i++) {
                // Success condition
                boolean addedMiniGhost = false;

                // Loop while no success
                while (!addedMiniGhost) {
                    Room x = getRandomRoomFromWorld();
                    if (x.getRoomMiniGhost() == (null)) {
                        x.setRoomMiniGhost(miniGhosts.get(i));
                        addedMiniGhost = true;
                    }
                }

            }
        } catch (NullPointerException e) {
            simpleOutputInlineSetting("There is no minighost to add to the room.\n", Color.pink);
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
            simpleOutputInlineSetting("You can only exit from Lobby.\n", Color.WHITE);
            return false;
        }
        if (player.getRoomsVisited().size() == 1) {
            simpleOutputInlineSetting("You must visit more than one room to exit.\n", Color.WHITE);
            return false;
        }
        return true;
    }

    private void resetWorld() {
        //resets world and adds a new ghost. guessCounter is incremented with a maximum allowable guesses
        // set at 2.
        guessCounter++;
        if (guessCounter <= 1) {
            removeAllEvidenceFromWorld();
            setCurrentGhost(getRandomGhost());
            assignRandomEvidenceToMap();
            player.resetPlayer();
        } else {
            String formatted = "Sorry, you've made too many incorrect guesses. GAME OVER.";
            simpleOutputInlineSetting(formatted, Color.YELLOW);
            isGameRunning = false;
        }
    }

    private void removeAllEvidenceFromWorld() {
        for (Room room : world.gameMap) {
            if (!room.getRoomEvidence().isEmpty()) {
                room.setRoomEvidence("");
            }
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
    public void narrateNoNewLine(String input, Color color) {

        if (isSound) {
            keyboardEffect.playSoundEffect();
        }
        try {
            jFrame.setTextColorAndDisplay(input, color);
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
        keyboardEffect.stopSoundEffect();
    }

    // Add narration to the GUI by removing all prior text added
    public void quickNarrateFormatted(String input, Color color) {
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
    public void simpleOutputInlineSetting(String input, Color color) {
        try {
            jFrame.setTextColorAndDisplay(input, color);
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