package com.intelligents.haunting;

public class Weapon {

    private String name;
    private String description;
    private int damagePoints;

    Weapon(String name, String description, int damagePoints) {
        this.name = name;
        this.description = description;
        this.damagePoints = damagePoints;
    }

    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public int getDamagePoints() {
        return this.damagePoints;
    }
}
