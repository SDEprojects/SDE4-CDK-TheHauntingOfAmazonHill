package com.intelligents.haunting;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class CombatEngine {
    private static Items fists = new Weapon("Fists", "Decent for a fist fight; not much help against ghosts.", 10);

    public static String runCombat(String userChoice, Game game, Player player) throws IOException, InterruptedException {
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
                String fightResult = mortalCombat(game, player);
                if (fightResult.contains("invalid") || fightResult.contains("hoping")) {
                    //output result message and loop again
                    game.appendWithColoredText(fightResult + "\n", Color.white);
                } else if (fightResult.contains("defeated")) {
                    game.getWorld().getCurrentRoom().setRoomMiniGhost(null);
                    result = fightResult;
                    inFight = false;
                } else if (fightResult.contains("lost")) {
                    return fightResult;
                } else {
                    result = fightResult;
                    inFight = false;
                    game.changeRoom(true, invertPlayerRoom(game.getPlayer().getMostRecentExit()), 0);
                }
            }
        }
        if (userChoice.equals("run")) {
            player.playerTakesDamage(10);
            result = "\nFrightened to the point of tears, you flee back the way you came." +
                    "Before you can make your narrow escape, the ghost scratches you and deals 10 points of damage.\n";
            game.changeRoom(true, invertPlayerRoom(game.getPlayer().getMostRecentExit()), 0);
        }
        return result;
    }

    private static String mortalCombat(Game game, Player player) {
        showStatus(game);
        return processChoice(game, player);
    }

    private static void showStatus(Game game) {
        game.appendWithColoredText("\n\nCombat commencing...\n", Color.WHITE);
    }

    private static String processChoice(Game game, Player player) {
        Items optionOneItem = Optional.ofNullable(player.getSpecificWeapon("Iron-Bar"))
            .orElse(fists);
        Items optionThreeItem = Optional.ofNullable(player.getSpecificWeapon("Sword"))
                .orElse(fists);
        MiniGhost battleGhost = game.getWorld().getCurrentRoom().getRoomMiniGhost();

        while (battleGhost.getHitPoints() > 0 && player.getPlayerHitPoints() > 0) {
            String fightChoice = (String) JOptionPane.showInputDialog(new JFrame(),
                    "Choose your action: \n" +
                            "1 - Swing " + optionOneItem.getName() + "!\n" +
                            "2 - Sweat on it!\n" +
                            "3 - Jab it with your " + optionThreeItem.getName() + "!\n" +
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
                    battleGhost.lowerHitPoints(((Weapon) optionOneItem).getDamagePoints());
                    result = "\n\nYou swing your " + optionOneItem.getName() + ", and the " + battleGhost.getName() + " dissipates, but reappears behind you.\n";
                    break;
                case "2":
                    battleGhost.lowerHitPoints(25);
                    player.playerTakesDamage(250);
                    result = "\n\nYou collect an impressive amount of sweat from your body " +
                            "and throw it at the " + battleGhost.getName() + ".\nWhile gross, the " +
                            "extreme salt content in your perspiration deals some damage to the " +
                            battleGhost.getName() + ", but in turn, dehydrating yourself took some major damage too.\n";
                    break;
                case "3":
                    battleGhost.lowerHitPoints(((Weapon) optionThreeItem).getDamagePoints());
                    if (optionThreeItem.getName().equals("Sword")) result = "\n\nYou jab your " + optionThreeItem.getName() + " at the " + battleGhost.getName() + ", and your " + optionThreeItem.getName() +  " passes right through. " +
                            "But something in the " +  optionThreeItem.getName() + " lights up like magic, forcing the " + battleGhost.getName() + " to dissipate forever.\n";
                    else result = "\n\nYou jab your " + optionThreeItem.getName() + " at the " + battleGhost.getName() + ", and your " + optionThreeItem.getName() +  " passes right through.\n";
                    break;
                case "4":
                    player.playerTakesDamage(10);
                    result = "\n\nYou think better about your choices, and decide to flee back the way you came.\n" +
                            "Before you can make your narrow escape, the ghost scratches you and deals 10 points of damage.\n";
                    break;
                default:
                    result = "\n\nThat is an invalid option, please pick 1-4.\n";
                    break;
            }

            // Break out of while loop if player runs
            if (result.contains("better about your choices")) return result;

            // Get player and ghost HP
            String playerHP = String.valueOf(player.getPlayerHitPoints());
            String ghostHP = String.valueOf(battleGhost.getHitPoints());
            if (player.getPlayerHitPoints() <= 0) playerHP = "DEAD";
            if (battleGhost.getHitPoints() <= 0) ghostHP = "DEAD";
            game.replaceGameWindowWithColorText(result +
                    "\n\nYour HP: " + playerHP +
                    "\n\nGhost HP: " + ghostHP, Color.WHITE);
        }
        if (player.getPlayerHitPoints() > 0) return "\n\nYou have defeated the " + battleGhost.getName() + "!";
        else if (player.getPlayerHitPoints() <= 0 && battleGhost.getHitPoints() <= 0) return "\n\nAt the cost of your own life, you defeat the ghost. Still, you have lost the game.";
        else return "\n\nBecause you lost all of your HP, you have lost the game. Too bad, so sad.";
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