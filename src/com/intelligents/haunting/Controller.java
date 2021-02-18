package com.intelligents.haunting;

import java.io.IOException;

public class Controller {


    private boolean introScreen;
    private boolean nameSet;
    private boolean readyToGuess;
    private boolean loadedGame = false;
    private Game game;

    public Controller(Game game) {
        introScreen = true;
        nameSet = false;
        readyToGuess = false;
        this.game = game;
    }

    public void kickoffResponse(String[] response, String promptToUser) throws IOException, InterruptedException {
        // Get what chapter the user wants
        if (introScreen) {
            game.intro(response);
            if (response[0].equals("1") || response[0].equals("4")) {
                introScreen = false;
                if (response[0].equals("4")) loadedGame = true;
            }
        // Gain the users name
        } else if (!introScreen && !nameSet && !loadedGame) {
            game.createPlayer(response);
            nameSet = true;
        // If the user is trying to exit, get if they want to guess or stay inside
        } else if (promptToUser.contains("GUESS")){
            if (response[0].contains("guess")) {
                readyToGuess = true;
            }
            game.guessOrGoBackInside(response[0]);
        // Player has indicated that they are ready to guess
        } else if (readyToGuess) {
            game.userGuess(response[0],game);
        // Player has option to write in journal or not
        } else if (promptToUser.equals("Would you like to document anything in your journal? [Yes/No]\n") || promptToUser.equals("Invalid Journal entry. Please look/show again to document again.\n")) {
            game.writeEntryInJournal(response[0]);
        } else if (promptToUser.equals("Your entry:\n ")) {
            game.inputEntryInJournal(response[0]);
        } else {
            game.processInput(true, response, game.getAttemptCount());
        }
    }


}
