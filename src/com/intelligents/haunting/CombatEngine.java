package com.intelligents.haunting;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class CombatEngine {

    public static String runCombat(String userChoice, Game game) throws IOException {
        String result = "";
        // Switches based on button - yes button value is 0, no is 1, close window is -1
        switch (userChoice) {
            case "0":
                userChoice = "fight";
                break;
            case "1": case "-1":
                userChoice = "run";
                break;
            default:
                userChoice = "run";
                break;
        }
        if (userChoice.equals("fight")) {
            boolean inFight = true;
            while (inFight) {
                String fightResult = mortalCombat(game);
                if (fightResult.contains("invalid") || fightResult.contains("hoping")) {
                    //output result message and loop again
                    game.appendWithColoredText(fightResult + "\n", Color.white);
                } else if (fightResult.contains("dissipates") || fightResult.contains("whence")) {
                    game.getWorld().getCurrentRoom().setRoomMiniGhost(null);
                    result = fightResult;
                    inFight = false;
                } else {
                    result = fightResult;
                    inFight = false;
                    game.changeRoom(true, invertPlayerRoom(game.getPlayer().getMostRecentExit()), 0);
                }
            }
        }
        if (userChoice.equals("run")) {
            result = "Frightened to the point of tears, you flee back the way you came.";
            game.changeRoom(true, invertPlayerRoom(game.getPlayer().getMostRecentExit()), 0);
        }
        return result;
    }

    private static String mortalCombat(Game game) {
        showStatus(game);
        return processChoice(game);
    }

    private static void showStatus(Game game) {
        game.appendWithColoredText("\n\nCombat commencing...\n", Color.WHITE);
    }

    private static String processChoice(Game game) {
        MiniGhost battleGhost = game.getWorld().getCurrentRoom().getRoomMiniGhost();
        String fightChoice = (String) JOptionPane.showInputDialog(new JFrame(),
                "Choose your action: \n" +
                        "1 - Swing Iron Bar!\n" +
                        "2 - Sweat on it!\n" +
                        "3 - Punch it!\n" +
                        "4 - Run!\n",
                "Combat!",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"1", "2", "3", "4"},
                "1");
        // This catches cancel and close buttons
        if (fightChoice == null) {
            fightChoice = "4";
        }
        String result;
        switch (fightChoice) {
            case "1":
                result = "\n\nYou swing the iron bar, and the " + battleGhost.getName() + " dissipates.\n";
                break;
            case "2":
                result = "\n\nYou collect an impressive amount of sweat from your body " +
                        "and throw it at the " + battleGhost.getName() + ".\nWhile gross, the " +
                        "extreme salt content in your perspiration banishes the " +
                        battleGhost.getName() + " back to whence it came.\n";
                break;
            case "3":
                result = "\n\nYou punch at the " + battleGhost.getName() + " , but your hand passes right through.\n" +
                        "What were you hoping to achieve?\n";
                break;
            case "4":
                result = "\n\nYou think better about your choices, and decide to flee back the way you came.\n";
                break;
            default:
                result = "\n\nThat is an invalid option, please pick 1-4.\n";
                break;
        }
        return result;
    }

    private static String[] invertPlayerRoom(String mostRecentExit) {
        String[] opposite = new String[]{"go", null};
        switch (mostRecentExit) {
            case "east":
                opposite[1] = "west";
                break;
            case "north":
                opposite[1] = "south";
                break;
            case "south":
                opposite[1] = "north";
                break;
            // default case is west, which will make the player go east in case most recent exit is null from just starting
            default:
                opposite[1] = "east";
                break;
        }
        return opposite;
    }
}