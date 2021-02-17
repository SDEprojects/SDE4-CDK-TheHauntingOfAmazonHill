package com.intelligents.haunting;

public class Weapon extends Items{
    private int damagePoints;

    Weapon(String name, String description, int damagePoints) {
        super(name, "weapon", description);
        this.damagePoints = damagePoints;
    }

    public int getDamagePoints() {
        return this.damagePoints;
    }

}
