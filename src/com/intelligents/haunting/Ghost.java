package com.intelligents.haunting;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Ghost implements java.io.Serializable {
    private final String name;
    private final String type;
    private String background;
    private ArrayList<String> evidence;
    private String backstory;

    public String getBackstory() {
        return backstory;
    }

    // Constructors

    Ghost(String name, String type) {
        this.name = name;
        this.type = type;
    }

    Ghost(String name, String type, String background, ArrayList<String> evidence, String backstory) {
        this.name = name;
        this.type = type;
        this.background = background;
        this.evidence = evidence;
        this.backstory = backstory;
    }

    // Getters & Setters

    String getName() {
        return name;
    }

    String getType() {
        return type;
    }

    String getBackground() {
        return background;
    }

    ArrayList<String> getEvidence() {
        return evidence;
    }

    @Override
    public String toString() {
        return "\n" + getType() + ":\n" +
                "\tBackground: " + getBackground() + "\n" +
                "\tEvidence: " +
                getEvidence()
                        .stream()
                        .map(x -> x.substring(x.lastIndexOf(" ") + 1))
                        .collect(Collectors.toList()) + "\n";
    }
}